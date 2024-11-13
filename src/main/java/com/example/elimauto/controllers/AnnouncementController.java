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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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
        Announcement announcement = announcementService.getAnnouncementById(id);
        model.addAttribute("announcement", announcement);
        model.addAttribute("images", announcement.getImages());
        return "announcement-info";
    }

    @PostMapping("/announcement/create")
    public String createAnnouncement(@RequestParam("files") List<MultipartFile> files,
                                     Announcement announcement) throws IOException {

        announcementService.saveAnnouncement(announcement, files);
        return "redirect:/";
    }

    @PostMapping("/announcement/delete/{id}")
    public String deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncements(id);
        return "redirect:/";
    }

}