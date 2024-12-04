package com.example.elimauto.models;

import lombok.Data;


@Data
public class AnnouncementUpdateRequest {
    private String title;       // Заголовок объявления
    private String description; // Описание объявления
    private Double price;       // Цена
    private String city;        // Город
}
