package com.aisafe.application.usecase;

import com.aisafe.core.exception.DuplicateAircraftRegistrationException;
import com.aisafe.core.exception.ResourceNotFoundException;
import com.aisafe.cqrs.event.AircraftEvent;
import com.aisafe.cqrs.event.AircraftEventPublisher;
import com.aisafe.cqrs.query.AircraftModelReadModelRepository;
import com.aisafe.model.Aircraft;
import com.aisafe.model.AircraftRegistration;
import com.aisafe.repository.AircraftModelRepository;
import com.aisafe.repository.AircraftRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class RegisterAircraftUseCase {

    private final AircraftRepository aircraftRepository;
    private final AircraftModelRepository modelRepository;
    private final AircraftModelReadModelRepository modelReadRepository;
    private final AircraftEventPublisher eventPublisher;

    public RegisterAircraftUseCase(AircraftRepository aircraftRepository,
                                   AircraftModelRepository modelRepository,
                                   AircraftModelReadModelRepository modelReadRepository,
                                   AircraftEventPublisher eventPublisher) {
        this.aircraftRepository = aircraftRepository;
        this.modelRepository = modelRepository;
        this.modelReadRepository = modelReadRepository;
        this.eventPublisher = eventPublisher;
    }

    public Aircraft execute(String registrationNumber, String modelId, LocalDate manufacturingDate, String status) {
        AircraftRegistration registration = new AircraftRegistration(registrationNumber);

        if (aircraftRepository.existsById(registration)) {
            throw new DuplicateAircraftRegistrationException(registrationNumber);
        }

        if (!modelRepository.existsById(modelId) && !modelReadRepository.existsById(modelId)) {
            throw new ResourceNotFoundException("Modelo de aeronave não encontrado.");
        }

        Aircraft aircraft = new Aircraft();
        aircraft.setRegistrationNumber(registration);
        aircraft.setModelId(modelId);
        aircraft.setManufacturingDate(manufacturingDate);
        aircraft.setStatus(status.toUpperCase());

        Aircraft saved = aircraftRepository.save(aircraft);

        eventPublisher.publish(new AircraftEvent(
                "CREATED",
                saved.getRegistrationNumber().getRegistration(),
                saved.getModelId(),
                saved.getManufacturingDate(),
                saved.getStatus()
        ));

        return saved;
    }
}