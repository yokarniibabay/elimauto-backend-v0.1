package com.example.elimauto.services;

import com.example.elimauto.DTO.AnnouncementDTO;
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
import java.util.stream.Collectors;

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

    public List<AnnouncementDTO> getAllAnnouncements() {
        return announcementRepository.findAll().stream()
                .map(announcement -> {
                    AnnouncementDTO dto = new AnnouncementDTO();
                    dto.setId(announcement.getId());
                    dto.setTitle(announcement.getTitle());
                    dto.setDescription(announcement.getDescription());
                    dto.setPrice(announcement.getPrice());
                    dto.setCity(announcement.getCity());
                    dto.setAuthorName(
                            announcement.getAuthor() != null
                                    ? announcement.getAuthor().getName()
                                    : "Anonymous"
                    );
                    dto.setPreviewImageUrl(
                            announcement.getPreviewImageId() != null
                                    ? "/images/" + announcement.getPreviewImageId()
                                    : null
                    );
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public void createAnnouncement(String title,
                                   String description,
                                   double price,
                                   String city,
                                   List<MultipartFile> files) throws IOException {
        if (files.size() > 20) {
            throw new IllegalArgumentException("Нельзя загрузить более 20 изображений.");
        }

        List<Image> savedImages = new ArrayList<>();
        boolean isFirstImage = true;

        // Создаем объявление
        Announcement announcement = new Announcement();
        announcement.setTitle(title);
        announcement.setDescription(description);
        announcement.setPrice(price);
        announcement.setCity(city);

        try {
            // Получаем текущего пользователя из контекста безопасности.  Добавлена проверка на null!
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!(principal instanceof User)) {
                throw new IllegalStateException("Не удалось получить текущего пользователя.  Проверьте аутентификацию.");
            }
            User currentUser = (User) principal;


            // Устанавливаем текущего пользователя как автора.  Проверка не нужна, так как мы уже проверили выше
            announcement.setAuthor(currentUser);

            // Сохраняем объявление
            Announcement savedAnnouncement = announcementRepository.save(announcement);

            // Сохраняем изображения
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    Image savedImage = imageService.saveImage(file, savedAnnouncement, isFirstImage);
                    savedImages.add(savedImage);
                    isFirstImage = false;
                }
            }

            // Устанавливаем preview-изображение
            Long previewImageId = savedImages.stream()
                    .filter(Image::isPreviewImage)
                    .map(Image::getId)
                    .findFirst()
                    .orElse(null);
            savedAnnouncement.setPreviewImageId(previewImageId);

            // Сохраняем объявление с обновленным previewImageId
            announcementRepository.save(savedAnnouncement);
        } catch (IOException | IllegalStateException e) { // Изменено на IllegalStateException
            // Удаляем загруженные изображения при ошибке
            for (Image image : savedImages) {
                imageService.deleteImage(image);
            }
            throw e;
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
