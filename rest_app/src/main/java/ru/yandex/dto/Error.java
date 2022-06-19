package ru.yandex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Error {

    @JsonProperty("code")
    Integer code;

    @JsonProperty("message")
    String message;
}
