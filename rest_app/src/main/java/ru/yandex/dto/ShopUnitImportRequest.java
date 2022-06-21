package ru.yandex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShopUnitImportRequest {

    @JsonProperty("items")
    ArrayList<ShopUnitImport> items;

    @JsonProperty("updateDate")
    String updateDate;
}
