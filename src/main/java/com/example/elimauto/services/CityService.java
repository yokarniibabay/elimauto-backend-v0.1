package com.example.elimauto.services;

import com.example.elimauto.models.City;
import com.example.elimauto.repositories.CityRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CityService {
    private final CityRepository cityRepository;

    @Autowired
    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public List<String> getCitiesSortedByNameRu() {
        return cityRepository.findAllByOrderByNameRuAsc()
                .stream()
                .map(City::getNameRu)
                .collect(Collectors.toList());
    }

    public List<String> getCitiesSortedByNameKk() {
        return cityRepository.findAllByOrderByNameKkAsc()
                .stream()
                .map(City::getNameKk)
                .collect(Collectors.toList());
    }

    public String getCityNameKkBy(String cityRu) {
        return cityRepository.findByNameRu(cityRu) // возвращает Optional
                .map(City::getNameKk)
                .orElseThrow(() -> new EntityNotFoundException("Город не найден: " + cityRu));
    }
}
