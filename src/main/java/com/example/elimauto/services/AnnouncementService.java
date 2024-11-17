package com.example.elimauto.services;

import com.example.elimauto.models.Announcement;
import com.example.elimauto.models.Image;
import com.example.elimauto.repositories.AnnouncementRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * Метод для получения списка объявлений с опциональной фильтрацией по названию.
     */
    public List<Announcement> listAnnouncements(String title) {
        if (title != null && !title.isEmpty()) {
            return announcementRepository.findByTitle(title);
        }
        return announcementRepository.findAll();
    }

    /**
     * Сохранение нового объявления вместе с прикрепленными изображениями.
     */
    public void saveAnnouncement(Announcement announcement, List<MultipartFile> files) throws IOException {
        if (files.size() > 20) {
            throw new IllegalArgumentException("Нельзя загрузить более 20 изображений.");
        }

        List<Image> savedImages = new ArrayList<>(); // Список для хранения сохраненных изображений
        boolean isFirstImage = true;

        try {
            // 1. Сначала сохраняем объявление
            Announcement savedAnnouncement = announcementRepository.save(announcement);

            // 2. Затем сохраняем изображения и устанавливаем связь с сохраненным объявлением
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    Image savedImage = imageService.saveImage(file, savedAnnouncement, isFirstImage); // Передаем уже сохраненное объявление
                    savedImages.add(savedImage);
                    isFirstImage = false;
                }
            }
            // Устанавливаем превью-изображение (можно оптимизировать, если нужно)
            Long previewImageId = savedImages.stream()
                    .filter(Image::isPreviewImage)
                    .map(Image::getId)
                    .findFirst()
                    .orElse(null);

            savedAnnouncement.setPreviewImageId(previewImageId);
            announcementRepository.save(savedAnnouncement);


            log.info("Saving new Announcement. Title: {}; Author: {}",
                    announcement.getTitle(),
                    announcement.getAuthor());

        } catch (IOException | IllegalArgumentException e) {
            // Обработка ошибок: удаление уже сохраненных изображений (если нужно)
            for (Image image : savedImages) {
                imageService.deleteImage(image);
            }
            throw new IOException("Ошибка при сохранении объявления или изображений: " + e.getMessage(), e);
        }
    }

    public void deleteAnnouncement(Long id) throws IOException {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Announcement not found"));

        // Удаляем изображения, привязанные к объявлению
        List<Image> images = announcement.getImages();
        for (Image image : images) {
            imageService.deleteImage(image);
        }

        // Удаляем само объявление
        announcementRepository.delete(announcement);
        log.info("Deleted announcement with ID: {}", id);
    }

    public Announcement getAnnouncementById(Long id) {
        return announcementRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Announcement not found"));
    }
}
