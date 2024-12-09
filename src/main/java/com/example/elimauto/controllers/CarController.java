package com.example.elimauto.controllers;

import com.example.elimauto.DTO.MarkDTO;
import com.example.elimauto.DTO.ModelDTO;
import com.example.elimauto.models.*;
import com.example.elimauto.services.CarReferenceService;
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

    @GetMapping("/makes")
    public List<MarkDTO> getMakes() {
        return carReferenceService.getAllMarks();
    }

    @GetMapping("/models")
    public List<ModelDTO> getModels(@RequestParam String markId) {
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