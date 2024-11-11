package com.example.elimauto.services;

import com.example.elimauto.models.Announcement;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnnouncementService {
    private List<Announcement> announcements = new ArrayList<>();
    private long ID = 0;

    {
        announcements.add(new Announcement(++ID,
                "BMW",
                "не битая, не крашенная",
                13500000,
                "Шымкент",
                "ADMIN"));
        announcements.add(new Announcement(++ID,
                "Mercedes Benz",
                "6.3 AMG жооооска валит",
                24990000,
                "Алматы",
                "Виталя"));
    }

    public List<Announcement> listAnnouncements() {
        return announcements;
    }

    public void saveAnnouncement(Announcement announcement) {
        announcement.setId(++ID);
        announcements.add(announcement);
    }

    public void deleteAnnouncements(Long id) {
        announcements.removeIf(announcement -> announcement.getId().equals(id));
    }

    public Announcement getAnnouncementById(Long id) {
        for (Announcement announcement : announcements) {
            if (announcement.getId().equals(id)) return announcement;
        }
        return null;
    }
}
