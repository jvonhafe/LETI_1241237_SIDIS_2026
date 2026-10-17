package com.aisafe.controller;

import com.aisafe.application.airport.RegisterAirportUseCase;
import com.aisafe.application.airport.RegisterDetailedAirportUseCase;
import com.aisafe.application.airport.UpdateAirportDetailsUseCase;
import com.aisafe.application.airport.UpdateAirportStatusUseCase;
import com.aisafe.model.Airport;
import com.aisafe.model.BusiestAirportDto;
import com.aisafe.model.IataCode;
import com.aisafe.repository.AirportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/airports")
public class AirportController {

    @Autowired
    private RegisterAirportUseCase registerAirportUseCase;

    @Autowired
    private UpdateAirportStatusUseCase updateAirportStatusUseCase;

    @Autowired
    private RegisterDetailedAirportUseCase registerDetailedAirportUseCase;

    @Autowired
    private UpdateAirportDetailsUseCase updateAirportDetailsUseCase;

    @Autowired
    private AirportRepository airportRepository;

    // --- NOVIDADES PARA O FORWARDING ---
    @Autowired
    private RestTemplate restTemplate;

    @Value("${server.port}")
    private String serverPort;

    // Lista estática de vizinhos (peers)
    private final List<String> peers = List.of("8081", "8082");

    // US106: Registar (Simples)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Airport createAirport(@RequestBody Airport airport) {
        return registerAirportUseCase.execute(airport);
    }

    // US207: Registar Aeroporto Detalhado (Com Fotos e Instalações)
    @PostMapping("/detailed")
    @ResponseStatus(HttpStatus.CREATED)
    public Airport createDetailedAirport(@RequestBody RegisterDetailedAirportUseCase.DetailedAirportDto request) {
        return registerDetailedAirportUseCase.execute(request);
    }

    // US107: Obter por ID (COM FORWARDING)
    @GetMapping("/{iata}")
    public EntityModel<Airport> getAirportById(
            @PathVariable String iata,
            @RequestHeader(value = "X-Forwarded", required = false, defaultValue = "false") boolean isForwarded) {

        IataCode searchId = new IataCode(iata);

        try {
            // 1. Tenta procurar na base de dados LOCAL da instância
            Airport airport = airportRepository.findById(searchId)
                    .orElseThrow(() -> new IllegalArgumentException("Aeroporto não encontrado localmente"));

            return EntityModel.of(airport,
                    linkTo(methodOn(AirportController.class).getAirportById(airport.getIataCode().getCode(), isForwarded)).withSelfRel(),
                    linkTo(methodOn(AirportController.class).getAllAirports()).withRel("todos-aeroportos")
            );

        } catch (IllegalArgumentException e) {
            // 2. Prevenção de loop infinito: Se não encontrou e o pedido já era um reencaminhamento, para aqui.
            if (isForwarded) {
                throw new IllegalArgumentException("Aeroporto não encontrado em nenhuma instância: " + iata);
            }

            // 3. Forwarding: Se não encontrou localmente, vai perguntar às outras instâncias
            for (String peerPort : peers) {
                if (!peerPort.equals(this.serverPort)) { // Garante que não pergunta a si próprio
                    try {
                        String peerUrl = "http://localhost:" + peerPort + "/api/airports/" + iata;

                        // Adiciona o cabeçalho X-Forwarded=true para avisar a outra instância que é um reencaminhamento
                        HttpHeaders headers = new HttpHeaders();
                        headers.add("X-Forwarded", "true");
                        HttpEntity<String> entity = new HttpEntity<>(headers);

                        ResponseEntity<Airport> response = restTemplate.exchange(
                                peerUrl,
                                HttpMethod.GET,
                                entity,
                                Airport.class
                        );

                        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                            System.out.println("-> Sucesso: Aeroporto " + iata + " encontrado na instância " + peerPort);
                            return EntityModel.of(response.getBody(),
                                    linkTo(methodOn(AirportController.class).getAirportById(response.getBody().getIataCode().getCode(), false)).withSelfRel());
                        }
                    } catch (Exception ex) {
                        System.out.println("-> Vizinho na porta " + peerPort + " também não tem o aeroporto " + iata);
                    }
                }
            }

            // 4. Se chegou aqui, nenhum vizinho tinha o aeroporto
            throw new IllegalArgumentException("Aeroporto não encontrado em nenhuma instância do sistema: " + iata);
        }
    }

    // US107: Listar todos
    @GetMapping
    public List<Airport> getAllAirports() {
        return airportRepository.findAll();
    }

    // US108: Pesquisar por cidade
    @GetMapping("/search")
    public List<Airport> searchAirports(@RequestParam String city) {
        return airportRepository.findByCityContainingIgnoreCase(city);
    }

    // US109: Atualizar Estado
    @PatchMapping("/{iata}/status")
    public Airport updateAirportStatus(@PathVariable String iata, @RequestParam String status) {
        return updateAirportStatusUseCase.execute(iata, status);
    }

    // US208: Atualizar Detalhes do Aeroporto (Contactos e Horários)
    @PatchMapping("/{iata}/details")
    public Airport updateAirportDetails(
            @PathVariable String iata,
            @RequestBody UpdateAirportDetailsUseCase.UpdateDetailsRequest request) {

        return updateAirportDetailsUseCase.execute(iata, request);
    }

    // --- NOVO: US210: Estatísticas dos Aeroportos mais movimentados ---
    @GetMapping("/statistics/busiest")
    public List<BusiestAirportDto> getBusiestAirports(
            @RequestParam(defaultValue = "5") int limit) {
        return airportRepository.findBusiestAirports(PageRequest.of(0, limit));
    }

    // --- NOVO: US211: Ver aeroportos agrupados por país ou região ---
    @GetMapping("/grouped")
    public Map<String, List<Airport>> getAirportsGrouped(@RequestParam(defaultValue = "country") String by) {

        List<Airport> allAirports = airportRepository.findAll();

        if ("country".equalsIgnoreCase(by)) {
            return allAirports.stream().collect(Collectors.groupingBy(Airport::getCountry));
        } else if ("timezone".equalsIgnoreCase(by)) {
            return allAirports.stream().collect(Collectors.groupingBy(Airport::getTimezone));
        } else {
            throw new IllegalArgumentException("Parâmetro de agrupamento não suportado. Use 'country' ou 'timezone'.");
        }
    }
}