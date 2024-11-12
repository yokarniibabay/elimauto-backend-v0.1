package com.example.elimauto.controllers;

import com.example.elimauto.models.Announcement;
import com.example.elimauto.services.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // @RestController вместо @Controller для работы с JSON
@RequestMapping("/api/announcements") // Общий URL для API
@RequiredArgsConstructor
public class AnnouncementController {
    private final AnnouncementService announcementService;

    // Получение всех объявлений в виде JSON
    @GetMapping
    public List<Announcement> getAllAnnouncements() {
        return announcementService.listAnnouncements();
    }

    // Получение конкретного объявления по ID
    @GetMapping("/{id}")
    public Announcement getAnnouncementById(@PathVariable Long id) {
        return announcementService.getAnnouncementById(id);
    }

    // Создание нового объявления
    @PostMapping("/add")
    public Announcement createAnnouncement(@RequestBody Announcement announcement) {
        announcementService.saveAnnouncement(announcement);
        return announcement; // Вернуть созданное объявление как JSON-ответ
    }

    // Удаление объявления по ID
    @DeleteMapping("/delete/{id}")
    public void deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncements(id);
    }
}