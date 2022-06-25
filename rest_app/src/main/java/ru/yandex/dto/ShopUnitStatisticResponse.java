package ru.yandex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;

@Data
@AllArgsConstructor
public class ShopUnitStatisticResponse {

    @JsonProperty("items")
    ArrayList<ShopUnitStatisticUnit> items;
}
