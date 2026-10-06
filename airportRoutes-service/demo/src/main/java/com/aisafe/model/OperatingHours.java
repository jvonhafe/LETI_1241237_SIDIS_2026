package com.aisafe.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class OperatingHours {
    private String openingTime;
    private String closingTime;
}