package ru.yandex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;

@Data
public class ShopUnitStatisticResponse {

    @JsonProperty("items")
    ArrayList<ShopUnitStatisticUnit> items;
}
