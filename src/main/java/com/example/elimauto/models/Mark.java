package com.example.elimauto.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "mark", catalog = "carbase")
public class Mark {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "cyrillic-name")
    private String cyrillicName;

    @Column(name = "popular")
    private boolean popular;

    @Column(name = "country")
    private String country;

    @OneToMany(mappedBy = "mark", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Model> models = new ArrayList<>();
}
