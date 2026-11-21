package com.aisafe.cqrs.event;

import com.aisafe.cqrs.query.FlightReadModel;
import com.aisafe.cqrs.query.FlightReadModelRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class FlightEventListener {

    private final FlightReadModelRepository readRepository;

    public FlightEventListener(FlightReadModelRepository readRepository) {
        this.readRepository = readRepository;
    }

    @RabbitListener(queues = "#{flightSyncQueue.name}")
    public void handleFlightEvent(FlightEvent event) {
        System.out.println(">>> [CQRS SYNC FLIGHT] Evento recebido: " + event);

        if ("DELETED".equals(event.eventType())) {
            if (readRepository.existsById(event.flightNumber())) {
                readRepository.deleteById(event.flightNumber());
            }
        } else {
            FlightReadModel view = new FlightReadModel(
                    event.flightNumber(),
                    event.aircraftId(),
                    event.originAirportId(),
                    event.destinationAirportId(),
                    event.departureTime(),
                    event.arrivalTime(),
                    event.status()
            );
            readRepository.save(view);
        }
    }
}