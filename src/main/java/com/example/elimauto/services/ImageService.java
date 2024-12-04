package com.example.elimauto.services;

import com.example.elimauto.models.Announcement;
import com.example.elimauto.models.Image;
import com.example.elimauto.repositories.AnnouncementRepository;
import com.example.elimauto.repositories.ImageRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
                Image savedImage = saveImage(file, announcement, false);
                savedImages.add(savedImage);

                if (isFirstImage) {
                    announcement.setPreviewImageId(savedImage.getId());
                    isFirstImage = false;
                }
            }
        }
        announcementRepository.save(announcement);
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
        fileStorageService.deleteFile(image.getPath());

        boolean removed = announcement.getImages().remove(image);
        log.info("Изображение с ID {} удалено из коллекции объявления: {}", image.getId(), removed);


        // Проверяем и обновляем previewImageId, если удаленное изображение было превью
        if (image.getId().equals(announcement.getPreviewImageId())) {
            if (!announcement.getImages().isEmpty()) {
                // Устанавливаем новое превью на первое доступное изображение
                announcement.setPreviewImageId(announcement.getImages().get(0).getId());
            } else {
                // Если изображений больше нет, сбрасываем previewImageId
                announcement.setPreviewImageId(null);
            }
            log.info("Preview image обновлено на ID: {}", announcement.getPreviewImageId());
        }
    }

    public void deleteImagesByAnnouncement(Announcement announcement) throws IOException {
        List<Image> images = imageRepository.findByAnnouncementId(announcement.getId());
        for (Image image : images) {
            deleteImage(image, announcement);
        }
    }
}
