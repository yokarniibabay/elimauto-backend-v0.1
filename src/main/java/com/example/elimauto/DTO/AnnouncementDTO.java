package com.example.elimauto.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnnouncementDTO {
    private Long id;
    private String title;
    private String description;
    private double price;
    private String city;
    private String authorName;
    private String previewImageUrl; // URL превью изображения
}