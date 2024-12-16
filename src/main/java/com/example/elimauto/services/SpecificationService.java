package com.example.elimauto.services;

import com.example.elimauto.repositories.SpecificationsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecificationService {

    private final SpecificationsRepository specificationsRepository;

    public SpecificationService(SpecificationsRepository specificationsRepository) {
        this.specificationsRepository = specificationsRepository;
    }

    public List<String> getAvailableEngineCapacities(String configurationId) {
        return specificationsRepository.findDistinctEngineCapacities(configurationId);
    }

    public List<String> getAvailableTransmissions(String configurationId, String engineCapacity) {
        return specificationsRepository.findDistinctTransmissions(configurationId, engineCapacity);
    }

    public List<String> getAvailableDriveTypes(String configurationId, String engineCapacity, String transmission) {
        return specificationsRepository.findDistinctDriveTypes(configurationId, engineCapacity, transmission);
    }

    public List<String> getAvailableHorsepowers(String configurationId, String engineCapacity, String transmission, String driveType) {
        return specificationsRepository.findDistinctHorsepowers(configurationId, engineCapacity, transmission, driveType);
    }
}