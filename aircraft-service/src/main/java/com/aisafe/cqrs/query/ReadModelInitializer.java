package com.aisafe.cqrs.query;

import com.aisafe.repository.AircraftRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ReadModelInitializer implements CommandLineRunner {

    private final AircraftRepository writeRepository;
    private final AircraftReadModelRepository readRepository;

    public ReadModelInitializer(AircraftRepository writeRepository, AircraftReadModelRepository readRepository) {
        this.writeRepository = writeRepository;
        this.readRepository = readRepository;
    }

    @Override
    public void run(String... args) {
        writeRepository.findAll().forEach(a -> readRepository.save(new AircraftReadModel(
                a.getRegistrationNumber().getRegistration(),
                a.getModelId(),
                a.getManufacturingDate(),
                a.getStatus()
        )));
    }
}