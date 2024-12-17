package com.example.elimauto.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "cities")
@Data
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name_ru", nullable = false)
    private String nameRu;

    @Column(name = "name_kk", nullable = false)
    private String nameKk;
}
