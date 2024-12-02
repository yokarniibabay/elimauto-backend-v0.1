package com.example.elimauto.services;

import com.example.elimauto.DTO.AnnouncementDTO;
import com.example.elimauto.DTO.ImageDTO;
import com.example.elimauto.consts.AnnouncementStatus;
import com.example.elimauto.models.Announcement;
import com.example.elimauto.models.AnnouncementUpdateRequest;
import com.example.elimauto.models.Image;
import com.example.elimauto.models.User;
import com.example.elimauto.repositories.AnnouncementRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final AccessService accessService;

    public List<AnnouncementDTO> getAllAnnouncements() {
        return announcementRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<AnnouncementDTO> getAllApprovedAnnouncements() {
        return announcementRepository.findAll().stream()
                .filter(announcement -> announcement.getStatus() == AnnouncementStatus.APPROVED)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<Announcement> getAnnouncementsByStatus(AnnouncementStatus status) {
        return announcementRepository.findByStatus(status);
    }

    public List<AnnouncementDTO> getAnnouncementsByAuthorId(Long authorId) {
        User currentUser = userService.getCurrentUser();

        List<Announcement> announcements = announcementRepository.findByAuthorId(authorId);

        return announcements.stream()
                .filter(announcement -> accessService.canAccessAnnouncement(announcement.getId(), SecurityContextHolder.getContext().getAuthentication()))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public AnnouncementDTO getAnnouncementById(Long id) throws AccessDeniedException {
        // Получаем объявление по ID
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Объявление с ID " + id + " не найдено."));

        // Получаем текущего пользователя, если он авторизован
        User currentUser = userService.getCurrentUserIfAuthenticated();

        // Проверяем доступ с использованием метода canAccessAnnouncement
        if (!accessService.canAccessAnnouncement(id, SecurityContextHolder.getContext().getAuthentication())) {
            throw new AccessDeniedException("Недостаточно прав для просмотра данного объявления.");
        }

        // Конвертируем объявление в DTO и возвращаем
        return convertToDto(announcement);
    }

    public AnnouncementDTO getPublicAnnouncementById(Long id) {
        // Находим объявление по ID
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Объявление с ID " + id + " не найдено."));

        // Проверяем, что статус объявления APPROVED
        if (announcement.getStatus() != AnnouncementStatus.APPROVED) {
            try {
                throw new AccessDeniedException("Объявление с ID " + id + " не опубликовано.");
            } catch (AccessDeniedException e) {
                throw new RuntimeException(e);
            }
        }

        // Конвертируем в DTO и возвращаем
        return convertToDto(announcement);
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

        Announcement savedAnnouncement = null;
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                throw new IllegalStateException("Пользователь должен быть аутентифицирован");
            }

            announcement.setAuthor(currentUser);
            announcement.setAuthorName(currentUser.getName());
            log.info("Текущий пользователь: {}", announcement.getAuthor());

            announcement.setStatus(AnnouncementStatus.PENDING);

            savedAnnouncement = announcementRepository.save(announcement);

            imageService.saveImages(files, savedAnnouncement, savedImages);

            imageService.setPreviewImage(savedAnnouncement, savedImages);

            announcementRepository.save(savedAnnouncement);
            log.info("Создано объявление с ID: {}", savedAnnouncement.getId());

        } catch (IOException | IllegalStateException e) {
            log.error("Ошибка при создании объявления: {}", e.getMessage());
            // Передаем сохраненное объявление в метод rollbackSavedImages
            imageService.rollbackSavedImages(savedImages, savedAnnouncement);
            throw e;
        }
    }

    @Transactional
    public void editAnnouncement(Long id,
                                 AnnouncementUpdateRequest updateRequest,
                                 List<MultipartFile> files) throws IOException {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Объявление с ID " + id + " не найдено."));

        User currentUser = userService.getCurrentUser();

        if (!accessService.canAccessAnnouncement(id, SecurityContextHolder.getContext().getAuthentication())) {
            throw new AccessDeniedException("Недостаточно прав для редактирования данного объявления.");
        }

        boolean priceChanged = updateRequest.getPrice() != null &&
                !announcement.getPrice().equals(updateRequest.getPrice());
        boolean otherFieldsChanged = false;

        if (updateRequest.getTitle() != null &&
                !updateRequest.getTitle().equals(announcement.getTitle())) {
            announcement.setTitle(updateRequest.getTitle());
            otherFieldsChanged = true;
        }
        if (updateRequest.getDescription() != null &&
                !updateRequest.getDescription().equals(announcement.getDescription())) {
            announcement.setDescription(updateRequest.getDescription());
            otherFieldsChanged = true;
        }
        if (updateRequest.getCity() != null &&
                !updateRequest.getCity().equals(announcement.getCity())) {
            announcement.setCity(updateRequest.getCity());
            otherFieldsChanged = true;
        }

        if (updateRequest.getPrice() != null && !announcement.getPrice().equals(updateRequest.getPrice())) {
            announcement.setPrice(updateRequest.getPrice());
            priceChanged = true;
        }

        if (announcement.getStatus() == AnnouncementStatus.APPROVED) {
            if (!(priceChanged && !otherFieldsChanged)) {
                announcement.setStatus(AnnouncementStatus.PENDING);
            }
        } else if (announcement.getStatus() == AnnouncementStatus.REJECTED) {
            announcement.setStatus(AnnouncementStatus.PENDING);
            announcement.setRejectedAt(null);
        }

        if (updateRequest.getImages() != null && !updateRequest.getImages().isEmpty()) {
            List<Image> savedImages = new ArrayList<>();

            imageService.saveImages(updateRequest.getImages(), announcement, savedImages);

            for (Image savedImage : savedImages) {
                if (savedImage.getId().equals(announcement.getPreviewImageId())) {
                    continue;
                }
            }
        }
        announcementRepository.save(announcement);
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

        dto.setAuthorName(announcement.getAuthor() != null ?
                announcement.getAuthor().getName() : "Неизвестно");
        dto.setAuthorNumber(announcement.getAuthor() != null ?
                announcement.getAuthor().getPhoneNumber() : "Invalid Phone Number");

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
