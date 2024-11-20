package com.example.elimauto.services;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Slf4j
@Getter
@Service
public class FileStorageService {

    private final Path storageDirectory;

    public FileStorageService(Path storageDirectory) throws IOException {
        this.storageDirectory = storageDirectory;
        Files.createDirectories(this.storageDirectory);
    }

    public void storeFile(InputStream inputStream, String fileName) throws IOException {
        if (inputStream == null || inputStream.available() == 0) {
            throw new IllegalArgumentException("Input stream is null or empty");
        }
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("File name is null or empty");
        }

        // Получаем только имя файла для предотвращения атаки через относительные пути
        fileName = Path.of(fileName).getFileName().toString();

        Path destination = storageDirectory.resolve(fileName).normalize();
        if (!destination.getParent().equals(storageDirectory)) {
            throw new IllegalArgumentException("Cannot store file outside current directory");
        }

        try {
            Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Failed to store file: {}", fileName, e);
            throw e;
        }
    }

    /**
     * Удаляет файл из директории хранения.
     *
     * @param fileName Имя файла для удаления.
     * @throws IOException Если произошла ошибка ввода-вывода.
     * @throws java.nio.file.NoSuchFileException если файл не существует.
     */
    public void deleteFile(String fileName) throws IOException {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("File name is null or empty");
        }

        // Получаем только имя файла
        fileName = Path.of(fileName).getFileName().toString();

        Path fileToDelete = storageDirectory.resolve(fileName).normalize();
        try {
            Files.deleteIfExists(fileToDelete);
        } catch (IOException e) {
            log.error("Failed to delete file: {}", fileName, e);
            throw e;
        }
    }
}