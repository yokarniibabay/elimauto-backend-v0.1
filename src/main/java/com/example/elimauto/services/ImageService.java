package com.example.elimauto.services;

import com.example.elimauto.models.Image;
import com.example.elimauto.repositories.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ImageService {
    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Image saveImage(MultipartFile file, boolean isPreviewImage) throws IOException {
        Image image = new Image();
        image.setName(file.getOriginalFilename());
        image.setContentType(file.getContentType());
        image.setSize(file.getSize());
        image.setBytes(file.getBytes());
        image.setPreviewImage(isPreviewImage);

        // Сохраняем изображение в базе данных через imageRepository
        return imageRepository.save(image);
    }

    // Метод для получения изображения по id
    public Optional<Image> getImageById(Long id) {
        return imageRepository.findById(id);
    }

    public List<Image> getImagesByAnnouncementId(Long announcementId) {
        return imageRepository.findByAnnouncementId(announcementId);
    }
}
