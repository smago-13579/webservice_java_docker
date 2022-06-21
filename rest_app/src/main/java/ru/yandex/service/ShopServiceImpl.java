package ru.yandex.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.dto.ShopUnitImport;
import ru.yandex.dto.ShopUnitImportRequest;
import ru.yandex.dto.ShopUnitType;
import ru.yandex.exception.RequestErrorException;
import ru.yandex.models.Unit;
import ru.yandex.repository.ShopUnitRepository;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ShopServiceImpl implements ShopService {
    private ShopUnitRepository repository;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public ShopServiceImpl(ShopUnitRepository repository, JdbcTemplate jdbcTemplate) {
        this.repository = repository;
        this.jdbcTemplate = jdbcTemplate;
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
                "parent_id varchar(50) references goods.unit);");
    }

    @Override
    public void create(ShopUnitImport unit, Timestamp timestamp) {
        repository.insertOrUpdate(unit.getId(), unit.getType().toString(), unit.getName(),
                timestamp, unit.getPrice(), unit.getParentId());
    }

    @Transactional
    @Override
    public void createAll(ShopUnitImportRequest importRequest) {
        try {
            ArrayList<ShopUnitImport> items = importRequest.getItems();
            Timestamp ts = Timestamp.valueOf(LocalDateTime.parse(importRequest.getUpdateDate()));

            if (items == null || items.isEmpty() || items.stream().anyMatch(s -> s.getType() == null
                    || s.getId() == null || s.getName() == null)
                    || items.stream().filter(i -> i.getType() == ShopUnitType.OFFER)
                            .anyMatch(i -> i.getPrice() == null)) {
                throw new RuntimeException();
            }
            items.forEach(unit -> create(unit, ts));
        } catch (RuntimeException e) {
            throw new RequestErrorException("Validation Failed");
        }
    }



    @Override
    public List<Unit> findAll() {
        Iterable<Unit> iterable = repository.findAll();
        return StreamSupport.stream(iterable.spliterator(), false)
                .collect(Collectors.toList());
    }
}
