package com.aisafe.controller;

import com.aisafe.application.aircraft.RegisterAircraftUseCase;
import com.aisafe.core.exception.AircraftNotFoundException;
import com.aisafe.model.Aircraft;
import com.aisafe.model.AircraftRegistration;
import com.aisafe.model.Route;
import com.aisafe.repository.AircraftRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.aisafe.application.aircraft.GetCompatibleRoutesUseCase;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/aircraft")
public class AircraftController {

    private final RegisterAircraftUseCase registerUseCase;
    private final AircraftRepository aircraftRepository;
    private final GetCompatibleRoutesUseCase getCompatibleRoutesUseCase;

    public AircraftController(RegisterAircraftUseCase registerUseCase, AircraftRepository aircraftRepository, GetCompatibleRoutesUseCase getCompatibleRoutesUseCase) {
        this.registerUseCase = registerUseCase;
        this.aircraftRepository = aircraftRepository;
        this.getCompatibleRoutesUseCase = getCompatibleRoutesUseCase;
    }

    @Operation(summary = "Registar Aeronave")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ATCC')")
    public Aircraft registerAircraft(@RequestBody AircraftDTO dto) {
        return registerUseCase.execute(dto.registrationNumber, dto.modelId, dto.manufacturingDate, dto.status);
    }

    @Operation(summary = "Obter Detalhes da Aeronave")
    @GetMapping("/{registration}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BACKOFFICE', 'ATCC')")
    public Aircraft getAircraftDetails(@PathVariable String registration) {
        AircraftRegistration regVo = new AircraftRegistration(registration);
        return aircraftRepository.findById(regVo)
                .orElseThrow(() -> new AircraftNotFoundException(registration));
    }

    @Operation(summary = "Pesquisar Aeronaves")
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATCC')")
    public List<Aircraft> searchAircrafts(@RequestParam(required = false) String modelId,
                                          @RequestParam(required = false) String status,
                                          @RequestParam(required = false) Integer year) {
        return aircraftRepository.searchAircrafts(modelId, status, year);
    }

    @Operation(summary = "Atualizar Estado da Aeronave")
    @PatchMapping("/{registration}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATCC')")
    public Aircraft updateStatus(@PathVariable String registration, @RequestParam String newStatus) {
        AircraftRegistration regVo = new AircraftRegistration(registration);
        Aircraft aircraft = aircraftRepository.findById(regVo)
                .orElseThrow(() -> new AircraftNotFoundException(registration));

        aircraft.setStatus(newStatus.toUpperCase());
        return aircraftRepository.save(aircraft);
    }

    public static class AircraftDTO {
        public String registrationNumber;
        public String modelId;
        public LocalDate manufacturingDate;
        public String status;
    }

    @Operation(summary = "Rotas Compatíveis")
    @GetMapping("/{registration}/compatible-routes")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATCC')")
    public ResponseEntity<List<Route>> getCompatibleRoutes(@PathVariable String registration) {
        List<Route> compatibleRoutes = getCompatibleRoutesUseCase.execute(registration);
        return ResponseEntity.ok(compatibleRoutes);
    }
}