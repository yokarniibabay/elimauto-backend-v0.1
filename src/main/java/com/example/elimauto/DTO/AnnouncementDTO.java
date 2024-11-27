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
    private String authorName; //  Имя автора, а не сам объект User
    private boolean isPreviewImage = false;

    public boolean isPreviewImage() {
        return isPreviewImage;
    }
}
