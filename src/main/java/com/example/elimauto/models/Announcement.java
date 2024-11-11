package com.example.elimauto.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Announcement {
    private Long id;
    private String title;
    private String description;
    private double price;
    private String city;
    private String author;

}
