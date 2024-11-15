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
        // Проверка на максимальное количество файлов
        if (files.size() > 20) {
            throw new IllegalArgumentException("Нельзя загрузить более 20 изображений.");
        }

        // Сохраняем объявление в базе данных перед сохранением изображений
        Announcement announcementFromDB = announcementRepository.save(announcement);

        // Логика сохранения изображений
        boolean isFirstImage = true;
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                // Сохраняем изображение через ImageService
                Image image = imageService.saveImage(file, isFirstImage);
                image.setAnnouncement(announcementFromDB); // Привязываем изображение к объявлению
                imageService.saveImage(file, isFirstImage); // сохраняем через ImageService
                isFirstImage = false;
            }
        }

        // Логирование информации о сохранённом объявлении
        log.info("Saving new Announcement. Title: {}; Author: {}",
                announcement.getTitle(),
                announcement.getAuthor());

        // Обновляем объявление, если были добавлены изображения
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
