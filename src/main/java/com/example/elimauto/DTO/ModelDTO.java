package com.example.elimauto.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModelDTO {
    private String id;
    private String name;
    private String cyrillicName;
    private String carClass;
    private Short yearFrom;
    private Short yearTo;
}
