package ru.yandex.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.dto.*;
import ru.yandex.dto.ShopUnit;
import ru.yandex.exception.ItemNotFoundException;
import ru.yandex.exception.RequestErrorException;
import ru.yandex.models.Unit;
import ru.yandex.repository.ShopUnitRepository;
import ru.yandex.service.utils.ShopServiceUtils;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ShopServiceImpl implements ShopService {
    private ShopUnitRepository repository;
    private JdbcTemplate jdbcTemplate;
    private ShopServiceUtils serviceUtils;
    private DateTimeFormatter formatter;

    @Autowired
    public ShopServiceImpl(ShopUnitRepository repository, JdbcTemplate jdbcTemplate) {
        this.repository = repository;
        this.jdbcTemplate = jdbcTemplate;
        this.serviceUtils = new ShopServiceUtils();
        this.formatter = serviceUtils.getFormatter();
    }

    @PostConstruct
    public void init() {
        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS goods;");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS goods.unit (\n" +
                "id bigserial,\n" +
                "uid varchar(50) primary key,\n" +
                "type varchar(20) not null,\n" +
                "name varchar(100) not null,\n" +
                "date timestamp not null,\n" +
                "price bigint check(price >= 0),\n" +
                "parent_id varchar(50) references goods.unit on delete cascade);");
    }

    private void updateParentDate(Unit unit, LocalDateTime date) {
        unit.setDate(date);
        repository.save(unit);

        while (unit.getParentId() != null) {
            unit = unit.getParentId();
            unit.setDate(date);
            repository.save(unit);
        }
    }

    @Override
    public void create(ShopUnitImport unit, LocalDateTime date) {
        repository.insertOrUpdate(unit.getId(), unit.getType().toString(), unit.getName(),
                date, unit.getPrice(), unit.getParentId());

        if (unit.getParentId() != null) {
            Optional<Unit> optUnit = repository.findById(unit.getParentId());

            if (optUnit.isEmpty()) {
                throw new ItemNotFoundException("Item not found");
            }
            updateParentDate(optUnit.get(), date);
        }
        //TODO need update children?
//        if (ShopUnitType.CATEGORY == unit.getType()) {
//            List<Unit> units = repository.findAllByParentId(unit.getId());
//            units = serviceUtils.findAll(units);
//            repository.saveAll(units);
//        }
    }

    @Transactional
    @Override
    public void createAll(ShopUnitImportRequest importRequest) {
        try {
            ArrayList<ShopUnitImport> items = importRequest.getItems();
            LocalDateTime date = LocalDateTime.parse(importRequest.getUpdateDate(), formatter);

            if (items == null || items.isEmpty() || items.stream().anyMatch(s -> s.getType() == null
                    || s.getId() == null || s.getName() == null)
                    || items.stream().filter(i -> i.getType() == ShopUnitType.OFFER)
                            .anyMatch(i -> i.getPrice() == null)) {
                throw new RuntimeException();
            }
            items.forEach(unit -> create(unit, date));
        } catch (RuntimeException e) {
            throw new RequestErrorException("Validation Failed");
        }
    }

    @Transactional
    @Override
    public ShopUnit getUnit(String id) {
        if (id == null) {
            throw new RequestErrorException("Validation Failed");
        }
        Optional<Unit> optUnit = repository.findById(id);

        if (optUnit.isEmpty()) {
            throw new ItemNotFoundException("Item not found");
        }
        Unit unit = optUnit.get();

        if (ShopUnitType.CATEGORY.toString().equals(unit.getType())) {
                unit.setPrice(serviceUtils.findAveragePrice(unit.getChildren()));
        }
        String uid = unit.getParentId() == null ? null : unit.getParentId().getUid();
        return ShopUnit.builder()
                .type(ShopUnitType.valueOf(unit.getType()))
                .id(unit.getUid())
                .name(unit.getName())
                .date(unit.getDate().format(formatter))
                .parentId(uid)
                .price(unit.getPrice())
                .children(serviceUtils.mapEntityToJson(unit.getChildren(), unit.getUid()))
                .build();
    }

    @Override
    public void delete(String id) {
        if (id == null) {
            throw new RequestErrorException("Validation Failed");
        }
        Optional<Unit> optUnit = repository.findById(id);

        if (optUnit.isEmpty()) {
            throw new ItemNotFoundException("Item not found");
        }
        repository.delete(optUnit.get());
    }

    @Override
    public List<Unit> findAll() {
        Iterable<Unit> iterable = repository.findAll();
        return StreamSupport.stream(iterable.spliterator(), false)
                .collect(Collectors.toList());
    }
}
