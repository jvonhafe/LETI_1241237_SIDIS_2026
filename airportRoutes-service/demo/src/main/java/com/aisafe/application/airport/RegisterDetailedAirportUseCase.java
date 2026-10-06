package com.aisafe.application.airport;

import com.aisafe.model.Airport;
import com.aisafe.model.Facility;
import com.aisafe.model.IataCode;
import com.aisafe.model.MediaImage;
import com.aisafe.repository.AirportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegisterDetailedAirportUseCase {

    @Autowired
    private AirportRepository airportRepository;

    public Airport execute(DetailedAirportDto dto) {
        IataCode iataCode = new IataCode(dto.iataCode.toUpperCase());
        if (airportRepository.existsById(iataCode)) {
            // CORRIGIDO: Substituído por IllegalStateException
            throw new IllegalStateException("O Aeroporto com IATA " + dto.iataCode + " já existe!");
        }

        Airport airport = new Airport(
                iataCode,
                dto.name,
                dto.city,
                dto.country,
                dto.timezone,
                "OPERATIONAL");//status iniciais

        // 3. Adicionar as Instalações
        if (dto.facilities != null) {
            for (DetailedAirportDto.FacilityDto fDto : dto.facilities) {
                Facility facility = new Facility();
                facility.setType(fDto.type);
                facility.setCapacity(fDto.capacity);
                facility.setDescription(fDto.description);

                airport.addFacility(facility);
            }
        }

        // 4. Adicionar as Fotos
        if (dto.images != null) {
            for (DetailedAirportDto.ImageDto iDto : dto.images) {
                MediaImage image = new MediaImage();
                image.setImageUrl(iDto.imageUrl);
                image.setDescription(iDto.description);
                airport.addImage(image);
            }
        }

        // 5. Guardar tudo na BD
        return airportRepository.save(airport);
    }

    public static class DetailedAirportDto {
        public String iataCode;
        public String name;
        public String city;
        public String country;
        public String timezone;
        public List<FacilityDto> facilities;
        public List<ImageDto> images;

        public static class FacilityDto {
            public String type;
            public int capacity;
            public String description;
        }

        public static class ImageDto {
            public String imageUrl;
            public String description;
        }
    }
}