package com.aisafe.auth.events;

import com.aisafe.auth.model.Role;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/** Evento "utilizador criado". Transporta o hash BCrypt (nunca a password em claro). */
public record UserCreatedEvent(String origin, String username, String passwordHash, Role role) {

    private static final Base64.Encoder ENC = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder DEC = Base64.getUrlDecoder();

    /** origin.username.passwordHash.role, cada campo em Base64 (URL-safe, sem pontos). */
    public String toMessage() {
        return String.join(".", enc(origin), enc(username), enc(passwordHash), enc(role.name()));
    }

    public static UserCreatedEvent fromMessage(String message) {
        String[] p = message.split("\\.", -1);
        if (p.length != 4) {
            throw new IllegalArgumentException("Mensagem UserCreated invalida");
        }
        return new UserCreatedEvent(dec(p[0]), dec(p[1]), dec(p[2]), Role.valueOf(dec(p[3])));
    }

    private static String enc(String v) {
        return ENC.encodeToString(v.getBytes(StandardCharsets.UTF_8));
    }

    private static String dec(String v) {
        return new String(DEC.decode(v), StandardCharsets.UTF_8);
    }
}