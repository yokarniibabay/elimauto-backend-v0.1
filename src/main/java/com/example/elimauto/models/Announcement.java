package com.example.elimauto.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "announcements")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "price")
    private double price;

    @Column(name = "city")
    private String city;

    @Column(name = "author")
    private String author;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,
    mappedBy = "announcement")
    private List<Image> images = new ArrayList<>();
    private Long previewImageId;
    private LocalDateTime createdAt;

    @PrePersist
    private void init() {
        createdAt = LocalDateTime.now();
    }

    public void addImageToAnnouncement(Image image){
        image.setAnnouncement(this);
        images.add(image);
    }
}