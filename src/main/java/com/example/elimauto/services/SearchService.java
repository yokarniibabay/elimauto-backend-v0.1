package com.example.elimauto.services;

import com.example.elimauto.DTO.AnnouncementDTO;
import com.example.elimauto.models.Announcement;
import com.example.elimauto.repositories.AnnouncementRepository;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final AnnouncementRepository announcementRepository;
    private final AnnouncementService announcementService;
    private final CityService cityService;

    public SearchService(AnnouncementRepository announcementRepository,
                         AnnouncementService announcementService,
                         CityService cityService) {
        this.announcementRepository = announcementRepository;
        this.announcementService = announcementService;
        this.cityService = cityService;
    }

    public List<AnnouncementDTO> searchAnnouncements(
            String markId,
            String modelId,
            String generationId,
            Double minVolume,
            Double maxVolume,
            Integer minYear,
            Integer maxYear,
            Integer minMileage,
            Integer maxMileage,
            Double minPrice,
            Double maxPrice,
            String city
    ) {
        Locale locale = LocaleContextHolder.getLocale();

        String cityToSearch = null;
        if (city != null) {
            if (locale.getLanguage().equals("kk")) {
                cityToSearch = cityService.getCityNameKkBy(city);
            } else {
                cityToSearch = city;
            }
        }

        List<Announcement> announcements = announcementRepository.searchAnnouncements(
                markId, modelId, generationId, minVolume, maxVolume, minYear,
                maxYear, minMileage, maxMileage, minPrice, maxPrice, cityToSearch
        );

        return announcements.stream()
                .map(announcementService::convertToDto)
                .collect(Collectors.toList());
    }
}