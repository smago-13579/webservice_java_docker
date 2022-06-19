package ru.yandex.models;

import lombok.Data;
import ru.yandex.dto.ShopUnitType;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Data
@Entity
@Table(schema = "goods", name = "unit")
public class Unit {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "type")
    private ShopUnitType type;

    @Column(name = "uid")
    private String uid;

    @Column(name = "name")
    private String name;

    @Column(name = "date")
    private Timestamp date;

    @Column(name = "price")
    private Long price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentId")
    private Unit parentId;

    @OneToMany(mappedBy = "parentId",
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Unit> children;
}
