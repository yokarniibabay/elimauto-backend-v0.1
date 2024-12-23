package com.example.elimauto.controllers;

import com.example.elimauto.DTO.*;
import com.example.elimauto.models.*;
import com.example.elimauto.services.CarReferenceService;
import com.example.elimauto.services.SpecificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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
    public ResponseEntity<List<ModelNameDTO>> getModelDTOsByMark(@PathVariable String markId) {
        List<ModelNameDTO> modelsByMark = carReferenceService.getModelDTOsByMark(markId);
        if (modelsByMark.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(modelsByMark);
    }

    @GetMapping("/makes/{markId}")
    public ResponseEntity<List<Model>> getModelsByMark(@PathVariable String markId) {
        List<Model> modelsByMark = carReferenceService.getModelsByMark(markId);
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

    /**
     * Получение доступных объемов двигателя (volumeLitres) и типов трансмиссий.
     */
    @GetMapping("/{configurationId}/available-params")
    public ResponseEntity<List<EngineTransmissionDTO>> getAvailableParams(@PathVariable String configurationId) {
        List<EngineTransmissionDTO> params = specificationService.getAvailableEngineAndTransmission(configurationId);
        return ResponseEntity.ok(params);
    }

    /**
     * Получение доступных объемов двигателя (volumeLitres).
     */
    @GetMapping("/{configurationId}/engine-capacities")
    public ResponseEntity<List<String>> getEngineCapacities(@PathVariable String configurationId) {
        List<String> engineCapacities = specificationService.getAvailableEngineCapacities(configurationId)
                .stream()
                .sorted(Comparator.comparingDouble(Double::parseDouble))
                .collect(Collectors.toList());
        return ResponseEntity.ok(engineCapacities);
    }

    /**
     * Получение доступных типов трансмиссий.
     */
    @GetMapping("/{configurationId}/{volumeLitres}/transmissions")
    public ResponseEntity<List<String>> getTransmissions(@PathVariable String configurationId,
                                                         @PathVariable String volumeLitres) {
        List<String> transmissions = specificationService.getAvailableTransmissions(configurationId,
                        volumeLitres)
                .stream()
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transmissions);
    }

    /**
     * Получение доступных типов привода.
     */
    @GetMapping("/{configurationId}/{volumeLitres}/{transmission}/drive-types")
    public ResponseEntity<List<String>> getDriveTypes(@PathVariable String configurationId,
                                                      @PathVariable String volumeLitres,
                                                      @PathVariable String transmission) {
        List<String> driveTypes = specificationService.getAvailableDriveTypes(configurationId,
                        volumeLitres,
                        transmission)
                .stream()
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList());
        return ResponseEntity.ok(driveTypes);
    }

    /**
     * Получение доступных значений мощности двигателя (horsePower).
     */
    @GetMapping("/{configurationId}/{volumeLitres}/{transmission}/{drive}/horsepowers")
    public ResponseEntity<List<String>> getHorsepowers(@PathVariable String configurationId,
                                                       @PathVariable String volumeLitres,
                                                       @PathVariable String transmission,
                                                       @PathVariable String drive) {
        List<String> horsepowers = specificationService.getAvailableHorsepowers(configurationId,
                        volumeLitres,
                        transmission,
                        drive)
                .stream()
                .sorted(Comparator.comparingInt(Integer::parseInt))
                .collect(Collectors.toList());
        return ResponseEntity.ok(horsepowers);
    }

    /**
     * Получение спецификации на основе всех параметров.
     */
    @GetMapping("/{configurationId}/{volumeLitres}/{transmission}/{drive}/{horsePower}/specification")
    public ResponseEntity<Specifications> getSpecification(@PathVariable String configurationId,
                                                           @PathVariable String volumeLitres,
                                                           @PathVariable String transmission,
                                                           @PathVariable String drive,
                                                           @PathVariable String horsePower) {
        Specifications specification =
                specificationService.getSpecification(configurationId, volumeLitres, transmission, drive, horsePower);
        return ResponseEntity.ok(specification);
    }

    /**
     * Валидация выбранных параметров.
     */

    @GetMapping("/{configurationId}/{volumeLitres}/{transmission}/{drive}/{horsePower}/validate-modification")
    public ResponseEntity<Void> validateModification(@PathVariable String configurationId,
                                                        @PathVariable String volumeLitres,
                                                        @PathVariable String transmission,
                                                        @PathVariable String drive,
                                                        @PathVariable String horsePower) {
        specificationService.validateModification(configurationId, volumeLitres, transmission, drive, horsePower);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/options")
    public Options getOptions(@PathVariable String complectationId) {
        return carReferenceService.getOptionsByComplectation(complectationId);
    }
}