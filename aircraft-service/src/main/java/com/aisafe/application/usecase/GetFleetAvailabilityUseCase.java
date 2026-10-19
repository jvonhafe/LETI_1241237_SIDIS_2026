package com.aisafe.application.usecase;

import com.aisafe.model.Aircraft;
import com.aisafe.application.dto.FleetAvailabilityDTO;
import com.aisafe.repository.AircraftRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@Service
public class GetFleetAvailabilityUseCase {

    private final AircraftRepository aircraftRepository;
    private final RestTemplate restTemplate;

    // Vai buscar as URLs das outras réplicas ao application.properties
    @Value("#{'${aircraft.peers.urls:}'.split(',')}")
    private List<String> peerUrls;

    public GetFleetAvailabilityUseCase(AircraftRepository aircraftRepository, RestTemplate restTemplate) {
        this.aircraftRepository = aircraftRepository;
        this.restTemplate = restTemplate;
    }

    public FleetAvailabilityDTO execute(boolean localOnly, String authHeader) {
        List<Aircraft> localAircraft = aircraftRepository.findAll();

        List<FleetAvailabilityDTO.AircraftStatusDTO> allDetails = localAircraft.stream()
                .map(a -> new FleetAvailabilityDTO.AircraftStatusDTO(
                        a.getRegistrationNumber() != null ? a.getRegistrationNumber().getRegistration() : "UNKNOWN",
                        a.getModelId(),
                        a.getStatus() != null ? a.getStatus().toString() : "UNKNOWN"
                ))
                .collect(Collectors.toList());

        if (!localOnly && peerUrls != null) {
            HttpHeaders headers = new HttpHeaders();
            if (authHeader != null) {
                headers.set("Authorization", authHeader);
            }
            HttpEntity<String> entity = new HttpEntity<>(headers);

            for (String peerUrl : peerUrls) {
                if (peerUrl == null || peerUrl.trim().isEmpty()) continue;

                try {
                    String url = peerUrl + "/api/aircraft/availability?localOnly=true"; // ou o caminho base do teu model controller

                    ResponseEntity<FleetAvailabilityDTO> response = restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            entity,
                            FleetAvailabilityDTO.class
                    );

                    FleetAvailabilityDTO peerData = response.getBody();

                    if (peerData != null && peerData.getAircraftList() != null) {
                        allDetails.addAll(peerData.getAircraftList());
                    }
                } catch (Exception e) {
                    System.err.println("Aviso: Falha na comunicação P2P com a réplica " + peerUrl);
                }
            }
        }

        long total = allDetails.size();
        long available = allDetails.stream()
                .filter(d -> d.getStatus() != null &&
                        ("AVAILABLE".equalsIgnoreCase(d.getStatus()) || "OPERATIONAL".equalsIgnoreCase(d.getStatus())))
                .count();

        long maintenance = allDetails.stream()
                .filter(d -> d.getStatus() != null && "MAINTENANCE".equalsIgnoreCase(d.getStatus()))
                .count();

        double rate = total > 0 ? ((double) available / total) * 100 : 100.0;

        return new FleetAvailabilityDTO(total, available, maintenance, rate, allDetails);
    }
}