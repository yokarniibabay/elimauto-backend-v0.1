package com.example.elimauto.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "configuration", schema = "carbase")
public class Configuration {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "doors-count")
    private Byte doorsCount;

    @Column(name = "body-type")
    private String bodyType;

    @Column(name = "configuration-name")
    private String configurationName;

    @Column(name = "generation_id")
    private String generationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="generation_id", referencedColumnName="id", insertable=false, updatable=false)
    private Generation generation;

    @OneToMany(mappedBy = "configuration", fetch = FetchType.LAZY)
    private List<Modification> modifications = new ArrayList<>();
}
