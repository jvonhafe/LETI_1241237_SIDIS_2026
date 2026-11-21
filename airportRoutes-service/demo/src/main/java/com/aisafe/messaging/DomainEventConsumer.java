package com.aisafe.messaging;

import com.aisafe.model.Airport;
import com.aisafe.model.IataCode;
import com.aisafe.readmodel.AirportReadModel;
import com.aisafe.readmodel.AirportReadRepository;
import com.aisafe.readmodel.RouteReadModel;
import com.aisafe.readmodel.RouteReadRepository;
import com.aisafe.repository.AirportRepository;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RabbitListener(queues = "#{instanceSyncQueue.name}")
public class DomainEventConsumer {

    private final AirportReadRepository airportReadRepository;
    private final RouteReadRepository routeReadRepository;
    private final AirportRepository airportRepository;

    @Value("${server.port:8091}")
    private String serverPort;

    public DomainEventConsumer(AirportReadRepository airportReadRepository,
                               RouteReadRepository routeReadRepository,
                               AirportRepository airportRepository) {
        this.airportReadRepository = airportReadRepository;
        this.routeReadRepository = routeReadRepository;
        this.airportRepository = airportRepository;
    }

    @RabbitHandler
    @Transactional
    public void handleAirportEvent(AirportDomainEvent event) {
        AirportReadModel model = airportReadRepository.findById(event.iataCode())
                .orElse(new AirportReadModel());

        if (event.eventId().equals(model.getLastEventId())) {
            return;
        }

        // 1. Atualiza o Read Model (CQRS)
        model.setIataCode(event.iataCode());
        model.setName(event.name());
        model.setCity(event.city());
        model.setCountry(event.country());
        model.setTimezone(event.timezone());
        model.setStatus(event.status());
        model.setLastEventId(event.eventId());
        airportReadRepository.save(model);

        // 2. Mantém consistência eventual do Write Model entre instâncias isoladas
        if (!serverPort.equals(event.sourcePort())) {
            IataCode code = new IataCode(event.iataCode());
            Airport localWrite = airportRepository.findById(code)
                    .orElseGet(() -> new Airport(code, event.name(), event.city(), event.country(), event.timezone(), event.status()));
            localWrite.updateStatus(event.status());
            airportRepository.save(localWrite);
        }

        System.out.println(" [CQRS] Airport sincronizado na porta " + serverPort + ": " + event.iataCode() + " (" + event.eventType() + ")");
    }

    @RabbitHandler
    @Transactional
    public void handleRouteEvent(RouteDomainEvent event) {
        RouteReadModel model = routeReadRepository.findById(event.routeId())
                .orElse(new RouteReadModel());

        if (event.eventId().equals(model.getLastEventId())) {
            return;
        }

        model.setId(event.routeId());
        model.setOriginIata(event.originIata());
        model.setOriginName(event.originName());
        model.setDestinationIata(event.destinationIata());
        model.setDestinationName(event.destinationName());
        model.setEstimatedFlightTimeMinutes(event.estimatedFlightTimeMinutes());
        model.setMinimumRange(event.minimumRange());
        model.setMinimumCapacity(event.minimumCapacity());
        model.setDistanceKm(event.distanceKm());
        model.setStatus(event.status());
        model.setLastEventId(event.eventId());

        routeReadRepository.save(model);
        System.out.println(" [CQRS] RouteReadModel sincronizado na porta " + serverPort + ": ID " + event.routeId() + " (" + event.eventType() + ")");
    }
}