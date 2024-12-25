package com.example.elimauto.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConfigurationGroupProjection {
    private String id;
    private String bodyType;
    private String configurationName;
    private String groupName; // может быть null
}
