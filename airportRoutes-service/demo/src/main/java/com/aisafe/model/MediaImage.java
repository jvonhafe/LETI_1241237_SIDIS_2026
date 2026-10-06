package com.aisafe.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class MediaImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;
    private String description;
}