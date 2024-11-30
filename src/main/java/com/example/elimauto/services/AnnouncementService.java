package com.example.elimauto.services;

import com.example.elimauto.DTO.AnnouncementDTO;
import com.example.elimauto.DTO.ImageDTO;
import com.example.elimauto.consts.AnnouncementStatus;
import com.example.elimauto.models.Announcement;
import com.example.elimauto.models.Image;
import com.example.elimauto.models.User;
import com.example.elimauto.repositories.AnnouncementRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
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

    public List<AnnouncementDTO> getAllAnnouncements() {
        return announcementRepository.findAll().stream()
                .filter(announcement -> announcement.getStatus() == AnnouncementStatus.APPROVED)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    public AnnouncementDTO getAnnouncementById(Long id) throws AccessDeniedException {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Объявление с ID " + id + " не найдено."));

        User currentUser = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            currentUser = userService.getCurrentUser();
        }

        // Если пользователь анонимный, проверяем только объявления со статусом APPROVED
        if (currentUser == null && announcement.getStatus() != AnnouncementStatus.APPROVED) {
            throw new AccessDeniedException("Недостаточно прав для просмотра данного объявления.");
        }

        // Если пользователь авторизован, проверяем доступ с использованием canAccessAnnouncement
        if (currentUser != null && !canAccessAnnouncement(currentUser, announcement, null)) {
            throw new AccessDeniedException("Недостаточно прав для просмотра данного объявления.");
        }

        // Если доступ разрешён, возвращаем DTO
        return convertToDto(announcement);
    }

    public List<Announcement> getAnnouncementsByStatus(AnnouncementStatus status) {
        return announcementRepository.findByStatus(status);
    }


    public List<AnnouncementDTO> getAnnouncementsByAuthorId(Long authorId) {
        User currentUser = userService.getCurrentUser();

        // Проверяем права доступа на основании нового метода
        List<Announcement> announcements = announcementRepository.findByAuthorId(authorId);

        return announcements.stream()
                .filter(announcement -> canAccessAnnouncement(currentUser, announcement, null)) // null для фильтрации по статусу
                .map(this::convertToDto)
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

            announcement.setStatus(AnnouncementStatus.PENDING);

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

    @Transactional
    public void updateAnnouncementStatus(Long id, AnnouncementStatus status) {
        User currentUser = userService.getCurrentUser();

        if (!currentUser.hasRole("ROLE_MODERATOR") && !currentUser.hasRole("ROLE_ADMIN")) {
            try {
                throw new AccessDeniedException("Недостаточно прав для модерации объявлений.");
            } catch (AccessDeniedException e) {
                throw new RuntimeException(e);
            }
        }

        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Объявление с ID " + id + " не найдено."));
        announcement.setStatus(status);
        announcementRepository.save(announcement);
    }

    @Transactional
    public void incrementViews(Long announcementId) {
        announcementRepository.incrementViews(announcementId);
    }

    @Transactional
    public void rejectAnnouncement(Long id, String comment) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Объявление с ID " + id + " не найдено."));
        announcement.setStatus(AnnouncementStatus.REJECTED);
        announcement.setStatusComment(comment);
        announcement.setRejectedAt(LocalDateTime.now());
        announcementRepository.save(announcement);
    }

    public void deleteAnnouncement(Long id) throws IOException {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Announcement not found"));

        imageService.deleteImagesByAnnouncement(announcement);

        announcementRepository.delete(announcement);
        log.info("Deleted announcement with ID: {}", id);
    }

    public boolean canAccessAnnouncement(User currentUser,
                                         Announcement announcement,
                                         AnnouncementStatus requiredStatus) {
        // Модератор и администратор имеют доступ к любым объявлениям
        if (currentUser.hasRole("ROLE_MODERATOR") || currentUser.hasRole("ROLE_ADMIN")) {
            return true;
        }

        // Автор объявления имеет доступ к своим объявлениям
        if (currentUser.getId().equals(announcement.getAuthor().getId())) {
            return true;
        }

        if (currentUser == null) {
            // Если пользователь не авторизован, доступ только к объявлениям со статусом APPROVED
            return announcement.getStatus() == AnnouncementStatus.APPROVED;
        }
        // По умолчанию проверяем статус
        return announcement.getStatus() == requiredStatus;
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

        dto.setViews(announcement.getViews());
        dto.setStatus(announcement.getStatus());
        dto.setStatusComment(announcement.getStatusComment());

        return dto;
    }
}
