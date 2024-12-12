package com.example.elimauto.DTO;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelDTO {
    private String id;
    private String name;
    private String cyrillicName;
    private String carClass;
    private Short yearFrom;
    private Short yearTo;
}
