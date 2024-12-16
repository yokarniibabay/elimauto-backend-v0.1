package com.example.elimauto.services;

import com.example.elimauto.elimauto.consts.AnnouncementStatus;
import com.example.elimauto.models.Announcement;
import com.example.elimauto.models.User;
import com.example.elimauto.repositories.AnnouncementRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AccessService {
    private final AnnouncementRepository announcementRepository;

    public AccessService(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    public boolean canAccessAnnouncement(Long announcementId, Authentication authentication) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new EntityNotFoundException("Объявление не найдено"));

        if (announcement.getStatus() == AnnouncementStatus.APPROVED) {
            return true;
        }

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            return false;
        }

        User currentUser = (User) authentication.getPrincipal();

        if (currentUser.getId().equals(announcement.getAuthor().getId())) {
            return true;
        }

        if (currentUser.hasRole("ROLE_MODERATOR") ||
                currentUser.hasRole("ROLE_ADMIN")) {
            return announcement.getStatus() == AnnouncementStatus.PENDING ||
                    announcement.getStatus() == AnnouncementStatus.APPROVED;
        }

        return false;
    }

    public boolean canEditAnnouncement(Long id, Authentication authentication) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Объявление не найдено"));

        User currentUser = (User) authentication.getPrincipal();

        return announcement.getAuthor().getId().equals(currentUser.getId()) ||
                currentUser.hasRole("ROLE_MODERATOR") ||
                currentUser.hasRole("ROLE_ADMIN");
    }
}
