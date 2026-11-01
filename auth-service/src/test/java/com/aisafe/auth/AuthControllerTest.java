package com.aisafe.auth;

import com.aisafe.auth.audit.AuditLogger;
import com.aisafe.auth.controller.AuthController;
import com.aisafe.auth.dto.*;
import com.aisafe.auth.model.Role;
import com.aisafe.auth.model.User;
import com.aisafe.auth.repository.UserRepository;
import com.aisafe.auth.security.JwtProperties;
import com.aisafe.auth.security.JwtTokenService;
import com.aisafe.auth.security.ServiceAccountRegistry;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private UserRepository users;
    private PasswordEncoder encoder;
    private AuthController controller;
    private HttpServletRequest http;

    @BeforeEach
    void setUp() {
        users = mock(UserRepository.class);
        encoder = new BCryptPasswordEncoder(4); // custo baixo so para os testes
        JwtTokenService jwt = new JwtTokenService(
                new JwtProperties("unit-test-secret-with-at-least-32-bytes!!", "iss", 60, 5));
        ServiceAccountRegistry registry = new ServiceAccountRegistry();
        registry.setServiceAccounts(Map.of("aircraft-service", "s3cret"));
        controller = new AuthController(users, encoder, jwt, registry, mock(AuditLogger.class));
        http = mock(HttpServletRequest.class);
        when(http.getRemoteAddr()).thenReturn("127.0.0.1");
    }

    @Test
    void loginWithCorrectCredentialsReturnsToken() {
        when(users.findByUsername("admin")).thenReturn(
                Optional.of(new User("admin", encoder.encode("Admin#2026"), Role.ADMIN)));

        ResponseEntity<?> res = controller.login(new LoginRequest("admin", "Admin#2026"), http);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        TokenResponse body = (TokenResponse) res.getBody();
        assertNotNull(body);
        assertEquals("Bearer", body.tokenType());
        assertFalse(body.token().isBlank());
    }

    @Test
    void loginWithWrongPasswordIsUnauthorized() {
        when(users.findByUsername("admin")).thenReturn(
                Optional.of(new User("admin", encoder.encode("Admin#2026"), Role.ADMIN)));

        assertEquals(HttpStatus.UNAUTHORIZED, controller.login(new LoginRequest("admin", "wrong"), http).getStatusCode());
    }

    @Test
    void loginWithUnknownUserGivesSameResponseAsWrongPassword() {
        when(users.findByUsername("ghost")).thenReturn(Optional.empty());

        ResponseEntity<?> res = controller.login(new LoginRequest("ghost", "whatever"), http);

        assertEquals(HttpStatus.UNAUTHORIZED, res.getStatusCode());
        assertEquals(Map.of("error", "Invalid credentials"), res.getBody());
    }

    @Test
    void disabledUserCannotLogin() {
        User u = new User("tech", encoder.encode("pw"), Role.MAINTENANCE_TECH);
        u.setEnabled(false);
        when(users.findByUsername("tech")).thenReturn(Optional.of(u));

        assertEquals(HttpStatus.UNAUTHORIZED, controller.login(new LoginRequest("tech", "pw"), http).getStatusCode());
    }

    @Test
    void serviceTokenIssuedOnlyForValidServiceCredentials() {
        assertEquals(HttpStatus.OK,
                controller.serviceToken(new ServiceTokenRequest("aircraft-service", "s3cret"), http).getStatusCode());
        assertEquals(HttpStatus.UNAUTHORIZED,
                controller.serviceToken(new ServiceTokenRequest("aircraft-service", "nope"), http).getStatusCode());
        assertEquals(HttpStatus.UNAUTHORIZED,
                controller.serviceToken(new ServiceTokenRequest("unknown-service", "s3cret"), http).getStatusCode());
    }
}
