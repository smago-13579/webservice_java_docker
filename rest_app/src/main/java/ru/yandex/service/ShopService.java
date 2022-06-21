package ru.yandex.service;

import ru.yandex.dto.ShopUnitImport;
import ru.yandex.dto.ShopUnitImportRequest;
import ru.yandex.models.Unit;

import java.sql.Timestamp;
import java.util.List;

public interface ShopService {
    void create(ShopUnitImport unit, Timestamp timestamp);
    void createAll(ShopUnitImportRequest importRequest);
    List<Unit> findAll();
}
