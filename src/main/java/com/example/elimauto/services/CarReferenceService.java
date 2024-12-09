package com.example.elimauto.services;

import com.example.elimauto.models.*;
import com.example.elimauto.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarReferenceService {
    private final MarkRepository markRepository;
    private final ModelRepository modelRepository;
    private final GenerationRepository generationRepository;
    private final ConfigurationRepository configurationRepository;
    private final ModificationRepository modificationRepository;
    private final SpecificationsRepository specificationsRepository;
    private final OptionsRepository optionsRepository;

    public CarReferenceService(MarkRepository markRepository,
                               ModelRepository modelRepository,
                               GenerationRepository generationRepository,
                               ConfigurationRepository configurationRepository,
                               ModificationRepository modificationRepository,
                               SpecificationsRepository specificationsRepository,
                               OptionsRepository optionsRepository) {
        this.markRepository = markRepository;
        this.modelRepository = modelRepository;
        this.generationRepository = generationRepository;
        this.configurationRepository = configurationRepository;
        this.modificationRepository = modificationRepository;
        this.specificationsRepository = specificationsRepository;
        this.optionsRepository = optionsRepository;
    }


    //MARKS

    public List<Mark> getAllMarks() {
        return markRepository.findAll();
    }

    public Mark getMarkById(String markId) {
        return markRepository.findById(markId)
                .orElseThrow(() -> new EntityNotFoundException("Марка с ID " + markId + " не найдена."));
    }


    //MODELS

    public List<Model> getModelsByMark(String markId) {
        return modelRepository.findAll().stream()
                .filter(m -> m.getMark().toString().equals(markId))
                .collect(Collectors.toList());
    }

    public Model getModelById(String modelId) {
        return modelRepository.findById(modelId)
                .orElseThrow(() -> new EntityNotFoundException("Модель с ID " + modelId + " не найдена."));
    }


    //GENERATIONS

    public List<Generation> getGenerationsByModel(String modelId) {
        return generationRepository.findAll().stream()
                .filter(g -> g.getModelId().equals(modelId))
                .collect(Collectors.toList());
    }


    //CONFIGURATIONS

    public List<Configuration> getConfigurationsByGeneration(String generationId) {
        return configurationRepository.findAll().stream()
                .filter(c -> c.getGenerationId().equals(generationId))
                .collect(Collectors.toList());
    }


    //MODIFICATIONS

    public List<Modification> getModificationsByConfiguration(String configurationId) {
        return modificationRepository.findAll().stream()
                .filter(m -> m.getConfigurationId().equals(configurationId))
                .collect(Collectors.toList());
    }

    public Specifications getSpecificationsByComplectation(String complectationId) {
        return specificationsRepository.findById(complectationId).orElse(null);
    }

    public Options getOptionsByComplectation(String complectationId) {
        return optionsRepository.findById(complectationId).orElse(null);
    }
}
