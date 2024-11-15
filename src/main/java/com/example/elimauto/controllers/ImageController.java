package com.example.elimauto.controllers;

import com.example.elimauto.models.Image;
import com.example.elimauto.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getImageById(@PathVariable Long id) {
        Optional<Image> imageOptional = imageService.getImageById(id);

        if (imageOptional.isPresent()) {
            Image image = imageOptional.get();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", image.getContentType());
            return new ResponseEntity<>(image.getBytes(), headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
