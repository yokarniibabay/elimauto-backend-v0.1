package com.example.elimauto.DTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageDTO {
    private Long id;
    private String path; // Путь или URL для загрузки изображения
    private String contentType; // Тип контента (например, image/jpeg)
}
