package com.example.elimauto.config;

import com.example.elimauto.services.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileStorageConfig {

    @Value("${file.storage.path}")
    private String storagePath;

    @Bean
    public FileStorageService fileStorageService() throws IOException {
        // Создаем абсолютный путь относительно корня проекта
        Path path = Paths.get(storagePath).toAbsolutePath().normalize();
        Files.createDirectories(path);
        System.out.println("Storage path: " + path); // для отладки
        return new FileStorageService(path);
    }
}