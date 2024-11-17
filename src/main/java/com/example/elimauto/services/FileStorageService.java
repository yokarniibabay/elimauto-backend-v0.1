package com.example.elimauto.services;

import lombok.Getter;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

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
        if (fileName == null || fileName.isEmpty() || fileName.contains("..")) {
            throw new IllegalArgumentException("Invalid file name: " + fileName);
        }

        // Нормализуем путь и проверяем, что он находится внутри директории хранения
        Path destination = storageDirectory.resolve(fileName).normalize();
        if (!destination.getParent().equals(storageDirectory)) {
            throw new IllegalArgumentException("Cannot store file outside current directory");
        }

        try {
            Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Failed to store file " + fileName, e);
        }
    }

    /**
     * Сохраняет файл в директорию хранения.
     *
     * @param inputStream  Поток данных файла.
     * @param fileName       Имя файла для сохранения (внутри директории хранения).
     * @throws IOException Если произошла ошибка ввода-вывода.
     * @throws IllegalArgumentException Если имя файла некорректно.
     */

    public void saveFile(InputStream inputStream, String fileName) throws IOException {
        Path filePath = storageDirectory.resolve(fileName); // Используем resolve()
        Files.createDirectories(filePath.getParent()); // Создаем родительские директории, если нужно
        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
    }


    /**
     * Удаляет файл из директории хранения.
     *
     * @param fileName Имя файла для удаления.
     * @throws IOException Если произошла ошибка ввода-вывода.
     * @throws java.nio.file.NoSuchFileException если файл не существует.
     */
    public void deleteFile(String fileName) throws IOException {
        Path fileToDelete = storageDirectory.resolve(fileName);
        Files.deleteIfExists(fileToDelete); // Используем deleteIfExists для более безопасного удаления
    }

    public String getRelativePath(String fileName) {
        return storageDirectory.relativize(storageDirectory.resolve(fileName)).toString();
    }

}