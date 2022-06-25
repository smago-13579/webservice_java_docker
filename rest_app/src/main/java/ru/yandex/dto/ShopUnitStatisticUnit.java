package ru.yandex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShopUnitStatisticUnit {

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
}
