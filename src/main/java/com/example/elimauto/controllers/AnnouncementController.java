package com.example.elimauto.controllers;

import com.example.elimauto.models.Announcement;
import com.example.elimauto.services.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AnnouncementController {
    private final AnnouncementService announcementService;


    @GetMapping("/")
    public String announcements(@RequestParam(name = "title", required = false) String title, Model model) {
        model.addAttribute("announcements", announcementService.listAnnouncements(title));
        return "announcements";
    }

    @GetMapping("/announcement/{id}")
    public String announcementInfo(@PathVariable Long id, Model model) {
        model.addAttribute("announcement", announcementService.getAnnouncementById(id));
        return "announcement-info";
    }

    @PostMapping("/announcement/create")
    public String createAnnouncement(Announcement announcement) {
        announcementService.saveAnnouncement(announcement);
        return "redirect:/";
    }

    @PostMapping("/announcement/delete/{id}")
    public String deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncements(id);
        return "redirect:/";
    }

}