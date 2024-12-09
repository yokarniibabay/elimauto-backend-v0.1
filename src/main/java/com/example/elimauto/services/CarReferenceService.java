package com.example.elimauto.services;

import com.example.elimauto.DTO.MarkDTO;
import com.example.elimauto.DTO.ModelDTO;
import com.example.elimauto.DTO.ModelDetailDTO;
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

    public List<MarkDTO> getAllMarks() {
        List<Mark> marks = markRepository.findAllByOrderByNameAsc();
        return marks.stream().map(this::convertToMarkDTO).collect(Collectors.toList());
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

    public List<ModelDTO> getModelsByMark(String markId) {
        List<Model> models = modelRepository.findByMarkIdOrderByNameAsc(markId);
        return models.stream().map(this::convertToModelDTO).collect(Collectors.toList());
    }

    public ModelDetailDTO getModelDetailById(String modelId) {
        Model model = modelRepository.findById(modelId)
                .orElseThrow(() -> new EntityNotFoundException("Модель с ID " + modelId + " не найдена."));
        Mark mark = getMarkById(model.getMark().getId());

        return new ModelDetailDTO(
                model.getId(),
                model.getName(),
                model.getCyrillicName(),
                model.getCarClass(),
                model.getYearFrom(),
                model.getYearTo(),
                mark.getName()
        );
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
