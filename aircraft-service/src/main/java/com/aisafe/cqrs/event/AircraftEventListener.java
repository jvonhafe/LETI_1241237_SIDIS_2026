package com.aisafe.cqrs.event;

import com.aisafe.cqrs.query.AircraftModelReadModel;
import com.aisafe.cqrs.query.AircraftModelReadModelRepository;
import com.aisafe.cqrs.query.AircraftReadModel;
import com.aisafe.cqrs.query.AircraftReadModelRepository;
import com.aisafe.cqrs.query.MaintenanceReadModel;
import com.aisafe.cqrs.query.MaintenanceReadModelRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AircraftEventListener {

    private final AircraftReadModelRepository readRepository;
    private final AircraftModelReadModelRepository modelReadRepository;
    private final MaintenanceReadModelRepository maintenanceReadRepository;

    public AircraftEventListener(AircraftReadModelRepository readRepository,
                                 AircraftModelReadModelRepository modelReadRepository,
                                 MaintenanceReadModelRepository maintenanceReadRepository) {
        this.readRepository = readRepository;
        this.modelReadRepository = modelReadRepository;
        this.maintenanceReadRepository = maintenanceReadRepository;
    }

    @RabbitListener(queues = "#{aircraftSyncQueue.name}")
    public void handleAircraftEvent(AircraftEvent event) {
        System.out.println(">>> [CQRS SYNC] A processar evento: " + event);

        if ("DELETED".equals(event.eventType())) {
            readRepository.deleteById(event.registration());
        } else {
            AircraftReadModel view = new AircraftReadModel(
                    event.registration(),
                    event.modelId(),
                    event.manufacturingDate(),
                    event.status()
            );
            readRepository.save(view);
        }
    }

    @RabbitListener(queues = "#{modelSyncQueue.name}")
    public void handleModelEvent(AircraftModelEvent event) {
        System.out.println(">>> [CQRS SYNC MODEL] Evento recebido: " + event);

        AircraftModelReadModel view = new AircraftModelReadModel(
                event.modelId(),
                event.modelName(),
                event.manufacturer(),
                event.maxCapacity(),
                event.fuelCapacity(),
                event.maxRange(),
                event.cruisingSpeed()
        );
        modelReadRepository.save(view);
    }

    @RabbitListener(queues = "#{maintenanceSyncQueue.name}")
    public void handleMaintenanceEvent(MaintenanceEvent event) {
        System.out.println(">>> [CQRS SYNC MAINTENANCE] Evento recebido: " + event);

        MaintenanceReadModel view = new MaintenanceReadModel(
                event.id(),
                event.aircraftRegistration(),
                event.templateId(),
                event.expectedDurationHours(),
                event.status(),
                event.completionNotes(),
                event.component(),
                event.cost()
        );
        maintenanceReadRepository.save(view);
    }
}