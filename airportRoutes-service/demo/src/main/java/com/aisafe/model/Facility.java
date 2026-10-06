package com.aisafe.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Facility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;
    private int capacity;
    private String description;
}