package com.example.elimauto.models;

import com.example.elimauto.consts.AnnouncementStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    private Long previewImageId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

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
        image.setAnnouncement(this);
        images.add(image);
    }
}