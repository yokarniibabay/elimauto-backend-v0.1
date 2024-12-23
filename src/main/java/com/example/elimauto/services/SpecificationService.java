package com.example.elimauto.services;

import com.example.elimauto.DTO.EngineTransmissionDTO;
import com.example.elimauto.DTO.SpecificationsDTO;
import com.example.elimauto.models.Announcement;
import com.example.elimauto.models.Specifications;
import com.example.elimauto.repositories.AnnouncementRepository;
import com.example.elimauto.repositories.ModificationRepository;
import com.example.elimauto.repositories.SpecificationsRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SpecificationService {

    private final AnnouncementRepository announcementRepository;
    private final SpecificationsRepository specificationsRepository;
    private final ModificationRepository modificationRepository;
    private final ModelMapper modelMapper;

    public SpecificationService(AnnouncementRepository announcementRepository,
                                SpecificationsRepository specificationsRepository,
                                ModificationRepository modificationRepository,
                                ModelMapper modelMapper) {
        this.announcementRepository = announcementRepository;
        this.specificationsRepository = specificationsRepository;
        this.modificationRepository = modificationRepository;
        this.modelMapper = modelMapper;
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
        log.info("Найдены complectationIds: {}", complectationIds);

        List<Specifications> specifications = specificationsRepository.findSpecifications(complectationIds,
                volumeLitres,
                transmission,
                drive,
                horsePower);
        log.info("Найдены specifications: {}", specifications);

        if (specifications.size() == 1) {
            return specifications.get(0);
        }

        throw new IllegalStateException("Не удалось определить уникальную модификацию");
    }

    public SpecificationsDTO getSpecificationsByAnnouncementId(Long announcementId) {
        // 1. Получить объявление
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new IllegalArgumentException("Объявление с ID " + announcementId + " не найдено."));

        // 2. Извлечь configuration_id
        String configurationId = announcement.getConfigurationId();

        // 3. Найти все complectation_id для configuration_id
        List<String> complectationIds = modificationRepository.findComplectationIdsByConfigurationId(configurationId);

        if (complectationIds.isEmpty()) {
            throw new IllegalStateException("Не найдены комплектации для configurationId: " + configurationId);
        }

        // 4. Найти подходящую запись specifications
        List<Specifications> specificationsList = specificationsRepository.findSpecificationsByComplectationIds(complectationIds);

        // Фильтрация спецификаций по дополнительным полям
        log.info("Фильтрация по следующим параметрам: volume={}, transmission={}, drive={}",
                announcement.getEngineCapacity(), announcement.getTransmissionType(), announcement.getDriveType());
        Specifications specification = specificationsList.stream()
                .filter(spec -> spec.getVolume().equals(String.valueOf(announcement.getEngineCapacity())) &&
                        spec.getTransmission().equals(announcement.getTransmissionType()) &&
                        spec.getDrive().equals(announcement.getDriveType()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Не удалось найти подходящую модификацию"));

        // 5. Преобразование в DTO
        return modelMapper.map(specification, SpecificationsDTO.class);
    }

    public String findCharacteristicsId(String configurationId, String volume, String transmission, String drive, String horsePower) {
        // Найти все complectationId для configurationId
        List<String> complectationIds = modificationRepository.findComplectationIdsByConfigurationId(configurationId);

        // Получить список specifications для этих complectationIds
        List<Specifications> specificationsList = specificationsRepository.findSpecificationsByComplectationIds(complectationIds);

        // Фильтровать спецификации по характеристикам
        return specificationsList.stream()
                .filter(spec -> spec.getVolume().equals(volume) &&
                        spec.getTransmission().equals(transmission) &&
                        spec.getDrive().equals(drive) &&
                        spec.getHorsePower().equals(horsePower))
                .map(Specifications::getComplectationId) // Возвратить complectationId
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Не удалось найти подходящую спецификацию"));
    }

    public void validateModification(String configurationId,
                                       String volumeLitres,
                                       String transmission,
                                       String drive,
                                       String horsePower) {
        List<String> complectationIds = modificationRepository.findComplectationIdsByConfigurationId(configurationId);

        boolean exists = specificationsRepository.existsSpecifications(complectationIds, volumeLitres, transmission, drive, horsePower);

        if (!exists) {
            throw new IllegalArgumentException(String.format(
                    "Модификация с параметрами [Configuration ID: %s, Volume: %s, " +
                            "Transmission: %s, Drive: %s, HorsePower: %s] не найдена.",
                    configurationId, volumeLitres, transmission, drive, horsePower));
        }
    }
}