package com.example.elimauto.services;

import com.example.elimauto.DTO.*;
import com.example.elimauto.consts.AnnouncementStatus;
import com.example.elimauto.models.*;
import com.example.elimauto.repositories.AnnouncementRepository;

import com.example.elimauto.repositories.ImageRepository;
import com.example.elimauto.repositories.ModificationRepository;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnnouncementService {
    private final AnnouncementRepository announcementRepository;
    private final ModificationRepository modificationRepository;
    private final ImageRepository imageRepository;
    private final ImageService imageService;
    private final UserService userService;
    private final AccessService accessService;
    private final CarReferenceService carReferenceService;

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
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Объявление с ID " + id + " не найдено."));

        User currentUser = userService.getCurrentUserIfAuthenticated();

        if (!accessService.canAccessAnnouncement(id, SecurityContextHolder.getContext().getAuthentication())) {
            throw new AccessDeniedException("Недостаточно прав для просмотра данного объявления.");
        }

        return convertToDto(announcement);
    }

    public AnnouncementDTO getPublicAnnouncementById(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Объявление с ID " + id + " не найдено."));

        incrementViews(id);

        if (announcement.getStatus() != AnnouncementStatus.APPROVED) {
            try {
                throw new AccessDeniedException("Объявление с ID " + id + " не опубликовано.");
            } catch (AccessDeniedException e) {
                throw new RuntimeException(e);
            }
        }

        return convertToDto(announcement);
    }



    @Transactional
    public void createAnnouncement(AnnouncementUpdateRequest updateRequest,
                                   List<MultipartFile> newImages) throws IOException {
        validateInputs(
                updateRequest.getDescription(),
                updateRequest.getPrice(),
                updateRequest.getCity(),
                newImages);

        List<Image> savedImages = new ArrayList<>();

        Announcement announcement = new Announcement();
        announcement.setDescription(updateRequest.getDescription());
        announcement.setPrice(updateRequest.getPrice());
        announcement.setCity(updateRequest.getCity());
        announcement.setYear(updateRequest.getYear());

        announcement.setMakeId(updateRequest.getMakeId());
        announcement.setModelId(updateRequest.getModelId());
        announcement.setGenerationId(updateRequest.getGenerationId());

        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                throw new IllegalStateException("Пользователь должен быть аутентифицирован");
            }

            announcement.setAuthor(currentUser);
            announcement.setAuthorName(currentUser.getName());
            log.info("Текущий пользователь: {}", announcement.getAuthor());

            announcement.setStatus(AnnouncementStatus.PENDING);

            // Первое сохранение объявления без изображений
            announcementRepository.save(announcement);

            // Генерируем title
            MarkDTO markDTO = carReferenceService.getMarkDTOById(updateRequest.getMakeId());
            ModelDTO modelDTO = carReferenceService.getModelById(updateRequest.getModelId());

            // Проверяем наличие комплектации
            String groupName = extractGroupName(updateRequest.getGenerationId());
            String generatedTitle;

            if (groupName != null && !groupName.isBlank()) {
                generatedTitle = markDTO.getName() + " "
                        + modelDTO.getName() + " "
                        + groupName + ", "
                        + updateRequest.getYear() + "г.";
            } else {
                generatedTitle = markDTO.getName() + " "
                        + modelDTO.getName() + ", "
                        + updateRequest.getYear() + "г.";
            }

            announcement.setTitle(generatedTitle);

            announcementRepository.save(announcement);

            if (newImages != null && !newImages.isEmpty()) {
                // Добавляем новые изображения к announcement
                imageService.saveImages(newImages, announcement, savedImages);
                announcementRepository.saveAndFlush(announcement);
            } else {
                // Если нет изображений, достаточно одного сохранения
                announcementRepository.save(announcement);
            }

            if (announcement.getPreviewImageId() == null && !announcement.getImages().isEmpty()) {
                Image firstImage = announcement.getImages().get(0);
                announcement.setPreviewImageId(firstImage.getId());
                announcementRepository.save(announcement);
            }

            announcementRepository.save(announcement);
            log.info("Создано объявление с ID: {}", announcement.getId());

        } catch (IOException | IllegalStateException e) {
            log.error("Ошибка при создании объявления: {}", e.getMessage());
            imageService.rollbackSavedImages(savedImages, announcement);
            throw e;
        }
    }

    @Transactional
    public void editAnnouncement(Long id, AnnouncementUpdateRequest request) throws IOException {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Объявление с ID " + id + " не найдено."));

        User currentUser = userService.getCurrentUser();

        if (!accessService.canAccessAnnouncement(id, SecurityContextHolder.getContext().getAuthentication())) {
            throw new AccessDeniedException("Недостаточно прав для редактирования данного объявления.");
        }

        // Обновление основных полей
        updateAnnouncementFields(announcement, request);

        log.info("Пользователь с ID {} редактирует объявление с ID {}", currentUser.getId(), announcement.getId());

        // Обновление статуса объявления
        boolean priceChanged = request.getPrice() != null && !announcement.getPrice().equals(request.getPrice());
        boolean otherFieldsChanged = request.getDescription() != null || request.getCity() != null;

        if (announcement.getStatus() == AnnouncementStatus.APPROVED) {
            if (!(priceChanged && !otherFieldsChanged)) {
                announcement.setStatus(AnnouncementStatus.PENDING);
            }
        } else if (announcement.getStatus() == AnnouncementStatus.REJECTED) {
            announcement.setStatus(AnnouncementStatus.PENDING);
            announcement.setRejectedAt(null);
        }

        if (request.getMakeId() != null || request.getModelId() != null || request.getYear() != null) {
            MarkDTO markDTO = carReferenceService.getMarkDTOById(announcement.getMakeId());
            ModelDTO modelDTO = carReferenceService.getModelById(announcement.getModelId());
            String newTitle = markDTO.getName() + " "
                    + modelDTO.getName() + ", "
                    + announcement.getYear() + "г.";
            announcement.setTitle(newTitle);
        }

        // 1. Обработка удаления изображений
        List<Long> imagesToDelete = request.getImagesToDelete();
        if (imagesToDelete != null && !imagesToDelete.isEmpty()) {
            for (Long imageId : imagesToDelete) {
                Image image = imageRepository.findById(imageId)
                        .orElseThrow(() -> new EntityNotFoundException("Изображение с ID " + imageId + " не найдено"));
                announcement.removeImage(image);
            }
        }

        log.info("Количество изображений после удаления: {}", announcement.getImages().size());

        // 2. Обработка новых изображений
        Map<String, Image> tempIdToImageMap = new HashMap<>();
        if (request.getNewImages() != null && !request.getNewImages().isEmpty()) {
            imageService.saveNewImages(announcement, request.getNewImages(), tempIdToImageMap);
        }

        log.info("Добавлено новых изображений: {}", tempIdToImageMap.size());

        // 3. Обновление порядка изображений
        imageService.updateImageOrder(announcement, request.getOrderedImageIds(), tempIdToImageMap);

        log.info("Порядок изображений обновлен.");

        // 4. Установка previewImageId
        imageService.updatePreviewImage(announcement, request.getPreviewImageId(), tempIdToImageMap);

        // 5. Сохранение изменений
        announcementRepository.save(announcement);
        log.info("Объявление с ID {} обновлено успешно.", announcement.getId());
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

        User currentUser = userService.getCurrentUser();

        if (!accessService.canAccessAnnouncement(id, SecurityContextHolder.getContext().getAuthentication())) {
            throw new AccessDeniedException("Недостаточно прав для редактирования данного объявления.");
        }

        imageService.deleteImagesByAnnouncement(announcement);

        announcementRepository.delete(announcement);
        log.info("Deleted announcement with ID: {}", id);
    }

    private void validateInputs(String description, Double price, String city, List<MultipartFile> files) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Описание не может быть пустым.");
        }
        if (price == null || price <= 0) {
            throw new IllegalArgumentException("Цена должна быть положительной.");
        }
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("Город не может быть пустым.");
        }
        if (files != null && files.size() > 20) {
            throw new IllegalArgumentException("Нельзя загрузить более 20 изображений.");
        }
    }

    // Вспомогательные методы

    private String generateTempId() {
        return "temp_" + UUID.randomUUID().toString();
    }

    public String extractGroupName(String complectationId) {
        if (complectationId == null || complectationId.isBlank()) {
            return null;
        }

        // Разделяем ID на части (X_Y_Z)
        String[] parts = complectationId.split("_");

        if (parts.length < 3) {
            log.warn("Некорректный формат complectationId: {}", complectationId);
            return null;
        }

        String configurationId = parts[0];
        String complectationPart = parts[1]; // Y
        String characteristicsId = parts[2];

        // Проверяем, есть ли Y (id комплектации)
        if (complectationPart == null || complectationPart.isBlank()) {
            log.info("Комплектация отсутствует для complectationId: {}", complectationId);
            return null; // Если Y отсутствует, возвращаем null
        }

        // Извлекаем комплектацию из базы
        Modification modification = modificationRepository.findByComplectationId(complectationId);
        if (modification != null) {
            return modification.getGroupName(); // Возвращаем название комплектации
        }

        log.info("Комплектация не найдена для complectationId: {}", complectationId);
        return null;
    }

    private void updateAnnouncementFields(Announcement announcement,
                                          AnnouncementUpdateRequest request) {
        if (request.getDescription() != null) announcement.setDescription(request.getDescription());
        if (request.getPrice() != null) announcement.setPrice(request.getPrice());
        if (request.getCity() != null) announcement.setCity(request.getCity());

        if (request.getYear() != null) announcement.setYear(request.getYear());
        if (request.getMakeId() != null) announcement.setMakeId(request.getMakeId());
        if (request.getModelId() != null) announcement.setModelId(request.getModelId());
        if (request.getGenerationId() != null) announcement.setGenerationId(request.getGenerationId());
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

        if (announcement.getMakeId() != null) {
            MarkDTO markDTO = carReferenceService.getMarkDTOById(announcement.getMakeId());
            dto.setMakeName(markDTO.getName());
        }
        if (announcement.getModelId() != null) {
            ModelDTO modelDTO = carReferenceService.getModelById(announcement.getModelId());
            dto.setModelName(modelDTO.getName());
        }
        if (announcement.getYear() != null) {
            dto.setYear(announcement.getYear());
        }

        return dto;
    }
}
