package com.aisafe.auth;

import com.aisafe.auth.model.Role;
import com.aisafe.auth.security.JwtProperties;
import com.aisafe.auth.security.JwtTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenServiceTest {

    private static final String SECRET = "unit-test-secret-with-at-least-32-bytes!!";

    private JwtTokenService service(String secret) {
        return new JwtTokenService(new JwtProperties(secret, "test-issuer", 60, 5));
    }

    @Test
    void userTokenRoundTripKeepsSubjectRoleAndType() {
        JwtTokenService svc = service(SECRET);
        Jwt jwt = svc.decode(svc.issueUserToken("atcc_user", Role.ATCC).value());

        assertEquals("atcc_user", jwt.getSubject());
        assertEquals("ATCC", jwt.getClaimAsString(JwtTokenService.CLAIM_ROLE));
        assertEquals("user", jwt.getClaimAsString(JwtTokenService.CLAIM_TYPE));
        assertEquals("test-issuer", jwt.getClaimAsString("iss"));
        assertTrue(jwt.getExpiresAt().isAfter(jwt.getIssuedAt()));
    }

    @Test
    void serviceTokenHasServiceRoleAndShortTtl() {
        JwtTokenService svc = service(SECRET);
        JwtTokenService.IssuedToken t = svc.issueServiceToken("aircraft-service");
        Jwt jwt = svc.decode(t.value());

        assertEquals("SERVICE", jwt.getClaimAsString(JwtTokenService.CLAIM_ROLE));
        assertEquals("service", jwt.getClaimAsString(JwtTokenService.CLAIM_TYPE));
        assertEquals(5 * 60, t.expiresInSeconds());
    }

    @Test
    void tokenSignedWithAnotherSecretIsRejected() {
        String token = service(SECRET).issueUserToken("admin", Role.ADMIN).value();
        JwtTokenService other = service("another-secret-with-at-least-32-bytes!!!!");
        assertThrows(JwtException.class, () -> other.decode(token));
    }

    @Test
    void tamperedTokenIsRejected() {
        JwtTokenService svc = service(SECRET);
        String token = svc.issueUserToken("tech", Role.MAINTENANCE_TECH).value();
        String[] parts = token.split("\\.");
        // troca o payload (muda a role) mantendo a assinatura antiga
        String forgedPayload = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(
                "{\"sub\":\"tech\",\"role\":\"ADMIN\"}".getBytes());
        String forged = parts[0] + "." + forgedPayload + "." + parts[2];
        assertThrows(JwtException.class, () -> svc.decode(forged));
    }

    @Test
    void shortSecretIsRejectedAtStartup() {
        assertThrows(IllegalStateException.class, () -> new JwtProperties("short", "iss", 1, 1));
    }
}
