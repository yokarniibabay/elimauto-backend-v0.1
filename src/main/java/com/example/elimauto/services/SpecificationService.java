package com.example.elimauto.services;

import com.example.elimauto.DTO.EngineTransmissionDTO;
import com.example.elimauto.models.Specifications;
import com.example.elimauto.repositories.ModificationRepository;
import com.example.elimauto.repositories.SpecificationsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpecificationService {

    private final SpecificationsRepository specificationsRepository;
    private final ModificationRepository modificationRepository;

    public SpecificationService(SpecificationsRepository specificationsRepository,
                                ModificationRepository modificationRepository) {
        this.specificationsRepository = specificationsRepository;
        this.modificationRepository = modificationRepository;
    }

    /**
     * Получение доступных объемов двигателя (volumeLitres) и типов трансмиссий.
     */
    public List<EngineTransmissionDTO> getAvailableEngineAndTransmission(String configurationId) {
        // Шаг 1: Получить список complectationIds для configurationId
        List<String> complectationIds = modificationRepository.findComplectationIdsByConfigurationId(configurationId);

        // Шаг 2: Найти уникальные объемы двигателя и типы трансмиссий
        List<Object[]> results = specificationsRepository.findDistinctVolumeLitresAndTransmission(complectationIds);

        // Преобразование результата в DTO
        return results.stream()
                .map(result -> new EngineTransmissionDTO((String) result[0], (String) result[1]))
                .collect(Collectors.toList());
    }

    /**
     * Получение доступных объемов двигателя (volumeLitres).
     */
    public List<String> getAvailableEngineCapacities(String configurationId) {
        List<String> complectationIds = modificationRepository.findComplectationIdsByConfigurationId(configurationId);
        return specificationsRepository.findDistinctVolumeLitres(complectationIds);
    }

    /**
     * Получение доступных типов трансмиссий.
     */
    public List<String> getAvailableTransmissions(String configurationId, String volumeLitres) {
        List<String> complectationIds = modificationRepository.findComplectationIdsByConfigurationId(configurationId);
        return specificationsRepository.findDistinctTransmissions(complectationIds, volumeLitres);
    }

    /**
     * Получение доступных типов привода.
     */
    public List<String> getAvailableDriveTypes(String configurationId, String volumeLitres, String transmission) {
        List<String> complectationIds = modificationRepository.findComplectationIdsByConfigurationId(configurationId);
        return specificationsRepository.findDistinctDrives(complectationIds, volumeLitres, transmission);
    }

    /**
     * Получение доступных значений мощности двигателя (horsePower).
     */
    public List<String> getAvailableHorsepowers(String configurationId, String volumeLitres, String transmission, String drive) {
        List<String> complectationIds = modificationRepository.findComplectationIdsByConfigurationId(configurationId);
        return specificationsRepository.findDistinctHorsePowers(complectationIds, volumeLitres, transmission, drive);
    }

    /**
     * Получение спецификации на основе всех параметров.
     */
    public Specifications getSpecification(String configurationId, String volumeLitres, String transmission, String drive, String horsePower) {
        List<String> complectationIds = modificationRepository.findComplectationIdsByConfigurationId(configurationId);
        List<Specifications> specifications = specificationsRepository.findSpecifications(complectationIds, volumeLitres, transmission, drive, horsePower);

        if (specifications.size() == 1) {
            return specifications.get(0); // Уникальная модификация
        }

        throw new IllegalStateException("Не удалось определить уникальную модификацию");
    }
}