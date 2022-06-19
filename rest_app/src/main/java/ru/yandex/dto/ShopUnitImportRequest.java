package ru.yandex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;

@Data
public class ShopUnitImportRequest {

    @JsonProperty("items")
    ArrayList<ShopUnitImport> items;

    @JsonProperty("updateDate")
    String updateDate;
}
