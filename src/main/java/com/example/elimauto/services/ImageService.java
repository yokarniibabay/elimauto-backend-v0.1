package com.example.elimauto.services;

import com.example.elimauto.models.Announcement;
import com.example.elimauto.models.Image;
import com.example.elimauto.repositories.AnnouncementRepository;
import com.example.elimauto.repositories.ImageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ImageService {
    private static final List<String> SUPPORTED_IMAGE_TYPES =
            List.of("image/jpeg",
            "image/png",
            "image/heic",
            "image/webp");
    private final FileStorageService fileStorageService;
    private final ImageRepository imageRepository;
    private final AnnouncementRepository announcementRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository,
                        AnnouncementRepository announcementRepository,
                        FileStorageService fileStorageService) {
        this.imageRepository = imageRepository;
        this.announcementRepository = announcementRepository;
        this.fileStorageService = fileStorageService;
    }

    public Image saveImage(MultipartFile file, Announcement announcement, boolean isPreview)
            throws IOException {
        validateFile(file);

        byte[] processedBytes = processImage(file);

        String fileName = UUID.randomUUID() + ".jpeg";

        try (ByteArrayInputStream bais = new ByteArrayInputStream(processedBytes)) {
            fileStorageService.storeFile(bais, fileName);
        }

        Image image = new Image();
        image.setName(fileName);
        image.setContentType("image/jpeg");
        image.setPath(fileName);
        image.setPreviewImage(isPreview);
        image.setAnnouncement(announcement);
        image.setDisplayOrder(announcement.getImages().size() - 1);

        // Сохраняем изображение
        Image savedImage = imageRepository.save(image);

        // Добавляем изображение в коллекцию объявления
        announcement.getImages().add(savedImage);

        return savedImage;
    }

    public void saveImages(List<MultipartFile> files,
                           Announcement announcement,
                           List<Image> savedImages) throws IOException {
        if (files == null || files.isEmpty()) {
            return;
        }

        boolean isFirstImage = true;

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                Image newImage = new Image();
                newImage.setName(UUID.randomUUID() + ".jpeg");
                newImage.setContentType(file.getContentType());

                byte[] processedBytes = processImage(file);
                try (ByteArrayInputStream bais = new ByteArrayInputStream(processedBytes)) {
                    fileStorageService.storeFile(bais, newImage.getName());
                }

                newImage.setPath(newImage.getName());
                newImage.setPreviewImage(false);
                newImage.setAnnouncement(announcement);
                newImage.setDisplayOrder(announcement.getImages().size());

                announcement.getImages().add(newImage);
                savedImages.add(newImage);

                if (isFirstImage) {
                    isFirstImage = false;
                }
            }
        }
    }

    public void saveNewImages(Announcement announcement,
                               List<MultipartFile> newImages,
                               Map<String, Image> tempIdToImageMap) throws IOException {
        int tempCounter = 1;
        for (MultipartFile file : newImages) {
            String tempId = "temp_" + tempCounter++;
            Image newImage = saveImage(file, announcement, false);
            tempIdToImageMap.put(tempId, newImage);
        }
    }

    public void updateImageOrder(Announcement announcement,
                                 List<String> orderedImageIds,
                                 Map<String, Image> tempIdToImageMap) {
        // Логируем входные параметры и текущее состояние
        log.debug("updateImageOrder called with announcementId: {}", announcement.getId());
        log.debug("orderedImageIds: {}", orderedImageIds);
        log.debug("Images in announcement before reorder: {}", announcement.getImages());
        log.debug("tempIdToImageMap keys: {}", tempIdToImageMap.keySet());

        List<Image> updatedImages = new ArrayList<>();

        // Создаём карту для быстрого поиска существующих изображений
        Map<Long, Image> existingImagesMap = announcement.getImages().stream()
                .collect(Collectors.toMap(Image::getId, img -> img));

        for (String id : orderedImageIds) {
            log.debug("Processing orderedImageId: {}", id);

            Image image;
            if (isNumeric(id)) {
                // Считаем, что это существующее изображение
                Long imageId = Long.parseLong(id);
                log.debug("Trying to find existing image with ID: {}", imageId);
                image = existingImagesMap.get(imageId);
                if (image == null) {
                    log.debug("Image with ID {} not found among announcement images", imageId);
                    throw new EntityNotFoundException("Существующее изображение с ID " + imageId + " не найдено в объявлении.");
                } else {
                    log.debug("Found existing image: {}", image);
                }
            } else {
                // Это новый image по tempId
                log.debug("Trying to find new image with tempId: {}", id);
                image = tempIdToImageMap.get(id);
                if (image == null) {
                    log.debug("New image with tempId '{}' not found in tempIdToImageMap", id);
                    throw new EntityNotFoundException("Новое изображение с tempId '" + id + "' не найдено.");
                } else {
                    log.debug("Found new image: {}", image);
                }
            }

            updatedImages.add(image);
        }

        log.debug("All images found. updatedImages size: {}", updatedImages.size());

        // Очищаем текущую коллекцию изображений и добавляем обновлённые по одному
        List<Image> currentImages = announcement.getImages();
        currentImages.clear();
        log.debug("Current images cleared. Current size: {}", currentImages.size());

        for (int i = 0; i < updatedImages.size(); i++) {
            Image img = updatedImages.get(i);
            img.setDisplayOrder(i);
            announcement.addImageToAnnouncement(img);
            log.debug("Set displayOrder={} for image with ID={} and added to announcement", i, img.getId());
        }

        log.debug("Final images in announcement after reorder: {}", announcement.getImages());
    }

    public void updatePreviewImage(Announcement announcement,
                                    String previewImageId,
                                    Map<String, Image> tempIdToImageMap) {

        if (previewImageId != null) {
            Image previewImage;

            if (isNumeric(previewImageId)) {
                // Идентификатор существует в базе данных
                Long previewId = Long.parseLong(previewImageId);
                previewImage = imageRepository.findById(previewId)
                        .orElseThrow(() ->
                                new EntityNotFoundException("Изображение с ID " + previewId + " не найдено."));
            } else {
                // Идентификатор временный (для новых изображений)
                previewImage = tempIdToImageMap.get(previewImageId);
                if (previewImage == null) {
                    throw new IllegalArgumentException("Некорректный идентификатор previewImage: " + previewImageId);
                }
            }

            // Сбрасываем старое изображение, если оно было установлено как превью
            if (announcement.getPreviewImageId() != null) {
                Image oldPreviewImage = imageRepository.findById(announcement.getPreviewImageId())
                        .orElseThrow(() ->
                                new EntityNotFoundException("Изображение с ID "
                                        + announcement.getPreviewImageId() + " не найдено."));
                oldPreviewImage.setPreviewImage(false);
                imageRepository.save(oldPreviewImage);
            }

            // Устанавливаем новое изображение как превью
            previewImage.setPreviewImage(true);
            imageRepository.save(previewImage);

            // Обновляем поле previewImageId у объявления
            announcement.setPreviewImageId(previewImage.getId());
            log.info("previewImageId установлено на ID: {}", previewImage.getId());

        } else if (!announcement.getImages().isEmpty()) {
            // Если previewImageId не передан, ставим первое изображение как превью
            Image firstImage = announcement.getImages().get(0);
            firstImage.setPreviewImage(true);
            imageRepository.save(firstImage);
            announcement.setPreviewImageId(firstImage.getId());
            log.info("previewImageId установлено на ID: {}", firstImage.getId());
        }
    }

    public void deleteImages(Announcement announcement, List<Long> imagesToDelete) {
        for (Long imageId : imagesToDelete) {
            Image image = imageRepository.findById(imageId)
                    .orElseThrow(() -> new EntityNotFoundException("Изображение с ID " + imageId + " не найдено."));
            try {
                deleteImage(image, announcement);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }

    public void setPreviewImage(Announcement announcement, List<Image> savedImages) {
        Long previewImageId = savedImages.stream()
                .filter(Image::isPreviewImage)
                .map(Image::getId)
                .findFirst()
                .orElse(null);
        announcement.setPreviewImageId(previewImageId);
    }

    public void rollbackSavedImages(List<Image> savedImages, Announcement announcement) {
        for (Image image : savedImages) {
            try {
                deleteImage(image, announcement);
            } catch (IOException e) {
                log.error("Ошибка при удалении изображения: {}", image.getId(), e);
            }
        }
    }

    public byte[] processImage(MultipartFile file) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(file.getInputStream())
                .size(1920, 1080)
                .outputQuality(0.9f)
                .outputFormat("jpeg")
                .toOutputStream(outputStream);
        return removeMetadata(outputStream.toByteArray());
    }

    public byte[] removeMetadata(byte[] imageBytes) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpeg", outputStream);
        return outputStream.toByteArray();
    }

    public static void validateFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (!SUPPORTED_IMAGE_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Unsupported file type: " + contentType);
        }

        if (!isValidImageFormat(file)) {
            throw new IllegalArgumentException("Файл не является изображением");
        }
    }

    private static boolean isValidImageFormat(MultipartFile file) {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            return image != null;
        } catch (IOException e) {
            return false;
        }
    }

    public Optional<Image> getImageById(Long id) {
        return imageRepository.findById(id);
    }

    public List<Image> getImagesByAnnouncementId(Long announcementId) {
        return imageRepository.findByAnnouncementId(announcementId);
    }

    public void deleteImage(Image image, Announcement announcement) throws IOException {
        try {
            fileStorageService.deleteFile(image.getPath());
            log.info("Файл изображения с ID {} удален с пути: {}", image.getId(), image.getPath());

            boolean removed = announcement.getImages().remove(image);
            log.info("Изображение с ID {} удалено из коллекции объявления: {}", image.getId(), removed);

            if (image.getId().equals(announcement.getPreviewImageId())) {
                if (!announcement.getImages().isEmpty()) {
                    announcement.setPreviewImageId(announcement.getImages().get(0).getId());
                    log.info("Preview image обновлено на ID: {}", announcement.getPreviewImageId());
                } else {
                    announcement.setPreviewImageId(null);
                    log.info("Все изображения удалены, previewImageId сброшен.");
                }
            }
        } catch (IOException e) {
            log.error("Ошибка при удалении изображения с ID {}: {}", image.getId(), e.getMessage());
            throw e;
        }
    }

    public void deleteImagesByAnnouncement(Announcement announcement) throws IOException {
        List<Image> images = imageRepository.findByAnnouncementId(announcement.getId());
        for (Image image : images) {
            deleteImage(image, announcement);
        }
    }
}
