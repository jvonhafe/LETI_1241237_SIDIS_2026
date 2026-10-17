package com.aisafe.controller;

import com.aisafe.model.MaintenanceTemplate;
import com.aisafe.service.MaintenanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Maintenance Templates",
        description = "Gestão de Templates de Manutenção (US115)"
)
@RestController
@RequestMapping("/api/maintenance-templates")
public class MaintenanceTemplateController {

    private final MaintenanceService maintenanceService;

    public MaintenanceTemplateController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @Operation(summary = "Criar Template de Manutenção")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MaintenanceTemplate createTemplate(@Valid @RequestBody MaintenanceTemplate template) {
        return maintenanceService.createTemplate(template);
    }
}