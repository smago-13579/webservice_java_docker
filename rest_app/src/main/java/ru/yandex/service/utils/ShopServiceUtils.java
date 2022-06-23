package ru.yandex.service.utils;

import ru.yandex.dto.ShopUnit;
import ru.yandex.dto.ShopUnitType;
import ru.yandex.models.Unit;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ShopServiceUtils {
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSS'Z'")
            .withZone(ZoneId.of("UTC"));

    public ArrayList<ShopUnit> mapEntityToJson(List<Unit> children, String uid) {
        if (children == null) {
            return null;
        }
        return children.stream().map(unit -> {
            ShopUnit shopUnit = new ShopUnit(ShopUnitType.valueOf(unit.getType()), unit.getUid(), unit.getName(),
                    unit.getDate().format(formatter), uid, unit.getPrice(), null);

            if (ShopUnitType.CATEGORY.toString().equals(unit.getType())) {
                shopUnit.setChildren(mapEntityToJson(unit.getChildren(), unit.getUid()));
                shopUnit.setPrice(findAveragePrice(unit.getChildren()));
            }
            return shopUnit;
        }).collect(Collectors.toCollection(ArrayList::new));
    }

    public Long findAveragePrice(List<Unit> units) {
        if (units == null) {
            return null;
        }
        List<Unit> allOffers = findAllOffers(units);

        if (allOffers.size() == 0) {
            return null;
        }
        return allOffers.stream().mapToLong(Unit::getPrice).sum() / allOffers.size();
    }

    private ArrayList<Unit> findAllOffers(List<Unit> units) {
        ArrayList<Unit> allUnits = new ArrayList<>();

        for (Unit unit : units) {
            if (ShopUnitType.CATEGORY.toString().equals(unit.getType())
                    && unit.getChildren() != null) {
                allUnits.addAll(findAllOffers(unit.getChildren()));
            } else {
                allUnits.add(unit);
            }
        }
        return allUnits;
    }

    public DateTimeFormatter getFormatter() {
        return formatter;
    }
}
