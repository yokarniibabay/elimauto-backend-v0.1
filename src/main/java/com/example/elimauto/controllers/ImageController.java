package com.example.elimauto.controllers;

import com.example.elimauto.models.Announcement;
import com.example.elimauto.models.Image;
import com.example.elimauto.repositories.ImageRepository;
import com.example.elimauto.services.AnnouncementService;
import com.example.elimauto.services.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {
    private final ImageRepository imageRepository;
    private final AnnouncementService announcementService;

    @Autowired
    private ImageService imageService;

    @GetMapping("/announcement/{id}/images")
    public ResponseEntity<List<String>> getImages(@PathVariable Long id) {
        List<String> base64Images = imageService.getBase64ImagesByAnnouncementId(id);
        return ResponseEntity.ok(base64Images);
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getImage(@PathVariable Long id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        String base64Image = "data:" + image.getContentType() + ";base64," + image.getData();

        return ResponseEntity.ok(base64Image);
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<String> getImageById(@PathVariable Long id) {
        Optional<Image> imageOptional = imageService.getImageById(id);

        return imageOptional
                .map(image -> new ResponseEntity<>(
                        "data:" + image.getContentType() + ";base64," + image.getData(),
                        HttpStatus.OK
                ))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/preview/{announcementId}")
    public ResponseEntity<String> getPreviewImage(@PathVariable Long announcementId) {
        // Получаем объявление
        Announcement announcement = announcementService.getAnnouncementById(announcementId);

        if (announcement.getPreviewImageId() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Превью изображение не установлено для данного объявления");
        }

        // Получаем изображение по previewImageId
        Optional<Image> previewImage = imageService.getImageById(announcement.getPreviewImageId());

        return previewImage.map(image -> ResponseEntity.ok(image.getData()))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Изображение для превью не найдено"));

        // Возвращаем Base64 строку изображения
    }
}
