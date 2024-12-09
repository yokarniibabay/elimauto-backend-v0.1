package com.example.elimauto.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MarkDTO {
    private String id;
    private String name;
    private String cyrillicName;
    private boolean popular;
    private String country;
    private List<ModelDTO> models;
}
