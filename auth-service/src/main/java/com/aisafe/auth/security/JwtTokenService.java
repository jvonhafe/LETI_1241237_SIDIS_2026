package com.aisafe.auth.security;

import com.aisafe.auth.model.Role;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

/**
 * Emite JWT standard (HS256) com os claims: iss, sub, iat, exp, role, type.
 * Os outros servicos validam o token com o mesmo segredo (ver docs/SECURITY.md).
 */
@Service
public class JwtTokenService {

    public static final String CLAIM_ROLE = "role";
    public static final String CLAIM_TYPE = "type"; // "user" | "service"

    private final JwtProperties props;
    private final JwtEncoder encoder;
    private final JwtDecoder decoder;

    public JwtTokenService(JwtProperties props) {
        this.props = props;
        SecretKey key = new SecretKeySpec(props.secret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        this.encoder = new NimbusJwtEncoder(new ImmutableSecret<>(key));
        this.decoder = NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
    }

    public IssuedToken issueUserToken(String username, Role role) {
        return issue(username, role, "user", Duration.ofMinutes(props.userTtlMinutes()));
    }

    public IssuedToken issueServiceToken(String serviceId) {
        return issue(serviceId, Role.SERVICE, "service", Duration.ofMinutes(props.serviceTtlMinutes()));
    }

    public Jwt decode(String token) {
        return decoder.decode(token);
    }

    public JwtDecoder decoder() {
        return decoder;
    }

    private IssuedToken issue(String subject, Role role, String type, Duration ttl) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(props.issuer())
                .subject(subject)
                .issuedAt(now)
                .expiresAt(now.plus(ttl))
                .claim(CLAIM_ROLE, role.name())
                .claim(CLAIM_TYPE, type)
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String value = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new IssuedToken(value, ttl.toSeconds());
    }

    public record IssuedToken(String value, long expiresInSeconds) {}
}
