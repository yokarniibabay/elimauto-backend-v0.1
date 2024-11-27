package com.example.elimauto.controllers;

import com.example.elimauto.DTO.AnnouncementDTO;
import com.example.elimauto.models.Announcement;
import com.example.elimauto.models.Image;
import com.example.elimauto.models.User;
import com.example.elimauto.services.AnnouncementService;

import com.example.elimauto.services.ImageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/announcement")
@RequiredArgsConstructor
public class AnnouncementController {
    private final AnnouncementService announcementService;
    private final ImageService imageService;


    @GetMapping
    public ResponseEntity<List<AnnouncementDTO>> getAllAnnouncements() {
        List<Announcement> announcements = announcementService.getAllAnnouncements();
        List<AnnouncementDTO> announcementDTOs = announcements.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(announcementDTOs);
    }

    private AnnouncementDTO convertToDto(Announcement announcement) {
        AnnouncementDTO dto = new AnnouncementDTO();
        dto.setId(announcement.getId());
        dto.setTitle(announcement.getTitle());
        dto.setDescription(announcement.getDescription());
        dto.setPrice(announcement.getPrice());
        dto.setCity(announcement.getCity());
        dto.setAuthorName(announcement.getAuthor() != null ? announcement.getAuthor().getName() : "Unknown"); // Обработка null
        Long previewImageId = announcement.getPreviewImageId();
        if (previewImageId != null) {
            Optional<Image> previewImage = imageService.getImageById(previewImageId);
            dto.setPreviewImage(previewImage.map(Image::isPreviewImage).orElse(false)); // Обработка null
        }
        return dto;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Announcement> getAnnouncementInfo(@PathVariable Long id) {
        Announcement announcement = announcementService.getAnnouncementById(id);
        if (announcement == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(announcement, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createAnnouncement(@RequestParam("title") String title,
                                                     @RequestParam("description") String description,
                                                     @RequestParam("price") double price,
                                                     @RequestParam("city") String city,
                                                     @RequestParam("files") MultipartFile[] files) {
        try {
            announcementService.createAnnouncement(title, description, price, city, Arrays.asList(files));
            return ResponseEntity.status(HttpStatus.CREATED).body("Объявление создано успешно");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при сохранении объявления");
        }
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAnnouncement(@PathVariable Long id) throws IOException {
        try {
            announcementService.deleteAnnouncement(id);
            return new ResponseEntity<>("Объявление удалено", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Объявление не найдено", HttpStatus.NOT_FOUND);
        }
    }
}