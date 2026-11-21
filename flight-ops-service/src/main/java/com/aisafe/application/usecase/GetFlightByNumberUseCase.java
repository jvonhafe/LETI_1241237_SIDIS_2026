package com.aisafe.application.usecase;

import com.aisafe.model.ScheduledFlight;
import com.aisafe.repository.ScheduledFlightRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class GetFlightByNumberUseCase {
    private final ScheduledFlightRepository flightRepository;
    private final RestTemplate restTemplate;

    @Value("${flight.peers:}")
    private String peersCsv;

    public GetFlightByNumberUseCase(ScheduledFlightRepository flightRepository, RestTemplate restTemplate) {
        this.flightRepository = flightRepository;
        this.restTemplate = restTemplate;
    }

    // Método original para não partir os testes unitários existentes
    public ScheduledFlight execute(String flightNumber) {
        return execute(flightNumber, null, false);
    }

    // Novo método com suporte a P2P Query Propagation
    public ScheduledFlight execute(String flightNumber, String authHeader, boolean isForwarded) {
        // 1. Procura na base de dados local
        Optional<ScheduledFlight> local = flightRepository.findById(flightNumber);

        if (local.isPresent()) {
            return local.get();
        }

        // Se já foi reencaminhado por outro nó, evitamos ciclos infinitos na rede
        if (isForwarded) {
            throw new RuntimeException("Voo não encontrado em nenhuma instância: " + flightNumber);
        }

        // 2. Mecanismo P2P: Perguntar aos peers (outras portas configuradas)
        if (peersCsv != null && !peersCsv.isBlank()) {
            for (String peer : peersCsv.split(",")) {
                if (peer.isBlank()) continue;
                try {
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("X-Forwarded", "true"); // Marca como reencaminhado
                    if (authHeader != null) {
                        headers.set(HttpHeaders.AUTHORIZATION, authHeader); // Propaga o JWT
                    }

                    ResponseEntity<ScheduledFlight> response = restTemplate.exchange(
                            peer.trim() + "/api/flights/" + flightNumber,
                            HttpMethod.GET,
                            new HttpEntity<>(headers),
                            ScheduledFlight.class
                    );

                    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                        return response.getBody();
                    }
                } catch (Exception ex) {
                    System.out.println("-> Peer " + peer + " não respondeu ou não tem o voo " + flightNumber);
                }
            }
        }

        throw new RuntimeException("Voo não encontrado com o número: " + flightNumber);
    }
}