package com.aisafe.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class MaintenanceTemplate {
    @Id
    @NotBlank(message = "O ID do template é obrigatório.")
    private String templateId;

    @NotBlank(message = "O nome do template é obrigatório.")
    private String templateName;

    @NotBlank(message = "O tipo de manutenção é obrigatório.")
    private String templateType; // INSPECTION, SCHEDULED_MAINTENANCE, OVERHAUL, MODIFICATION

    @ElementCollection
    @NotEmpty(message = "O template deve aplicar-se a pelo menos um modelo de avião.")
    private List<String> applicableAircraftModelIds = new ArrayList<>();

    @ElementCollection
    @NotEmpty(message = "A checklist não pode estar vazia.")
    private List<String> checklist = new ArrayList<>();
}
