package com.aisafe.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;

/**
 * Obtém (e guarda em cache) o token de serviço que identifica ESTE serviço perante os outros.
 * O token é pedido ao auth-service com o serviceId e o segredo deste serviço.
 */
@Component
public class ServiceTokenProvider {

    private final RestTemplate authClient = new RestTemplate(); // cliente próprio, sem interceptor
    private final String authUrl;
    private final String serviceId;
    private final String serviceSecret;

    private String cachedToken;
    private Instant expiresAt = Instant.EPOCH;

    public ServiceTokenProvider(@Value("${aisafe.auth.url:http://localhost:8084}") String authUrl,
                                @Value("${aisafe.service.id}") String serviceId,
                                @Value("${aisafe.service.secret}") String serviceSecret) {
        this.authUrl = authUrl;
        this.serviceId = serviceId;
        this.serviceSecret = serviceSecret;
    }

    @SuppressWarnings("unchecked")
    public synchronized String getToken() {
        // Renova 30 segundos antes de expirar
        if (cachedToken == null || Instant.now().isAfter(expiresAt.minusSeconds(30))) {
            Map<String, Object> body = Map.of("serviceId", serviceId, "secret", serviceSecret);
            Map<String, Object> response = authClient.postForObject(authUrl + "/api/auth/service-token", body, Map.class);
            if (response == null || response.get("token") == null) {
                throw new IllegalStateException("auth-service não devolveu um token de serviço");
            }
            cachedToken = (String) response.get("token");
            long ttlSeconds = ((Number) response.get("expiresInSeconds")).longValue();
            expiresAt = Instant.now().plusSeconds(ttlSeconds);
        }
        return cachedToken;
    }
}