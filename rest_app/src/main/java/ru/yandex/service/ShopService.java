package ru.yandex.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.repository.ShopUnitRepository;

import javax.annotation.PostConstruct;

@Service
public class ShopService {
    private ShopUnitRepository repository;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public ShopService(ShopUnitRepository repository, JdbcTemplate jdbcTemplate) {
        this.repository = repository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    @Autowired
    public void init() {
        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS goods;");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS goods.unit (\n" +
                "id bigserial primary key,\n" +
                "uid varchar(50) not null unique,\n" +
                "name varchar(50) not null unique,\n" +
                "password varchar(100) not null);");
    }
}
