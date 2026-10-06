package com.aisafe.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "airports")
public class Airport {

    @EmbeddedId
    private IataCode iataCode; //value object

    private String name;
    private String city;
    private String country;
    private String timezone;
    private String status;

    // --- NOVO: US208 (Value Objects Embutidos para Contactos e Horários) ---
    @Embedded
    private ContactInfo contactInfo;

    @Embedded
    private OperatingHours operatingHours;

    // --- NOVO: US207 (Entidades Locais em Cascata para Fotos e Instalações) ---
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "airport_iata") // Cria a chave estrangeira automaticamente na tabela 'facility'
    private List<Facility> facilities = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "airport_iata") // Cria a chave estrangeira automaticamente na tabela 'media_image'
    private List<MediaImage> images = new ArrayList<>();

    protected Airport() {}

    // Construtor
    public Airport(IataCode iataCode, String name, String city, String country, String timezone, String status) {
        this.iataCode = iataCode;
        this.name = name;
        this.city = city;
        this.country = country;
        this.timezone = timezone;
        this.updateStatus(status);
    }

    public void updateStatus(String newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("O estado do aeroporto não pode ser nulo.");
        }
        String statusUpper = newStatus.toUpperCase();
        if (!statusUpper.matches("^(OPERATIONAL|CLOSED|UNDER_MAINTENANCE)$")) {
            throw new IllegalArgumentException("Estado inválido. Escolha: OPERATIONAL, CLOSED, UNDER_MAINTENANCE");
        }
        this.status = statusUpper;
    }

    // --- NOVO: Métodos de Domínio (Para orquestrar a US207 e US208 com segurança) ---
    public void updateContactInfo(ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }

    public void updateOperatingHours(OperatingHours operatingHours) {
        this.operatingHours = operatingHours;
    }

    public void addFacility(Facility facility) {
        this.facilities.add(facility);
    }

    public void addImage(MediaImage image) {
        this.images.add(image);
    }

    // --- Getters Originais ---
    public IataCode getIataCode() { return iataCode; }
    public String getName() { return name; }
    public String getCity() { return city; }
    public String getCountry() { return country; }
    public String getTimezone() { return timezone; }
    public String getStatus() { return status; }

    // --- Novos Getters (Necessários para devolver o JSON completo) ---
    public ContactInfo getContactInfo() { return contactInfo; }
    public OperatingHours getOperatingHours() { return operatingHours; }
    public List<Facility> getFacilities() { return facilities; }
    public List<MediaImage> getImages() { return images; }

}