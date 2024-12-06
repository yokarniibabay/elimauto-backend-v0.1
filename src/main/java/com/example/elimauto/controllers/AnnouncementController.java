package com.example.elimauto.controllers;

import com.example.elimauto.DTO.AnnouncementDTO;
import com.example.elimauto.consts.AnnouncementStatus;
import com.example.elimauto.models.Announcement;
import com.example.elimauto.models.AnnouncementUpdateRequest;
import com.example.elimauto.services.AnnouncementService;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/announcement")
@RequiredArgsConstructor
public class AnnouncementController {
    private final AnnouncementService announcementService;

    @GetMapping("/allApproved")
    public ResponseEntity<List<AnnouncementDTO>> getAllPublicAnnouncements() {
        log.info("Fetching all public announcements from the database.");
        List<AnnouncementDTO> announcements = announcementService.getAllApprovedAnnouncements();
        return ResponseEntity.ok(announcements);
    }

    @GetMapping("/all")
    public ResponseEntity<List<AnnouncementDTO>> getAllAnnouncements() {
        log.info("Fetching all announcements from the database.");
        List<AnnouncementDTO> announcements = announcementService.getAllAnnouncements();
        return ResponseEntity.ok(announcements);
    }

    @GetMapping("/author/{authorId}/announcements")
    public ResponseEntity<List<AnnouncementDTO>> getAnnouncementsByAuthor(@PathVariable Long authorId) {
        List<AnnouncementDTO> announcements = announcementService.getAnnouncementsByAuthorId(authorId);
        return ResponseEntity.ok(announcements);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnnouncementDTO> getAnnouncement(@PathVariable Long id)
            throws AccessDeniedException {
        try {
            AnnouncementDTO dto = announcementService.getAnnouncementById(id);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<AnnouncementDTO> getPublicAnnouncement(@PathVariable Long id) {
        AnnouncementDTO dto = announcementService.getPublicAnnouncementById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/private/{id}")
    public ResponseEntity<AnnouncementDTO> getPrivateAnnouncement(@PathVariable Long id) throws AccessDeniedException {
        AnnouncementDTO dto = announcementService.getAnnouncementById(id); // Используется полная проверка
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createAnnouncement(
            @ModelAttribute AnnouncementUpdateRequest updateRequest,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        try {
            announcementService.createAnnouncement(updateRequest, images);
            return ResponseEntity.status(HttpStatus.CREATED).body("Объявление создано успешно");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при сохранении объявления");
        }
    }

    @PostMapping("/edit/{id}")
    public ResponseEntity<String> editAnnouncement(
            @PathVariable Long id,
            @ModelAttribute AnnouncementUpdateRequest updateRequest) {
        try {
            log.info("Request received: /announcement/edit/{} with data: {}", id, updateRequest);
            announcementService.editAnnouncement(id, updateRequest);
            return ResponseEntity.ok("Объявление успешно обновлено.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Объявление с таким ID не найдено.");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("У вас нет прав для редактирования этого объявления.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Некорректные данные: " + e.getMessage());
        } catch (Exception e) {
            log.error("Ошибка при редактировании объявления", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Произошла ошибка при обновлении объявления.");
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

    @PostMapping("/increment-views/{id}")
    public ResponseEntity<Void> incrementViews(@PathVariable Long id) {
        announcementService.incrementViews(id);
        return ResponseEntity.ok().build();
    }

    /* MODERAROR ENDPOINTS*/

    @PostMapping("/approve/{id}")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<String> approveAnnouncement(@PathVariable Long id) {
        announcementService.updateAnnouncementStatus(id, AnnouncementStatus.APPROVED);
        return ResponseEntity.ok("Объявление одобрено.");
    }

    @PostMapping("/reject/{id}")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<String> rejectAnnouncement(@PathVariable Long id,
                                                     @RequestBody Map<String, String> requestBody) {
        String comment = requestBody.get("comment");

        if (comment == null || comment.isBlank()) {
            return ResponseEntity.badRequest().body("Комментарий обязателен.");
        }

        announcementService.rejectAnnouncement(id, comment);
        return ResponseEntity.ok("Объявление отклонено. Причина: " + comment);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<List<AnnouncementDTO>> getPendingAnnouncements() {
        List<Announcement> pendingAnnouncements = announcementService.getAnnouncementsByStatus(AnnouncementStatus.PENDING);
        List<AnnouncementDTO> announcementDTOs = pendingAnnouncements.stream()
                .map(announcementService::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(announcementDTOs);
    }
}