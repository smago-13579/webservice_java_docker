package ru.yandex.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.dto.*;
import ru.yandex.dto.ShopUnit;
import ru.yandex.exception.ItemNotFoundException;
import ru.yandex.exception.RequestErrorException;
import ru.yandex.models.Statistic;
import ru.yandex.models.Unit;
import ru.yandex.repository.ShopUnitRepository;
import ru.yandex.repository.StatisticRepository;
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
    private ShopUnitRepository unitRepository;
    private StatisticRepository statRepository;
    private JdbcTemplate jdbcTemplate;
    private ShopServiceUtils serviceUtils;
    private DateTimeFormatter formatter;

    @Autowired
    public ShopServiceImpl(ShopUnitRepository unitRepository,
                           StatisticRepository statRepository,
                           JdbcTemplate jdbcTemplate) {
        this.unitRepository = unitRepository;
        this.statRepository = statRepository;
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
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS goods.statistic (\n" +
                "id bigserial primary key,\n" +
                "uid varchar(50) references goods.unit on delete cascade,\n" +
                "type varchar(20) not null,\n" +
                "name varchar(100) not null,\n" +
                "date timestamp not null,\n" +
                "price bigint check(price >= 0),\n" +
                "parent_id varchar(50));");
    }

    private void updateParentDate(Unit unit, LocalDateTime date) {
        String parentId = unit.getParentId() == null ? null : unit.getParentId().getUid();
        unit.setDate(date);
        unitRepository.save(unit);
        statRepository.insert(unit.getUid(), unit.getType(), unit.getName(),
                date, unit.getPrice(), parentId);

        while (unit.getParentId() != null) {
            unit = unit.getParentId();
            unit.setDate(date);
            unitRepository.save(unit);

            parentId = unit.getParentId() == null ? null : unit.getParentId().getUid();
            statRepository.insert(unit.getUid(), unit.getType(), unit.getName(),
                    date, unit.getPrice(), parentId);
        }
    }

    @Override
    public void create(ShopUnitImport unit, LocalDateTime date) {
        unitRepository.insertOrUpdate(unit.getId(), unit.getType().toString(), unit.getName(),
                date, unit.getPrice(), unit.getParentId());
        statRepository.insert(unit.getId(), unit.getType().toString(), unit.getName(),
                date, unit.getPrice(), unit.getParentId());

        if (unit.getParentId() != null) {
            Optional<Unit> optUnit = unitRepository.findById(unit.getParentId());

            if (optUnit.isEmpty()) {
                throw new ItemNotFoundException("Item not found");
            }
            updateParentDate(optUnit.get(), date);
        }
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
        Optional<Unit> optUnit = unitRepository.findById(id);

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
                .children(serviceUtils.mapEntityToShopUnit(unit.getChildren(), unit.getUid()))
                .build();
    }

    @Override
    public void delete(String id) {
        if (id == null) {
            throw new RequestErrorException("Validation Failed");
        }
        Optional<Unit> optUnit = unitRepository.findById(id);

        if (optUnit.isEmpty()) {
            throw new ItemNotFoundException("Item not found");
        }
        unitRepository.delete(optUnit.get());
    }

    @Override
    public ShopUnitStatisticResponse getSales(String date) {
        try {
            LocalDateTime endDate = LocalDateTime.parse(date, formatter);
            LocalDateTime startDate = endDate.minusDays(1);
            List<Unit> units = unitRepository.findAllByDateInterval(startDate, endDate);

            ArrayList<ShopUnitStatisticUnit> statUnits = units.stream().map(u -> {
                ShopUnitStatisticUnit unit = ShopUnitStatisticUnit.builder()
                        .type(ShopUnitType.valueOf(u.getType()))
                        .id(u.getUid())
                        .name(u.getName())
                        .date(u.getDate().format(formatter))
                        .price(u.getPrice())
                        .build();

                if (u.getParentId() != null) {
                    unit.setParentId(u.getParentId().getUid());
                }
                return unit;
            }).collect(Collectors.toCollection(ArrayList::new));

            return new ShopUnitStatisticResponse(statUnits);
        } catch (RuntimeException e) {
            throw new RequestErrorException("Validation Failed");
        }
    }

    @Override
    public ShopUnitStatisticResponse getStatistic(String uid, String sDate, String eDate) {
        List<Statistic> stats;

        if (sDate == null || eDate == null) {
            stats = statRepository.findAllByUid(uid);
        } else {
            LocalDateTime startDate = LocalDateTime.parse(sDate, formatter);
            LocalDateTime endDate = LocalDateTime.parse(eDate, formatter);
            stats = statRepository.findAllByUidWithDate(uid, startDate, endDate);
        }

        if (stats == null || stats.isEmpty()) {
            throw new ItemNotFoundException("Item not found");
        }

        ArrayList<ShopUnitStatisticUnit> statUnits = stats.stream()
                .filter(s -> s != null && s.getUid() != null)
                .map(u -> ShopUnitStatisticUnit.builder()
                        .type(ShopUnitType.valueOf(u.getType()))
                        .id(u.getUid().getUid())
                        .name(u.getName())
                        .date(u.getDate().format(formatter))
                        .parentId(u.getParentId())
                        .price(u.getPrice())
                        .build()
                ).collect(Collectors.toCollection(ArrayList::new));

        return new ShopUnitStatisticResponse(statUnits);
    }

    @Override
    public List<Unit> findAll() {
        Iterable<Unit> iterable = unitRepository.findAll();
        return StreamSupport.stream(iterable.spliterator(), false)
                .collect(Collectors.toList());
    }
}
