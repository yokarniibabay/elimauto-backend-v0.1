package com.example.elimauto.controllers;

import com.example.elimauto.DTO.MarkNameDTO;
import com.example.elimauto.DTO.ModelDTO;
import com.example.elimauto.models.*;
import com.example.elimauto.services.CarReferenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<ModelDTO>> getModelsByMark(@PathVariable String markId) {
        List<ModelDTO> modelsByMark = carReferenceService.getModelsByMark(markId);
        if (modelsByMark.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(modelsByMark);
    }

    @GetMapping("/generations")
    public List<Generation> getGenerations(@PathVariable String modelId) {
        return carReferenceService.getGenerationsByModel(modelId);
    }

    @GetMapping("/configurations")
    public List<Configuration> getConfigurations(@PathVariable String generationId) {
        return carReferenceService.getConfigurationsByGeneration(generationId);
    }

    @GetMapping("/modifications")
    public List<Modification> getModifications(@PathVariable String configurationId) {
        return carReferenceService.getModificationsByConfiguration(configurationId);
    }

    @GetMapping("/specifications")
    public Specifications getSpecifications(@PathVariable String complectationId) {
        return carReferenceService.getSpecificationsByComplectation(complectationId);
    }

    @GetMapping("/options")
    public Options getOptions(@PathVariable String complectationId) {
        return carReferenceService.getOptionsByComplectation(complectationId);
    }
}