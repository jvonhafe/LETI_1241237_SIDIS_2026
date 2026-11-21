package com.aisafe.controller;

import com.aisafe.application.dto.RegisterAirportUseCase;
import com.aisafe.application.dto.RegisterDetailedAirportUseCase;
import com.aisafe.application.dto.UpdateAirportDetailsUseCase;
import com.aisafe.application.dto.UpdateAirportStatusUseCase;
import com.aisafe.messaging.HelloProducer;
import com.aisafe.model.Airport;
import com.aisafe.model.BusiestAirportDto;
import com.aisafe.model.IataCode;
import com.aisafe.readmodel.AirportReadModel;
import com.aisafe.readmodel.AirportReadRepository;
import com.aisafe.repository.AirportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/airports")
@PreAuthorize("hasAnyRole('ADMIN', 'ATCC', 'BACKOFFICE', 'SERVICE')")
public class AirportController {

    @Autowired
    private HelloProducer helloProducer;

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

    @Autowired
    private AirportReadRepository airportReadRepository;

    // US106: Registar (Command)
    @PreAuthorize("hasAnyRole('ADMIN', 'BACKOFFICE')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Airport createAirport(@RequestBody Airport airport) {
        Airport savedAirport = registerAirportUseCase.execute(airport);
        helloProducer.sendMessage("Novo aeroporto criado (Simples): " + savedAirport.getIataCode().getCode());
        return savedAirport;
    }

    // US207: Registar Aeroporto Detalhado (Command)
    @PreAuthorize("hasAnyRole('ADMIN', 'BACKOFFICE')")
    @PostMapping("/detailed")
    @ResponseStatus(HttpStatus.CREATED)
    public Airport createDetailedAirport(@RequestBody RegisterDetailedAirportUseCase.DetailedAirportDto request) {
        Airport savedAirport = registerDetailedAirportUseCase.execute(request);
        helloProducer.sendMessage("Novo aeroporto criado (Detalhado): " + savedAirport.getIataCode().getCode());
        return savedAirport;
    }

    // US107: Obter por ID (Query CQRS - lê do Read Model replicado via RabbitMQ, sem HTTP P2P)
    @GetMapping("/{iata}")
    public EntityModel<AirportReadModel> getAirportById(@PathVariable String iata) {
        IataCode searchId = new IataCode(iata);
        AirportReadModel airport = airportReadRepository.findById(searchId.getCode())
                .orElseThrow(() -> new IllegalArgumentException("Aeroporto não encontrado no Read Model: " + iata));

        return EntityModel.of(airport,
                linkTo(methodOn(AirportController.class).getAirportById(iata)).withSelfRel(),
                linkTo(methodOn(AirportController.class).getAllAirports()).withRel("todos-aeroportos"));
    }

    // US107: Listar todos (Query CQRS)
    @GetMapping
    public List<AirportReadModel> getAllAirports() {
        return airportReadRepository.findAll();
    }

    // US108: Pesquisar por cidade (Query CQRS)
    @GetMapping("/search")
    public List<AirportReadModel> searchAirports(@RequestParam String city) {
        return airportReadRepository.findByCityContainingIgnoreCase(city);
    }

    // US109: Atualizar Estado (Command)
    @PreAuthorize("hasAnyRole('ADMIN', 'BACKOFFICE')")
    @PatchMapping("/{iata}/status")
    public Airport updateAirportStatus(@PathVariable String iata, @RequestParam String status) {
        return updateAirportStatusUseCase.execute(iata, status);
    }

    // US208: Atualizar Detalhes do Aeroporto (Command)
    @PreAuthorize("hasAnyRole('ADMIN', 'BACKOFFICE')")
    @PatchMapping("/{iata}/details")
    public Airport updateAirportDetails(
            @PathVariable String iata,
            @RequestBody UpdateAirportDetailsUseCase.UpdateDetailsRequest request) {
        return updateAirportDetailsUseCase.execute(iata, request);
    }

    // US210: Estatísticas dos Aeroportos mais movimentados
    @PreAuthorize("hasAnyRole('ADMIN', 'BACKOFFICE')")
    @GetMapping("/statistics/busiest")
    public List<BusiestAirportDto> getBusiestAirports(
            @RequestParam(defaultValue = "5") int limit) {
        return airportRepository.findBusiestAirports(PageRequest.of(0, limit));
    }

    // US211: Ver aeroportos agrupados por país ou região (Query CQRS)
    @GetMapping("/grouped")
    public Map<String, List<AirportReadModel>> getAirportsGrouped(@RequestParam(defaultValue = "country") String by) {
        List<AirportReadModel> allAirports = airportReadRepository.findAll();

        if ("country".equalsIgnoreCase(by)) {
            return allAirports.stream().collect(Collectors.groupingBy(AirportReadModel::getCountry));
        } else if ("timezone".equalsIgnoreCase(by)) {
            return allAirports.stream().collect(Collectors.groupingBy(AirportReadModel::getTimezone));
        } else {
            throw new IllegalArgumentException("Parâmetro de agrupamento não suportado. Use 'country' ou 'timezone'.");
        }
    }
}