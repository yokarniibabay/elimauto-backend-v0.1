package com.example.elimauto.controllers;

import com.example.elimauto.models.Announcement;
import com.example.elimauto.services.AnnouncementService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AnnouncementController {
    private final AnnouncementService announcementService;


    @GetMapping("/announcements")
    public ResponseEntity<List<Announcement>> getAnnouncements(@RequestParam(name = "title", required = false) String title) {
        List<Announcement> announcements = announcementService.listAnnouncements(title);
        return new ResponseEntity<>(announcements, HttpStatus.OK);
    }

    @GetMapping("/announcement/{id}")
    public ResponseEntity<Announcement> getAnnouncementInfo(@PathVariable Long id) {
        Announcement announcement = announcementService.getAnnouncementById(id);
        if (announcement == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(announcement, HttpStatus.OK);
    }

    @PostMapping("/announcement/create")
    public ResponseEntity<String> createAnnouncement(@RequestParam("title") String title,
                                                     @RequestParam("description") String description,
                                                     @RequestParam("price") double price,
                                                     @RequestParam("city") String city,
                                                     @RequestParam("author") String author, // Или возможно, объект Author?
                                                     @RequestParam("files") MultipartFile[] files) throws IOException {
        if (files.length > 20) {
            return new ResponseEntity<>("Максимум 20 изображений можно загрузить.", HttpStatus.BAD_REQUEST);
        }

        Announcement announcement = new Announcement();
        announcement.setTitle(title);
        announcement.setDescription(description);
        announcement.setPrice(price);
        announcement.setCity(city);
        announcement.setAuthor(author); // Или соответствующая логика, если author - объект

        announcementService.saveAnnouncement(announcement, Arrays.asList(files));

        return new ResponseEntity<>("Объявление создано успешно", HttpStatus.CREATED);
    }


    @DeleteMapping("/announcement/delete/{id}")
    public ResponseEntity<String> deleteAnnouncement(@PathVariable Long id) {
        try {
            announcementService.deleteAnnouncements(id);
            return new ResponseEntity<>("Объявление удалено", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Объявление не найдено", HttpStatus.NOT_FOUND);
        }
    }
}