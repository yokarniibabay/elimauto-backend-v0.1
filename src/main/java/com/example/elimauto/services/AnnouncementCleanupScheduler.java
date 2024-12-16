package com.example.elimauto.services;

import com.example.elimauto.elimauto.consts.AnnouncementStatus;
import com.example.elimauto.models.Announcement;
import com.example.elimauto.repositories.AnnouncementRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class AnnouncementCleanupScheduler {
    private final AnnouncementRepository announcementRepository;

    public AnnouncementCleanupScheduler(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    // Выполняется каждый день в полночь
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteOldRejectedAnnouncements() {
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);

        List<Announcement> oldRejectedAnnouncements = announcementRepository.findAllByStatusAndRejectedAtBefore(
                AnnouncementStatus.REJECTED, threeDaysAgo);

        if (!oldRejectedAnnouncements.isEmpty()) {
            announcementRepository.deleteAll(oldRejectedAnnouncements);
            System.out.println("Удалено " + oldRejectedAnnouncements.size() + " устаревших отклоненных объявлений");
        }
    }
}
