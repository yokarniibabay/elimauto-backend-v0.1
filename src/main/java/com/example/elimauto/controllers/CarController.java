package com.example.elimauto.controllers;

import com.example.elimauto.DTO.MarkNameDTO;
import com.example.elimauto.DTO.ModelDTO;
import com.example.elimauto.models.*;
import com.example.elimauto.services.CarReferenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {
    private final CarReferenceService carReferenceService;

    public CarController(CarReferenceService carReferenceService) {
        this.carReferenceService = carReferenceService;
    }

    @GetMapping("/makes-popular")
    public ResponseEntity<List<MarkNameDTO>> getMakesOrderedByPopular() {
        List<MarkNameDTO> popularMarks = carReferenceService.getAllMarksOrderedByPopular();
        if (popularMarks.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(popularMarks);
    }

    @GetMapping("/makes/{markId}/models")
    public List<ModelDTO> getModelsByMark(@RequestParam String markId) {
        return carReferenceService.getModelsByMark(markId);
    }

    @GetMapping("/generations")
    public List<Generation> getGenerations(@RequestParam String modelId) {
        return carReferenceService.getGenerationsByModel(modelId);
    }

    @GetMapping("/configurations")
    public List<Configuration> getConfigurations(@RequestParam String generationId) {
        return carReferenceService.getConfigurationsByGeneration(generationId);
    }

    @GetMapping("/modifications")
    public List<Modification> getModifications(@RequestParam String configurationId) {
        return carReferenceService.getModificationsByConfiguration(configurationId);
    }

    @GetMapping("/specifications")
    public Specifications getSpecifications(@RequestParam String complectationId) {
        return carReferenceService.getSpecificationsByComplectation(complectationId);
    }

    @GetMapping("/options")
    public Options getOptions(@RequestParam String complectationId) {
        return carReferenceService.getOptionsByComplectation(complectationId);
    }
}