package com.example.elimauto.controllers;

import com.example.elimauto.DTO.*;
import com.example.elimauto.models.*;
import com.example.elimauto.services.CarReferenceService;
import com.example.elimauto.services.SpecificationService;
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
    private final SpecificationService specificationService;

    public CarController(CarReferenceService carReferenceService,
                         SpecificationService specificationService) {
        this.carReferenceService = carReferenceService;
        this.specificationService = specificationService;
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
    public ResponseEntity<List<ModificationDTO>> getModifications(@PathVariable String configurationId) {
        try {
            List<ModificationDTO> modificationDTOS = carReferenceService.getModificationDTOByConfigurationId(configurationId);
            if (modificationDTOS == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(modificationDTOS);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{configurationId}/engine-capacities")
    public ResponseEntity<List<String>> getEngineCapacities(@PathVariable String configurationId) {
        return ResponseEntity.ok(specificationService.getAvailableEngineCapacities(configurationId));
    }

    @GetMapping("/{configurationId}/transmissions")
    public ResponseEntity<List<String>> getTransmissions(@PathVariable String configurationId,
                                                         @RequestParam String engineCapacity) {
        return ResponseEntity.ok(specificationService.getAvailableTransmissions(configurationId, engineCapacity));
    }

    @GetMapping("/{configurationId}/drive-types")
    public ResponseEntity<List<String>> getDriveTypes(@PathVariable String configurationId,
                                                      @RequestParam String engineCapacity,
                                                      @RequestParam String transmission) {
        return ResponseEntity.ok(specificationService.getAvailableDriveTypes(configurationId, engineCapacity, transmission));
    }

    @GetMapping("/{configurationId}/horsepowers")
    public ResponseEntity<List<String>> getHorsepowers(@PathVariable String configurationId,
                                                        @RequestParam String engineCapacity,
                                                        @RequestParam String transmission,
                                                        @RequestParam String driveType) {
        return ResponseEntity.ok(specificationService.getAvailableHorsepowers(configurationId, engineCapacity, transmission, driveType));
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