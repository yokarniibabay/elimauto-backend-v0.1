package com.example.elimauto.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecificationsEngineDetailsDTO {
    private String engineType;
    private String volumeLitres;
    private String horsePower;
}
