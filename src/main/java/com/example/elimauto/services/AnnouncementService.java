package com.example.elimauto.services;

import com.example.elimauto.models.Announcement;
import com.example.elimauto.repositories.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnnouncementService {
    private final AnnouncementRepository announcementRepository;

    public List<Announcement> listAnnouncements(String title) {
        if (title != null) return announcementRepository.findByTitle(title);
        return announcementRepository.findAll();
    }

    public void saveAnnouncement(Announcement announcement) {
        log.info("Saving new {}", announcement);
        announcementRepository.save(announcement);
    }

    public void deleteAnnouncements(Long id) {
        announcementRepository.deleteById(id);
    }

    public Announcement getAnnouncementById(Long id) {
        return announcementRepository.findById(id).orElse(null);
    }
}
