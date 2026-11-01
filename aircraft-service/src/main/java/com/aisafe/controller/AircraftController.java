package com.aisafe.controller;

import com.aisafe.application.usecase.RegisterAircraftUseCase;
import com.aisafe.core.exception.AircraftNotFoundException;
import com.aisafe.model.Aircraft;
import com.aisafe.model.AircraftRegistration;
import com.aisafe.repository.AircraftRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/aircraft")
public class AircraftController {

    private final RegisterAircraftUseCase registerUseCase;
    private final AircraftRepository aircraftRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${peer.node.url:https://localhost:8082/api}")
    private String peerNodeUrl;

    public AircraftController(RegisterAircraftUseCase registerUseCase, AircraftRepository aircraftRepository) {
        this.registerUseCase = registerUseCase;
        this.aircraftRepository = aircraftRepository;
    }

    @Operation(summary = "Registar Aeronave")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ATCC')")
    public Aircraft registerAircraft(@RequestBody AircraftDTO dto) {
        return registerUseCase.execute(dto.registrationNumber, dto.modelId, dto.manufacturingDate, dto.status);
    }

    @Operation(summary = "Obter Detalhes da Aeronave (com P2P Forwarding)")
    @GetMapping("/{registration}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BACKOFFICE', 'ATCC', 'SERVICE')")
    public ResponseEntity<?> getAircraftDetails(
            @PathVariable String registration,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "X-Forwarded-Peer", defaultValue = "false") boolean isForwarded) {

        AircraftRegistration regVo = new AircraftRegistration(registration);
        Optional<Aircraft> aircraftLocal = aircraftRepository.findById(regVo);

        if (aircraftLocal.isPresent()) {
            return ResponseEntity.ok(aircraftLocal.get());
        }

        if (isForwarded) {
            throw new AircraftNotFoundException(registration);
        }

        try {
            System.out.println("Aeronave " + registration + " não encontrada. A iniciar P2P Forwarding para: " + peerNodeUrl);

            HttpHeaders headers = new HttpHeaders();
            if (token != null) {
                headers.set("Authorization", token);
            }
            headers.set("X-Forwarded-Peer", "true");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Aircraft> peerResponse = restTemplate.exchange(
                    peerNodeUrl + "/aircraft/" + registration,
                    HttpMethod.GET,
                    entity,
                    Aircraft.class
            );

            return ResponseEntity.ok(peerResponse.getBody());

        } catch (HttpClientErrorException.NotFound e) {
            throw new AircraftNotFoundException(registration);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro no P2P Forwarding: " + e.getMessage());
        }
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

    @Operation(summary = "Fornecer capacidades da aeronave para cálculo de rotas no serviço externo")
    @GetMapping("/{registration}/specs")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATCC', 'SERVICE')")
    public ResponseEntity<?> getAircraftSpecs(@PathVariable String registration) {
        AircraftRegistration regVo = new AircraftRegistration(registration);

        Aircraft aircraft = aircraftRepository.findById(regVo)
                .orElseThrow(() -> new AircraftNotFoundException(registration));

        AircraftSpecsDTO specs = new AircraftSpecsDTO(
                aircraft.getRegistrationNumber().getRegistration(),
                150,
                3500.0,
                800.0
        );

        return ResponseEntity.ok(specs);
    }
}