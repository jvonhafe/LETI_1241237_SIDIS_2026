package com.aisafe.cqrs.query;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "maintenance_read_model")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceReadModel {

    @Id
    private String id;
    private String aircraftRegistration;
    private Long templateId;
    private Double expectedDurationHours;
    private String status;
    private String completionNotes;
    private String component;
    private Double cost;
}