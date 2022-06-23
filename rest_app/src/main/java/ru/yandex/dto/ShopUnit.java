package ru.yandex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.ArrayList;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShopUnit {

    @JsonProperty("type")
    ShopUnitType type;

    @JsonProperty("id")
    String id;

    @JsonProperty("name")
    String name;

    @JsonProperty("date")
    String date;

    @Nullable
    @JsonProperty("parentId")
    String parentId;

    @Nullable
    @JsonProperty("price")
    Long price;

    @Nullable
    @JsonProperty("children")
    ArrayList<ShopUnit> children;
}
