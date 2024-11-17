package com.example.elimauto.services;

import com.example.elimauto.models.Announcement;
import com.example.elimauto.models.Image;
import com.example.elimauto.repositories.ImageRepository;
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

@Service
public class ImageService {
    private final FileStorageService fileStorageService;
    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository, FileStorageService fileStorageService) {
        this.imageRepository = imageRepository;
        this.fileStorageService = fileStorageService;
    }

    public Image saveImage(MultipartFile file, Announcement announcement, boolean isPreview)
            throws IOException {
        String contentType = file.getContentType();
        if (!List.of("image/jpeg", "image/png", "image/heic", "image/webp").contains(contentType)) {
            throw new IllegalArgumentException("Unsupported file type: " + contentType);
        }

        byte[] processedBytes = convertToJpeg(file);
        processedBytes = removeMetadata(processedBytes);

        String fileName = UUID.randomUUID() + ".jpeg";

        try (ByteArrayInputStream bais = new ByteArrayInputStream(processedBytes)) {
            fileStorageService.storeFile(bais, fileName); // Используем FileStorageService
        }

        Image image = new Image();
        image.setName(fileName);
        image.setContentType("image/jpeg");
        image.setPath(fileName); // Храним только имя файла
        image.setPreviewImage(isPreview);
        image.setAnnouncement(announcement);

        return imageRepository.save(image);
    }

    private byte[] convertToJpeg(MultipartFile file) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(file.getInputStream())
                .size(1920, 1080) // Уменьшение до FullHD
                .outputQuality(0.9f)
                .outputFormat("jpeg") // Конвертация в JPEG
                .toOutputStream(outputStream);
        return outputStream.toByteArray();
    }

    // Удаление метаданных
    public byte[] removeMetadata(byte[] imageBytes) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpeg", outputStream); // Пересохранение удаляет метаданные
        return outputStream.toByteArray();
    }

    public Optional<Image> getImageById(Long id) {
        return imageRepository.findById(id);
    }

    public List<Image> getImagesByAnnouncementId(Long announcementId) {
        return imageRepository.findByAnnouncementId(announcementId);
    }

    public void deleteImage(Image image) throws IOException {
        fileStorageService.deleteFile(image.getPath());
        imageRepository.delete(image);
    }
}
