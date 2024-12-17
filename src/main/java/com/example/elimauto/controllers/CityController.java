package com.example.elimauto.controllers;

import com.example.elimauto.services.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/cities")
public class CityController {

    private final CityService cityService;

    @Autowired
    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping
    public ResponseEntity<List<String>> getCities() {
        Locale locale = LocaleContextHolder.getLocale();

        if (locale.getLanguage().equals("kk")) {
            return ResponseEntity.ok(cityService.getCitiesSortedByNameKk());
        }
        return ResponseEntity.ok(cityService.getCitiesSortedByNameRu());
    }
}
