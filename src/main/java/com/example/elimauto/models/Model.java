package com.example.elimauto.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "model", schema = "carbase")
public class Model {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "cyrillic-name")
    private String cyrillicName;

    @Column(name = "class")
    private String carClass;

    @Column(name = "year-from")
    private Short yearFrom;

    @Column(name = "year-to")
    private Short yearTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="mark_id", referencedColumnName="id", insertable=false, updatable=false)
    private Mark mark;

    @OneToMany(mappedBy = "model", fetch = FetchType.LAZY)
    private List<Generation> generations = new ArrayList<>();
}
