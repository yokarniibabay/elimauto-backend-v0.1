package com.example.elimauto.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ConfigurationGroupDTO {
    private String groupName;
    private List<ConfigurationDTO> configurations;
}
