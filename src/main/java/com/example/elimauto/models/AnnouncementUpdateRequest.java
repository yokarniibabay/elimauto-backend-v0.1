package com.example.elimauto.models;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class AnnouncementUpdateRequest {
    private String title;       // Заголовок объявления
    private String description; // Описание объявления
    private Double price;       // Цена
    private String city;        // Город
    private List<MultipartFile> images; // Список изображений (если они изменяются)
}
