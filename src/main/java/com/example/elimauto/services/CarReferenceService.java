package com.example.elimauto.services;

import com.example.elimauto.DTO.*;
import com.example.elimauto.models.*;
import com.example.elimauto.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ModelMapper modelMapper;

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

    public List<MarkDTO> getAllMarks() {
        List<Mark> marks = markRepository.findAllByOrderByNameAsc();
        return marks.stream().map(this::convertToMarkDTO).collect(Collectors.toList());
    }

    public List<MarkNameDTO> getAllMarksOrderedByPopular() {
        List<Mark> marks = markRepository.findAllByOrderByPopularDescNameAsc();
        return marks.stream()
                .map(mark -> modelMapper.map(mark, MarkNameDTO.class))
                .collect(Collectors.toList());
    }

    public MarkDTO getMarkDTOById(String markId) {
        Mark mark = getMarkById(markId);
        return convertToMarkDTO(mark);
    }

    private Mark getMarkById(String markId) {
        return markRepository.findById(markId)
                .orElseThrow(() -> new EntityNotFoundException("Марка с ID " + markId + " не найдена."));
    }


    //MODELS

    public List<ModelNameDTO> getModelsByMark(String markId) {
        List<Model> models = modelRepository.findByMarkIdOrderByNameAsc(markId);
        return models.stream()
                .map(model -> modelMapper.map(model, ModelNameDTO.class))
                .collect(Collectors.toList());
    }

    public ModelDTO getModelById(String modelId) {
        Model model = modelRepository.findById(modelId)
                .orElseThrow(() -> new EntityNotFoundException("Модель с ID " + modelId + " не найдена."));
        Mark mark = getMarkById(model.getMark().getId());

        return new ModelDTO(
                model.getId(),
                model.getName(),
                model.getCyrillicName(),
                model.getCarClass(),
                model.getYearFrom(),
                model.getYearTo()
        );
    }


    //GENERATIONS

    public List<GenerationDTO> getGenerationsByModel(String modelId) {
        List<Generation> generations = generationRepository.findByModelIdOrderByYearStart(modelId);
        return generations.stream()
                .map(generation -> modelMapper.map(generation, GenerationDTO.class))
                .collect(Collectors.toList());
    }


    //CONFIGURATIONS

    public List<ConfigurationDTO> getConfigurationsByGeneration(String generationId) {
        List<Configuration> configurations = configurationRepository.findByGenerationIdOrderByDoorsCount(generationId);
        return configurations.stream()
                .map(configuration -> modelMapper.map(configurations, ConfigurationDTO.class))
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


    //ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
    private MarkDTO convertToMarkDTO(Mark mark) {
        List<ModelDTO> modelDTOs = mark.getModels().stream()
                .map(this::convertToModelDTO)
                .collect(Collectors.toList());

        return new MarkDTO(
                mark.getId(),
                mark.getName(),
                mark.getCyrillicName(),
                mark.isPopular(),
                mark.getCountry(),
                modelDTOs
        );
    }

    private ModelDTO convertToModelDTO(Model model) {
        return new ModelDTO(
                model.getId(),
                model.getName(),
                model.getCyrillicName(),
                model.getCarClass(),
                model.getYearFrom(),
                model.getYearTo()
        );
    }
}
