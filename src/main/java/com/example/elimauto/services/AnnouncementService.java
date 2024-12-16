package com.example.elimauto.services;

import com.example.elimauto.DTO.*;
import com.example.elimauto.elimauto.consts.AnnouncementStatus;
import com.example.elimauto.models.*;
import com.example.elimauto.repositories.AnnouncementRepository;

import com.example.elimauto.repositories.ImageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;
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
        return announcementRepository.findAllByOrderByCreatedAtDesc().stream()
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
        announcement.setColor(updateRequest.getColor());
        announcement.setDriveType(updateRequest.getDriveType());
        announcement.setEngineCapacity(updateRequest.getEngineCapacity());
        announcement.setTransmissionType(updateRequest.getTransmissionType());
        announcement.setMileage(updateRequest.getMileage());
        announcement.setHorsePower(updateRequest.getHorsePower());

        announcement.setMakeId(updateRequest.getMakeId());
        announcement.setModelId(updateRequest.getModelId());
        announcement.setGenerationId(updateRequest.getGenerationId());
        announcement.setBodyType(updateRequest.getBodyType());
        announcement.setConfigurationId(updateRequest.getConfigurationId());

        MarkDTO markDTO = carReferenceService.getMarkDTOById(updateRequest.getMakeId());
        ModelDTO modelDTO = carReferenceService.getModelById(updateRequest.getModelId());
        GenerationDTO generationDTO = carReferenceService.getGenerationDTOById(updateRequest.getGenerationId());

        announcement.setMakeName(markDTO.getName());
        announcement.setModelName(modelDTO.getName());

        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                throw new IllegalStateException("Пользователь должен быть аутентифицирован");
            }

            announcement.setAuthor(currentUser);
            announcement.setAuthorName(currentUser.getName());

            announcement.setStatus(AnnouncementStatus.PENDING);

            announcementRepository.save(announcement);

            String groupName = "";
//            if (updateRequest.getConfigurationId() != null) {
//                groupName = extractGroupName(updateRequest.getConfigurationId());
//            }

            String generatedTitle = generateTitle(markDTO.getName(), modelDTO.getName(),
                    generationDTO.isRestyle(), groupName,
                    updateRequest.getYear());

            announcement.setTitle(generatedTitle);

            announcementRepository.save(announcement);

            if (newImages != null && !newImages.isEmpty()) {
                imageService.saveImages(newImages, announcement, savedImages);
                announcementRepository.saveAndFlush(announcement);
            } else {
                announcementRepository.save(announcement);
            }

            if (announcement.getPreviewImageId() == null && !announcement.getImages().isEmpty()) {
                Image firstImage = announcement.getImages().get(0);
                announcement.setPreviewImageId(firstImage.getId());
                announcementRepository.save(announcement);
            }
            announcement.setUpdatedAt(LocalDateTime.now());
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

        updateAnnouncementFields(announcement, request);

        log.info("Пользователь с ID {} редактирует объявление с ID {}", currentUser.getId(), announcement.getId());

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
        announcement.setUpdatedAt(LocalDateTime.now());
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
        announcement.setUpdatedAt(LocalDateTime.now());
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
        announcement.setUpdatedAt(LocalDateTime.now());
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

    private String generateTitle(String markName, String modelName, boolean isRestyle,
                                 String groupName, Integer year) {
        StringBuilder title = new StringBuilder();

        title.append(markName).append(" ");
        title.append(modelName).append(" ");
        if (isRestyle) {
            title.append("Рестайлинг ");
        }
        if (groupName != null) {
            title.append(groupName).append(" ");
        }
        if (year != null) {
            title.append(year).append(" г.");
        }

        return title.toString().trim();
    }

    private String generateTempId() {
        return "temp_" + UUID.randomUUID().toString();
    }

//    private String extractGroupName(String configurationId) {
//        if (configurationId == null || configurationId.isEmpty()) {
//            return null;
//        }
//        ModificationDTO modificationDTO =
//                carReferenceService.getModificationDTOByConfigurationId(configurationId);
//        return modificationDTO != null ? modificationDTO.getGroupName() : null;
//    }

    private void updateAnnouncementFields(Announcement announcement,
                                          AnnouncementUpdateRequest request) {
        if (request.getDescription() != null) announcement.setDescription(request.getDescription());
        if (request.getPrice() != null) announcement.setPrice(request.getPrice());
        if (request.getCity() != null) announcement.setCity(request.getCity());
        if (request.getYear() != null) announcement.setYear(request.getYear());
        if (request.getColor() != null) announcement.setColor(request.getColor());
        if (request.getDriveType() != null) announcement.setDriveType(request.getDriveType());
        if (request.getEngineCapacity() != 0) announcement.setEngineCapacity(request.getEngineCapacity());
        if (request.getTransmissionType() != null) announcement.setTransmissionType(request.getTransmissionType());
        if (request.getMileage() != null) announcement.setMileage(request.getMileage());
        if (request.getHorsePower() != null) announcement.setHorsePower(request.getHorsePower());
        if (request.getBodyType() != null) announcement.setBodyType(request.getBodyType());

        if (request.getMakeId() != null) {
            announcement.setMakeId(request.getMakeId());
            MarkDTO markDTO = carReferenceService.getMarkDTOById(request.getMakeId());
            announcement.setMakeName(markDTO.getName());
        }

        if (request.getModelId() != null) {
            announcement.setModelId(request.getModelId());
            ModelDTO modelDTO = carReferenceService.getModelById(request.getModelId());
            announcement.setModelName(modelDTO.getName());
        }

        if (request.getGenerationId() != null) {
            announcement.setGenerationId(request.getGenerationId());
        }

        if (request.getConfigurationId() != null) {
            announcement.setConfigurationId(request.getConfigurationId());
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

        dto.setMakeName(announcement.getMakeName());
        dto.setModelName(announcement.getModelName());
        dto.setYear(announcement.getYear());

        dto.setColor(announcement.getColor());
        dto.setEngineCapacity(announcement.getEngineCapacity());
        dto.setTransmissionType(announcement.getTransmissionType());
        dto.setDriveType(announcement.getDriveType());
        dto.setMileage(announcement.getMileage());
        dto.setHorsePower(announcement.getHorsePower());
        GenerationDTO generationDTO = carReferenceService.getGenerationDTOById(announcement.getGenerationId());
        dto.setGenerationName(generationDTO.getName());

        return dto;
    }
}
