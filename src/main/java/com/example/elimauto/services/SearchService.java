package com.example.elimauto.services;

import com.example.elimauto.DTO.AnnouncementDTO;
import com.example.elimauto.models.Announcement;
import com.example.elimauto.repositories.AnnouncementRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final AnnouncementRepository announcementRepository;
    private final ModelMapper modelMapper;

    public SearchService(AnnouncementRepository announcementRepository, ModelMapper modelMapper) {
        this.announcementRepository = announcementRepository;
        this.modelMapper = modelMapper;
    }

    public List<AnnouncementDTO> searchAnnouncements(String markId, String modelId, String generationId,
                                                     Double minVolume, Double maxVolume, Integer minYear,
                                                     Integer maxYear, Integer minMileage, Integer maxMileage,
                                                     Double minPrice, Double maxPrice) {

        List<Announcement> announcements = announcementRepository.searchAnnouncements(
                markId, modelId, generationId, minVolume, maxVolume,
                minYear, maxYear, minMileage, maxMileage, minPrice, maxPrice
        );

        return announcements.stream()
                .map(announcement -> modelMapper.map(announcement, AnnouncementDTO.class))
                .collect(Collectors.toList());
    }
}