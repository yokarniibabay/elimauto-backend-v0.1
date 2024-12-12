package com.example.elimauto.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarkNameDTO {
    private String id;
    private String name;
    private String cyrillicName;
    private boolean popular;
}
