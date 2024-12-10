package com.example.elimauto.DTO;

import lombok.Data;

@Data
public class GenerationDTO {
    private String id;
    private String name;
    private Short yearStart;
    private Short yearStop;
    private boolean isRestyle;
}
