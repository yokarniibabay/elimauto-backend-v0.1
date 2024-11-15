package com.example.elimauto.repositories;

import com.example.elimauto.models.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByAnnouncementId(Long announcementId);
}
