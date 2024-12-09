package com.example.elimauto.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "model", catalog = "carbase")
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
    @JsonBackReference
    private Mark mark;

    @OneToMany(mappedBy = "model", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Generation> generations = new ArrayList<>();
}
