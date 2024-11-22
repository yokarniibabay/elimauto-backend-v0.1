package com.example.elimauto.services;

import com.example.elimauto.models.Announcement;
import com.example.elimauto.models.Image;
import com.example.elimauto.models.User;
import com.example.elimauto.repositories.AnnouncementRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnnouncementService {
    private final AnnouncementRepository announcementRepository;
    private final ImageService imageService;

    public List<Announcement> listAnnouncements(String title) {
        if (title != null && !title.isEmpty()) {
            return announcementRepository.findByTitle(title);
        }
        return announcementRepository.findAll();
    }

    public void saveAnnouncement(Announcement announcement, List<MultipartFile> files) throws IOException {
        if (files.size() > 20) {
            throw new IllegalArgumentException("Нельзя загрузить более 20 изображений.");
        }

        List<Image> savedImages = new ArrayList<>(); // Список для хранения сохраненных изображений
        boolean isFirstImage = true;

        try {
            // Получение текущего пользователя из контекста Spring Security
            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (currentUser == null) {
                throw new IllegalArgumentException("Текущий пользователь не найден.");
            }

            // Установка текущего пользователя как автора объявления
            announcement.setAuthor(currentUser);

            // Сохранение объявления
            Announcement savedAnnouncement = announcementRepository.save(announcement);

            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    Image savedImage = imageService.saveImage(file, savedAnnouncement, isFirstImage);
                    savedImages.add(savedImage);
                    isFirstImage = false;
                }
            }

            // Установка превью-изображения
            Long previewImageId = savedImages.stream()
                    .filter(Image::isPreviewImage)
                    .map(Image::getId)
                    .findFirst()
                    .orElse(null);
            savedAnnouncement.setPreviewImageId(previewImageId);

            // Сохранение объявления с обновленным previewImageId
            announcementRepository.save(savedAnnouncement);

            // Логирование
            log.info("Saving new Announcement. Title: {}; Author: {}",
                    announcement.getTitle(),
                    currentUser.getName());

        } catch (IOException | IllegalArgumentException e) {
            // Удаление изображений в случае ошибки
            for (Image image : savedImages) {
                imageService.deleteImage(image);
            }
            throw new IOException("Ошибка при сохранении объявления или изображений: " + e.getMessage(), e);
        }
    }

    public void deleteAnnouncement(Long id) throws IOException {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Announcement not found"));

        imageService.deleteImagesByAnnouncement(announcement);

        announcementRepository.delete(announcement);
        log.info("Deleted announcement with ID: {}", id);
    }

    public Announcement getAnnouncementById(Long id) {
        return announcementRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Announcement not found"));
    }
}
