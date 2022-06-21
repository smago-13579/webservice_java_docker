package ru.yandex.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(schema = "goods", name = "unit")
public class Unit {
    @Column(name = "id")
    private Long id;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid")
    private String uid;

    @Column(name = "type")
    private String type;

    @Column(name = "name")
    private String name;

    @Column(name = "date")
    private Timestamp date;

    @Column(name = "price")
    private Long price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Unit parentId;

    @OneToMany(mappedBy = "parentId",
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Unit> children;

    @Transient
    private String parentIdValue;
}
