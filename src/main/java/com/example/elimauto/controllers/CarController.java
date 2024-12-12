package com.example.elimauto.controllers;

import com.example.elimauto.DTO.*;
import com.example.elimauto.models.*;
import com.example.elimauto.services.CarReferenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
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

    @GetMapping("/{configurationId}/modifications")
    public ResponseEntity<ModificationDTO> getModifications(@PathVariable String configurationId) {
        try {
            ModificationDTO modificationDTO = carReferenceService.getModificationDTOByConfigurationId(configurationId);
            if (modificationDTO == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(modificationDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{complectationId}/specifications/engineDetails")
    public ResponseEntity<SpecificationsEngineDetailsDTO> getSpecificationsEngineDetails
            (@PathVariable String complectationId) {
        try {
            SpecificationsEngineDetailsDTO specificationsEngineDetailsDTO =
                    carReferenceService.getSpecificationsEngineDetailsDTO(complectationId);
            if (specificationsEngineDetailsDTO == null) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(specificationsEngineDetailsDTO);
        } catch (NoSuchElementException ex) {
            log.warn("No such Specifications element with complectationdId: {}", complectationId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{complectationId}/specifications/transmissionDetails")
    public ResponseEntity<String> getSpecificationsTransmissionDetails (@PathVariable String complectationId) {
        try {
            String transmission =
                    carReferenceService.getSpecificationsByComplectation(complectationId).getTransmission();
            if (transmission == null || transmission.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(transmission);
        } catch (NoSuchElementException ex) {
            log.warn("Transmission was not found for such id: {}", complectationId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{complectationId}/specifications/driveDetails")
    public ResponseEntity<String> getSpecificationsDriveDetails (@PathVariable String complectationId) {
        try {
            String driveDetails = carReferenceService.getSpecificationsByComplectation(complectationId).getDrive();
            if (driveDetails == null || driveDetails.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(driveDetails);
        } catch (NoSuchElementException ex) {
            log.warn("No drive details found for such id: {}", complectationId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/options")
    public Options getOptions(@PathVariable String complectationId) {
        return carReferenceService.getOptionsByComplectation(complectationId);
    }
}