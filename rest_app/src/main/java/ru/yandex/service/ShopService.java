package ru.yandex.service;

import ru.yandex.dto.*;
import ru.yandex.models.Unit;

import java.time.LocalDateTime;
import java.util.List;

public interface ShopService {
    void create(ShopUnitImport unit, LocalDateTime date);
    void createAll(ShopUnitImportRequest importRequest);
    ShopUnit getUnit(String id);
    void delete(String id);
    ShopUnitStatisticResponse getSales(String date);
    ShopUnitStatisticResponse getStatistic(String id, String startDate, String endDate);
    List<Unit> findAll();
}
