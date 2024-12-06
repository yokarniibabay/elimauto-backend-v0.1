package com.example.elimauto.models;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Data
public class AnnouncementUpdateRequest {
    private String title;
    private String description;
    private Double price;
    private String city;
    private List<String> orderedImageIds;
    private List<MultipartFile> newImages;
    private List<Long> imagesToDelete;
    private String previewImageId;
}
