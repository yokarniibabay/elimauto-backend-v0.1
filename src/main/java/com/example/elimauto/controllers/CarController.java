package com.example.elimauto.controllers;

import com.example.elimauto.DTO.ConfigurationDTO;
import com.example.elimauto.DTO.GenerationDTO;
import com.example.elimauto.DTO.MarkNameDTO;
import com.example.elimauto.DTO.ModelNameDTO;
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
    public ResponseEntity<List<ModelNameDTO>> getModelsByMark(@PathVariable String markId) {
        List<ModelNameDTO> modelsByMark = carReferenceService.getModelsByMark(markId);
        if (modelsByMark.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(modelsByMark);
    }

    @GetMapping("/{modelId}/generations")
    public ResponseEntity<List<GenerationDTO>> getGenerations(@PathVariable String modelId) {
        List<GenerationDTO> generationsByModel = carReferenceService.getGenerationsByModel(modelId);
        if (generationsByModel.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(generationsByModel);
    }

    @GetMapping("/{generationId}/configurations")
    public ResponseEntity<List<ConfigurationDTO>> getConfigurations(@PathVariable String generationId) {
        List<ConfigurationDTO> configurationDTOSByGeneration =
                carReferenceService.getConfigurationsByGeneration(generationId);
        if(configurationDTOSByGeneration.isEmpty()) {
            return  ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(configurationDTOSByGeneration);
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