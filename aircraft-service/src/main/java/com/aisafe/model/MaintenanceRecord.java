package com.aisafe.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class MaintenanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;


    @Version
    private Long version;

    private String aircraftRegistration;
    private Long templateId;

    private Double expectedDurationHours;

    @ElementCollection
    private List<String> checklist;

    private String status; // SCHEDULED, IN_PROGRESS, COMPLETED
    private String completionNotes;

    // US217: Categorizar por componente
    @Enumerated(EnumType.STRING)
    private MaintenanceComponent component;

    // US220: Gestão de custos
    private Double cost;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "startDate", column = @Column(name = "period_start_date")),
            @AttributeOverride(name = "endDate", column = @Column(name = "period_end_date"))
    })
    private MaintenancePeriod period;

    public void complete(String notes) {
        this.status = "COMPLETED";
        this.completionNotes = notes;
    }
}