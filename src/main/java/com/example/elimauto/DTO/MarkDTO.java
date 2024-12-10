package com.example.elimauto.DTO;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
public class MarkDTO {
    private String id;
    private String name;
    private String cyrillicName;
    private boolean popular;
    private String country;
    private List<ModelDTO> models;
}
