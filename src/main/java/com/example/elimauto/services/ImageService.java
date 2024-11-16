package com.example.elimauto.services;

import com.example.elimauto.models.Announcement;
import com.example.elimauto.models.Image;
import com.example.elimauto.repositories.ImageRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class ImageService {
    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Image saveImage(MultipartFile file, boolean isPreviewImage, Announcement announcement)
            throws IOException {
        Image image = new Image();
        image.setName(file.getName());
        image.setData(Base64.getEncoder().encodeToString(file.getBytes()));// Преобразуем в Base64
        image.setPreviewImage(isPreviewImage);
        if (image.isPreviewImage()) {
            announcement.setPreviewImageId(image.getId());
        }
        image.setContentType(file.getContentType());
        image.setOriginalFileName(file.getOriginalFilename());
        image.setSize(file.getSize());
        image.setAnnouncement(announcement); // Устанавливаем связь с объявлением

        return imageRepository.save(image);
    }

    public Optional<Image> getImageById(Long id) {
        return imageRepository.findById(id);
    }

    public List<String> getBase64ImagesByAnnouncementId(Long announcementId) {
        List<Image> images = imageRepository.findByAnnouncementId(announcementId);
        return images.stream()
                .map(image -> Base64.getEncoder().encodeToString(image.getData().getBytes())) // Преобразуем в Base64
                .toList();
    }

    public List<Image> getImagesByAnnouncementId(Long announcementId) {
        return imageRepository.findByAnnouncementId(announcementId);
    }
}
