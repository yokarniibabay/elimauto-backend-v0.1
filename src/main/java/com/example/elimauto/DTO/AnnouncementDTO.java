package com.example.elimauto.DTO;

import com.example.elimauto.consts.AnnouncementStatus;
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
    private String authorNumber;
    private String previewImageUrl;
    private List<ImageDTO> images;
    private Long views;
    private AnnouncementStatus status;
    private String statusComment;
}