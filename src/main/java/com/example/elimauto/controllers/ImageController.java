package com.example.elimauto.controllers;

import com.example.elimauto.models.Announcement;
import com.example.elimauto.models.Image;
import com.example.elimauto.repositories.AnnouncementRepository;
import com.example.elimauto.repositories.ImageRepository;
import com.example.elimauto.services.AnnouncementService;
import com.example.elimauto.services.FileStorageService;
import com.example.elimauto.services.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImageController {
    private final ImageRepository imageRepository;
    private final AnnouncementRepository announcementRepository;
    private final AnnouncementService announcementService;
    private final FileStorageService fileStorageService;

    @Autowired
    private ImageService imageService;

    @GetMapping("/{announcementId}/images")
    public ResponseEntity<List<String>> getImages(@PathVariable Long id) {
        List<Image> images = imageService.getImagesByAnnouncementId(id);

        List<String> imageUrls = images.stream()
                .map(image -> "/api/images/" + image.getId()) // Генерация URL для каждого изображения
                .toList();

        return ResponseEntity.ok(imageUrls);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ByteArrayResource> getImage(@PathVariable Long id) throws IOException {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        Path path = fileStorageService.getStorageDirectory().resolve(image.getPath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getContentType()))
                .body(resource);
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<ByteArrayResource> getImageById(@PathVariable Long id) throws IOException {
        Optional<Image> imageOptional = imageService.getImageById(id);

        if (imageOptional.isPresent()) {
            Image image = imageOptional.get();
            Path imagePath = Paths.get(image.getPath());
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(imagePath));

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(image.getContentType()))
                    .body(resource);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/preview/{announcementId}")
    public ResponseEntity<ByteArrayResource> getPreviewImage(@PathVariable Long announcementId) throws IOException {
        // Находим объявление по ID
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));

        // Получаем ID превью-изображения
        Long previewImageId = announcement.getPreviewImageId();
        if (previewImageId == null) {
            throw new RuntimeException("No preview image set for this announcement");
        }

        // Находим изображение по ID
        Image previewImage = imageRepository.findById(previewImageId)
                .orElseThrow(() -> new RuntimeException("Preview image not found"));

        // Получаем путь к файлу
        Path path = fileStorageService.getStorageDirectory().resolve(previewImage.getPath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        // Возвращаем изображение с правильным content-type
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(previewImage.getContentType()))
                .body(resource);
    }
}
