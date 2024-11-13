package com.example.elimauto.services;

import com.example.elimauto.models.Announcement;
import com.example.elimauto.models.Image;
import com.example.elimauto.repositories.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnnouncementService {
    private final AnnouncementRepository announcementRepository;

    public List<Announcement> listAnnouncements(String title) {
        if (title != null) return announcementRepository.findByTitle(title);
        return announcementRepository.findAll();
    }

    public void saveAnnouncement(Announcement announcement, List<MultipartFile> files) throws IOException {
        boolean isFirstImage = true;
        // Проверка на максимальное количество файлов
        if (files.size() > 20) {
            throw new IllegalArgumentException("Нельзя загрузить более 20 изображений.");
        }

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                Image image = toImageEntity(file);

                // Устанавливаем превью для первой загруженной картинки
                if (isFirstImage) {
                    image.setPreviewImage(true);
                    isFirstImage = false;
                }

                announcement.addImageToAnnouncement(image);
            }
        }

        log.info("Saving new Announcement. Title: {}; Author: {}",
                announcement.getTitle(),
                announcement.getAuthor());

        Announcement announcementFromDB = announcementRepository.save(announcement);

        // Устанавливаем ID превью-картинки для сохраненного объявления
        if (!announcementFromDB.getImages().isEmpty()) {
            announcementFromDB.setPreviewImageId(announcementFromDB.getImages().get(0).getId());
        }

        announcementRepository.save(announcementFromDB);
    }

    private Image toImageEntity(MultipartFile file) throws IOException {
            Image image = new Image();
            image.setName(file.getName());
            image.setOriginalFileName(file.getOriginalFilename());
            image.setContentType(file.getContentType());
            image.setSize(file.getSize());
            image.setBytes(file.getBytes());
            return image;
    }

    public void deleteAnnouncements(Long id) {
        announcementRepository.deleteById(id);
    }

    public Announcement getAnnouncementById(Long id) {
        return announcementRepository.findById(id).orElse(null);
    }
}
