package com.example.elimauto.DTO;

import com.example.elimauto.consts.AnnouncementStatus;
import lombok.Data;

import java.util.List;

@Data
public class AnnouncementDTO {
    private Long id;
    private String title;
    private String description;
    private double price;
    private String city;
    private String authorName;
    private String authorNumber;
    private String previewImageUrl;
    private List<ImageDTO> images;
    private Long views;
    private AnnouncementStatus status;
    private String statusComment;

    private String makeName;
    private String modelName;
    private Integer year;
}