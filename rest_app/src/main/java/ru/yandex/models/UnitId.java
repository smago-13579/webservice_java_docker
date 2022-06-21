package ru.yandex.models;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Data
//@Entity
public class UnitId {
    @Column(name = "id")
    protected Long id;
}
