package com.example.elimauto.services;

import com.example.elimauto.consts.AnnouncementStatus;
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

        // 1. Неавторизованные пользователи могут видеть только объявления со статусом APPROVED
        if (announcement.getStatus() == AnnouncementStatus.APPROVED) {
            return true; // Одобренные объявления доступны всем
        }

        // 2. Для авторизованных пользователей
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return false; // Неавторизованные пользователи не имеют доступа к другим объявлениям
        }

        User currentUser = (User) authentication.getPrincipal();

        // 3. Автор может видеть свои объявления, даже если они в статусе PENDING
        if (currentUser.getId().equals(announcement.getAuthor().getId())) {
            return true; // Автор может всегда видеть свои объявления
        }

        // 4. Модератор и администратор могут видеть все объявления со статусом PENDING
        if (currentUser.hasRole("ROLE_MODERATOR") || currentUser.hasRole("ROLE_ADMIN")) {
            return announcement.getStatus() == AnnouncementStatus.PENDING || announcement.getStatus() == AnnouncementStatus.APPROVED;
        }

        // 5. Другие пользователи могут видеть только APPROVED объявления
        return false;
    }
}
