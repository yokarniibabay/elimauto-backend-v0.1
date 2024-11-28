package com.example.elimauto.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
    private List<ImageDTO> images;
}