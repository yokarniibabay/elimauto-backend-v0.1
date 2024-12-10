package com.example.elimauto.DTO;

import lombok.Data;

@Data
public class ModificationDTO {
    private String complectationId;
    private Integer offersPriceFrom;
    private Integer offersPriceTo;
    private String groupName;
}
