package com.aisafe.auth.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/** Contas de servico (aircraft-service, airport-routes-service, flight-ops-service) lidas de aisafe.service-accounts.*. */
@Component
@ConfigurationProperties(prefix = "aisafe")
public class ServiceAccountRegistry {

    private Map<String, String> serviceAccounts = new HashMap<>();

    public Map<String, String> getServiceAccounts() { return serviceAccounts; }
    public void setServiceAccounts(Map<String, String> serviceAccounts) { this.serviceAccounts = serviceAccounts; }

    /** Comparacao em tempo constante. */
    public boolean matches(String serviceId, String secret) {
        String expected = serviceAccounts.get(serviceId);
        if (expected == null || secret == null) return false;
        return MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), secret.getBytes(StandardCharsets.UTF_8));
    }
}
