package com.aisafe.controller;

import com.aisafe.application.usecase.RegisterAircraftUseCase;
import com.aisafe.core.exception.AircraftNotFoundException;
import com.aisafe.cqrs.event.AircraftEvent;
import com.aisafe.cqrs.event.AircraftEventPublisher;
import com.aisafe.cqrs.query.AircraftReadModel;
import com.aisafe.cqrs.query.AircraftReadModelRepository;
import com.aisafe.model.Aircraft;
import com.aisafe.model.AircraftRegistration;
import com.aisafe.repository.AircraftRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/aircraft")
public class AircraftController {

    private final RegisterAircraftUseCase registerUseCase;
    private final AircraftRepository aircraftRepository;
    private final AircraftReadModelRepository readRepository;
    private final AircraftEventPublisher eventPublisher;

    public AircraftController(RegisterAircraftUseCase registerUseCase,
                              AircraftRepository aircraftRepository,
                              AircraftReadModelRepository readRepository,
                              AircraftEventPublisher eventPublisher) {
        this.registerUseCase = registerUseCase;
        this.aircraftRepository = aircraftRepository;
        this.readRepository = readRepository;
        this.eventPublisher = eventPublisher;
    }

    //commands (escrita)

    @Operation(summary = "Registar Aeronave (Command)")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ATCC')")
    public Aircraft registerAircraft(@RequestBody AircraftDTO dto) {
        return registerUseCase.execute(dto.registrationNumber, dto.modelId, dto.manufacturingDate, dto.status);
    }

    @Operation(summary = "Atualizar Estado da Aeronave (Command)")
    @PatchMapping("/{registration}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATCC')")
    public Aircraft updateStatus(@PathVariable String registration, @RequestParam String newStatus) {
        AircraftRegistration regVo = new AircraftRegistration(registration);
        Aircraft aircraft = aircraftRepository.findById(regVo)
                .orElseThrow(() -> new AircraftNotFoundException(registration));

        aircraft.setStatus(newStatus.toUpperCase());
        Aircraft updated = aircraftRepository.save(aircraft);

        eventPublisher.publish(new AircraftEvent(
                "UPDATED",
                updated.getRegistrationNumber().getRegistration(),
                updated.getModelId(),
                updated.getManufacturingDate(),
                updated.getStatus()
        ));

        return updated;
    }

    //queries (leitura - CQRS)

    @Operation(summary = "Obter Detalhes da Aeronave (Query - Read Model)")
    @GetMapping("/{registration}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BACKOFFICE', 'ATCC', 'SERVICE')")
    public ResponseEntity<AircraftReadModel> getAircraftDetails(@PathVariable String registration) {
        AircraftReadModel readModel = readRepository.findById(registration)
                .orElseThrow(() -> new AircraftNotFoundException(registration));

        return ResponseEntity.ok(readModel);
    }

    @Operation(summary = "Pesquisar Aeronaves (Query - Read Model)")
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATCC')")
    public List<AircraftReadModel> searchAircrafts(@RequestParam(required = false) String modelId,
                                                   @RequestParam(required = false) String status,
                                                   @RequestParam(required = false) Integer year) {
        return readRepository.findAll().stream()
                .filter(a -> modelId == null || modelId.equalsIgnoreCase(a.getModelId()))
                .filter(a -> status == null || status.equalsIgnoreCase(a.getStatus()))
                .filter(a -> year == null || (a.getManufacturingDate() != null && a.getManufacturingDate().getYear() == year))
                .toList();
    }

    @Operation(summary = "Fornecer capacidades da aeronave (Query - Read Model)")
    @GetMapping("/{registration}/specs")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATCC', 'SERVICE')")
    public ResponseEntity<AircraftSpecsDTO> getAircraftSpecs(@PathVariable String registration) {
        AircraftReadModel aircraft = readRepository.findById(registration)
                .orElseThrow(() -> new AircraftNotFoundException(registration));

        AircraftSpecsDTO specs = new AircraftSpecsDTO(
                aircraft.getRegistration(),
                150,
                3500.0,
                800.0
        );

        return ResponseEntity.ok(specs);
    }

    //dtos

    public static class AircraftDTO {
        public String registrationNumber;
        public String modelId;
        public LocalDate manufacturingDate;
        public String status;
    }

    public static class AircraftSpecsDTO {
        public String registration;
        public int maxCapacity;
        public double maxRange;
        public double cruisingSpeed;

        public AircraftSpecsDTO(String registration, int maxCapacity, double maxRange, double cruisingSpeed) {
            this.registration = registration;
            this.maxCapacity = maxCapacity;
            this.maxRange = maxRange;
            this.cruisingSpeed = cruisingSpeed;
        }
    }
}