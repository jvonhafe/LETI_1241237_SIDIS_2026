package com.aisafe.controller;

import com.aisafe.application.aircraft.RegisterAircraftModelUseCase;
import com.aisafe.application.aircraft.UpdateAircraftModelUseCase;
import com.aisafe.application.aircraft.GetFleetAvailabilityUseCase;
import com.aisafe.application.aircraft.GetTop5UtilizedModelsUseCase;
import com.aisafe.application.aircraft.CalculateFleetOperationalHoursUseCase;
import com.aisafe.model.AircraftModel;
import com.aisafe.model.FleetAvailabilityDTO;
import com.aisafe.model.UpdateAircraftModelDTO;
import com.aisafe.model.AircraftModelUtilizationDTO;
import com.aisafe.model.AircraftOperationalHoursDTO;
import com.aisafe.repository.AircraftModelRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/aircraft-models")
public class AircraftModelController {

    private final RegisterAircraftModelUseCase registerUseCase;
    private final AircraftModelRepository modelRepository;
    private final UpdateAircraftModelUseCase updateAircraftModelUseCase;
    private final GetTop5UtilizedModelsUseCase getTop5UtilizedModelsUseCase;
    private final GetFleetAvailabilityUseCase getFleetAvailabilityUseCase;
    private final CalculateFleetOperationalHoursUseCase calculateFleetOperationalHoursUseCase;

    public AircraftModelController(RegisterAircraftModelUseCase registerUseCase,
                                   AircraftModelRepository modelRepository,
                                   UpdateAircraftModelUseCase updateAircraftModelUseCase,
                                   GetTop5UtilizedModelsUseCase getTop5UtilizedModelsUseCase,
                                   GetFleetAvailabilityUseCase getFleetAvailabilityUseCase,
                                   CalculateFleetOperationalHoursUseCase calculateFleetOperationalHoursUseCase) {
        this.registerUseCase = registerUseCase;
        this.modelRepository = modelRepository;
        this.updateAircraftModelUseCase = updateAircraftModelUseCase;
        this.getTop5UtilizedModelsUseCase = getTop5UtilizedModelsUseCase;
        this.getFleetAvailabilityUseCase = getFleetAvailabilityUseCase;
        this.calculateFleetOperationalHoursUseCase = calculateFleetOperationalHoursUseCase;
    }

    @Operation(summary = "Registar Modelo de Aeronave")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BACKOFFICE')")
    public AircraftModel registerModel(@RequestBody AircraftModelDTO dto) {
        String url = (dto.image != null) ? dto.image.imageUrl : null;
        String desc = (dto.image != null) ? dto.image.description : null;

        return registerUseCase.execute(dto.modelId, dto.modelName, dto.manufacturer,
                dto.maxCapacity, dto.fuelCapacity, dto.maxRange, dto.cruisingSpeed, url, desc);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BACKOFFICE', 'ATCC')")
    public List<AircraftModel> getAllModels() {
        return modelRepository.findAll();
    }

    @Operation(summary = "Atualizar Especificações do Modelo")
    @PutMapping("/{modelId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BACKOFFICE')")
    public ResponseEntity<AircraftModel> updateModelSpecifications(
            @PathVariable String modelId,
            @RequestBody UpdateAircraftModelDTO updateDTO) {

        AircraftModel updatedModel = updateAircraftModelUseCase.execute(modelId, updateDTO);
        return ResponseEntity.ok(updatedModel);
    }

    public static class ImageDTO {
        public String imageUrl;
        public String description;
    }

    public static class AircraftModelDTO {
        public String modelId;
        public String modelName;
        public String manufacturer;
        public int maxCapacity;
        public double fuelCapacity;
        public double maxRange;
        public double cruisingSpeed;
        public ImageDTO image;
    }

    @Operation(summary = "Top 5 Modelos Mais Utilizados")
    @GetMapping("/top5")
    @PreAuthorize("hasAnyRole('ADMIN', 'BACKOFFICE')")
    public ResponseEntity<List<AircraftModelUtilizationDTO>> getTop5MostUtilized() {
        List<AircraftModelUtilizationDTO> top5 = getTop5UtilizedModelsUseCase.execute();
        return ResponseEntity.ok(top5);
    }

    @Operation(summary = "Disponibilidade da Frota")
    @GetMapping("/fleet/availability")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATCC')")
    public ResponseEntity<FleetAvailabilityDTO> getFleetAvailability() {
        FleetAvailabilityDTO availability = getFleetAvailabilityUseCase.execute();
        return ResponseEntity.ok(availability);
    }

    @Operation(summary = "Calcular Horas Operacionais")
    @GetMapping("/operational-hours")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATCC')")
    public ResponseEntity<List<AircraftOperationalHoursDTO>> getOperationalHours() {
        List<AircraftOperationalHoursDTO> hours = calculateFleetOperationalHoursUseCase.execute();
        return ResponseEntity.ok(hours);
    }
}