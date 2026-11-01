package com.aisafe.auth.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aisafe.jwt")
public record JwtProperties(String secret, String issuer, long userTtlMinutes, long serviceTtlMinutes) {

    public JwtProperties {
        if (secret == null || secret.getBytes().length < 32) {
            throw new IllegalStateException(
                    "aisafe.jwt.secret tem de ter pelo menos 32 bytes (defina AISAFE_JWT_SECRET)");
        }
    }
}
