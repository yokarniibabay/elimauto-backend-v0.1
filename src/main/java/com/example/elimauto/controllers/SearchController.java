package com.example.elimauto.controllers;

import com.example.elimauto.DTO.*;
import com.example.elimauto.services.CarReferenceService;
import com.example.elimauto.services.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final CarReferenceService carReferenceService;
    private final SearchService searchService;

    public SearchController(CarReferenceService carReferenceService,
                            SearchService searchService) {
        this.carReferenceService = carReferenceService;
        this.searchService = searchService;
    }

    @GetMapping("/marks")
    public ResponseEntity<List<MarkNameDTO>> getAllMarks() {
        return ResponseEntity.ok(carReferenceService.getAllMarksOrderedByNameAsc());
    }

    @GetMapping("/{markId}/models")
    public ResponseEntity<List<ModelNameDTO>> getModelsByMark(@PathVariable String markId) {
        return ResponseEntity.ok(carReferenceService.getModelsByMark(markId));
    }

    @GetMapping("/{modelId}/generations")
    public ResponseEntity<List<GenerationDTO>> getGenerationsByModel(@PathVariable String modelId) {
        return ResponseEntity.ok(carReferenceService.getGenerationsByModel(modelId));
    }

    @GetMapping("/{generationId}/configurations")
    public ResponseEntity<List<ConfigurationDTO>> getConfigurationsByGeneration(@PathVariable String generationId) {
        return ResponseEntity.ok(carReferenceService.getConfigurationsByGeneration(generationId));
    }

    @GetMapping("/engine-volumes")
    public ResponseEntity<List<String>> getEngineVolumes() {
        List<String> volumes = java.util.stream.IntStream.rangeClosed(5, 80)
                .mapToObj(i -> String.format("%.1f", i / 10.0))
                .toList();
        return ResponseEntity.ok(volumes);
    }

    @PostMapping("/announcements")
    public ResponseEntity<List<AnnouncementDTO>> searchAnnouncements(
            @RequestParam(required = false) String markId,
            @RequestParam(required = false) String modelId,
            @RequestParam(required = false) String generationId,
            @RequestParam(required = false) Double minVolume,
            @RequestParam(required = false) Double maxVolume,
            @RequestParam(required = false) Integer minYear,
            @RequestParam(required = false) Integer maxYear,
            @RequestParam(required = false) Integer minMileage,
            @RequestParam(required = false) Integer maxMileage,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String city
    ) {
        return ResponseEntity.ok(searchService.searchAnnouncements(
                markId, modelId, generationId, minVolume, maxVolume, minYear,
                maxYear, minMileage, maxMileage, minPrice, maxPrice, city
        ));
    }
}