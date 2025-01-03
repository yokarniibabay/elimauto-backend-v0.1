package com.example.elimauto.models;

import com.example.elimauto.elimauto.consts.AnnouncementStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "announcements")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "images")
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    private String makeName;
    private String modelName;

    private String makeId;
    private String modelId;
    private String generationId;
    private String configurationId;
    private String bodyType;
    private Integer year;
    private String color;
    private String driveType;
    private float engineCapacity;
    private String transmissionType;
    private Integer mileage;
    private Integer horsePower;
    private String characteristicsId;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private Double price;

    @Column(name = "city")
    private String city;

    @Column(name = "author")
    private String authorName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,
    mappedBy = "announcement",
            orphanRemoval = true)
    @JsonManagedReference
    @OrderBy("displayOrder ASC")
    private List<Image> images = new ArrayList<>();

    @Column(name = "preview_image_id")
    private Long previewImageId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AnnouncementStatus status = AnnouncementStatus.PENDING;

    @Column(name = "status_comment")
    private String statusComment;

    @Column(nullable = false)
    private Long views = 0L;

    @PrePersist
    private void init() {
        createdAt = LocalDateTime.now();
    }

    public void addImageToAnnouncement(Image image){
        this.images.add(image);
        image.setAnnouncement(this);
    }

    public void removeImage(Image image) {
        images.remove(image);
        image.setAnnouncement(null);
    }
}