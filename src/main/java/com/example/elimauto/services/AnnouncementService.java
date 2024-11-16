package com.example.elimauto.services;

import com.example.elimauto.models.Announcement;
import com.example.elimauto.models.Image;
import com.example.elimauto.repositories.AnnouncementRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnnouncementService {
    private final AnnouncementRepository announcementRepository;
    private ImageService imageService;

    @Autowired
    public AnnouncementService(AnnouncementRepository announcementRepository, ImageService imageService) {
        this.announcementRepository = announcementRepository;
        this.imageService = imageService;
    }

    public List<Announcement> listAnnouncements(String title) {
        if (title != null) return announcementRepository.findByTitle(title);
        return announcementRepository.findAll();
    }

    public void saveAnnouncement(Announcement announcement, List<MultipartFile> files) throws IOException {
        if (files.size() > 20) {
            throw new IllegalArgumentException("Нельзя загрузить более 20 изображений.");
        }

        Announcement savedAnnouncement = announcementRepository.save(announcement);

        boolean isFirstImage = true;
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                // Передаем объект `savedAnnouncement` при сохранении изображения
                imageService.saveImage(file, isFirstImage, savedAnnouncement);
                isFirstImage = false;
            }
        }

        if (!savedAnnouncement.getImages().isEmpty()) {
            Long previewImageId = null;
            for (Image image : savedAnnouncement.getImages()) {
                if (image.isPreviewImage()) {
                    previewImageId = image.getId();
                    break; // Находим первое изображение с isPreviewImage = true и выходим из цикла
                }
            }
            savedAnnouncement.setPreviewImageId(previewImageId);
            announcementRepository.save(savedAnnouncement);
        }


        // Логирование информации о сохранённом объявлении
        log.info("Saving new Announcement. Title: {}; Author: {}",
                announcement.getTitle(),
                announcement.getAuthor());

        // Обновляем объявление, если были добавлены изображения
        announcementRepository.save(savedAnnouncement);
    }

    private Image toImageEntity(MultipartFile file) throws IOException {
        Image image = new Image();
        image.setName(file.getName());
        image.setOriginalFileName(file.getOriginalFilename());
        image.setContentType(file.getContentType());
        image.setSize(file.getSize());
        return image;
    }

    public void deleteAnnouncements(Long id) {
        if (announcementRepository.existsById(id)) {
            announcementRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Announcement not found");
        }
    }

    public Announcement getAnnouncementById(Long id) {
        return announcementRepository.findById(id).orElse(null);
    }
}
