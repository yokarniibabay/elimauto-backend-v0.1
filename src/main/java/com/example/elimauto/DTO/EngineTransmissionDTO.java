package com.example.elimauto.DTO;

import lombok.Data;

@Data
public class EngineTransmissionDTO {
    private String engineCapacity;
    private String transmission;

    public EngineTransmissionDTO(String engineCapacity, String transmission) {
        this.engineCapacity = engineCapacity;
        this.transmission = transmission;
    }
}
