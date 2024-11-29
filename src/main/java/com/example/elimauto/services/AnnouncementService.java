package com.example.elimauto.services;

import com.example.elimauto.DTO.AnnouncementDTO;
import com.example.elimauto.DTO.ImageDTO;
import com.example.elimauto.models.Announcement;
import com.example.elimauto.models.Image;
import com.example.elimauto.models.User;
import com.example.elimauto.repositories.AnnouncementRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final UserService userService;

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
                                    : "Неизвестный Автор"
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

    @Transactional
    public void createAnnouncement(String title,
                                   String description,
                                   double price,
                                   String city,
                                   List<MultipartFile> files) throws IOException {
        validateInputs(title, description, price, city, files);

        List<Image> savedImages = new ArrayList<>();
        boolean isFirstImage = true;

        Announcement announcement = new Announcement();
        announcement.setTitle(title);
        announcement.setDescription(description);
        announcement.setPrice(price);
        announcement.setCity(city);

        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                throw new IllegalStateException("Пользователь должен быть аутентифицирован");
            }

            announcement.setAuthor(currentUser);
            announcement.setAuthorName(currentUser.getName());
            log.info("Текущий пользователь: {}", announcement.getAuthor());

            Announcement savedAnnouncement = announcementRepository.save(announcement);

            imageService.saveImages(files, savedAnnouncement, savedImages);

            imageService.setPreviewImage(savedAnnouncement, savedImages);

            announcementRepository.save(savedAnnouncement);
            log.info("Создано объявление с ID: {}", savedAnnouncement.getId());

        } catch (IOException | IllegalStateException e) {
            log.error("Ошибка при создании объявления: {}", e.getMessage());
            imageService.rollbackSavedImages(savedImages);
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

    private void validateInputs(String title, String description, double price, String city, List<MultipartFile> files) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Название объявления не может быть пустым.");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Описание не может быть пустым.");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Цена должна быть положительной.");
        }
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("Город не может быть пустым.");
        }
        if (files.size() > 20) {
            throw new IllegalArgumentException("Нельзя загрузить более 20 изображений.");
        }
    }

    public AnnouncementDTO convertToDto(Announcement announcement) {
        AnnouncementDTO dto = new AnnouncementDTO();
        dto.setId(announcement.getId());
        dto.setTitle(announcement.getTitle());
        dto.setDescription(announcement.getDescription());
        dto.setPrice(announcement.getPrice());
        dto.setCity(announcement.getCity());

        dto.setAuthorName(announcement.getAuthor() != null ? announcement.getAuthor().getName() : "Неизвестно");

        log.info("Конвертация объявления с ID {}: Превью-изображение ID = {}",
                announcement.getId(), announcement.getPreviewImageId());

        dto.setPreviewImageUrl(announcement.getPreviewImageId() != null ?
                "/api/image/preview/" + announcement.getPreviewImageId() : null);

        dto.setImages(announcement.getImages() != null ?
                announcement.getImages().stream()
                .map(image -> new ImageDTO(image.getId(), image.getPath(), image.getContentType()))
                .collect(Collectors.toList())
                : new ArrayList<>());

        return dto;
    }
}
