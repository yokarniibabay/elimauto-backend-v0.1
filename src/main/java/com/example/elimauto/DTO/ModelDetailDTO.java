package com.example.elimauto.DTO;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ModelDetailDTO extends ModelDTO{
    private String markName;

    public ModelDetailDTO() {}

    public ModelDetailDTO(String id, String name, String cyrillicName, String carClass, Short yearFrom, Short yearTo, String markName) {
        super(id, name, cyrillicName, carClass, yearFrom, yearTo);
        this.markName = markName;
    }
}
