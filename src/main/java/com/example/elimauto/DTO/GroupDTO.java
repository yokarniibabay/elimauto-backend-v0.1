package com.example.elimauto.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GroupDTO {
    private String groupName; // Название группы (например, A-Класс, B-Класс и т.д.)
    private String configurationId; // ID конфигурации, связанный с группой
}