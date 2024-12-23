package com.example.elimauto.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnnouncementUpdateRequest {
    private String makeId;
    private String modelId;
    private String generationId;
    private String configurationId;
    private String characteristicsId;
    private String bodyType;
    private Integer year;
    private String color;
    private String driveType;
    private Integer mileage;
    private Integer horsePower;
    private Float engineCapacity;
    private String transmissionType;
    private String description;
    private Double price;
    private String city;
    private List<String> orderedImageIds;
    private List<MultipartFile> newImages;
    private List<Long> imagesToDelete;
    private String previewImageId;
}
