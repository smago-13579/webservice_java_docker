package ru.yandex.service;

import ru.yandex.dto.ShopUnit;
import ru.yandex.dto.ShopUnitImport;
import ru.yandex.dto.ShopUnitImportRequest;
import ru.yandex.models.Unit;

import java.time.LocalDateTime;
import java.util.List;

public interface ShopService {
    void create(ShopUnitImport unit, LocalDateTime date);
    void createAll(ShopUnitImportRequest importRequest);
    ShopUnit getUnit(String id);
    void delete(String id);
    List<Unit> findAll();
}
