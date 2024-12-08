package com.example.elimauto.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "generation", schema = "carbase")
public class Generation {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "year-start")
    private Short yearStart;

    @Column(name = "year-stop")
    private Short yearStop;

    @Column(name = "is-restyle")
    private boolean isRestyle;

    @Column(name = "model_id")
    private String modelId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="model_id", referencedColumnName="id", insertable=false, updatable=false)
    private Model model;

    @OneToMany(mappedBy = "generation", fetch = FetchType.LAZY)
    private List<Configuration> configurations = new ArrayList<>();
}
