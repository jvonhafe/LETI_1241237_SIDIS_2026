package com.aisafe.auth.controller;

import com.aisafe.auth.audit.AuditLogger;
import com.aisafe.auth.dto.*;
import com.aisafe.auth.model.User;
import com.aisafe.auth.repository.UserRepository;
import com.aisafe.auth.security.JwtTokenService;
import com.aisafe.auth.security.ServiceAccountRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Login de utilizadores e tokens de servico (JWT)")
public class AuthController {

    // Hash BCrypt real, calculado no arranque, para gastar o mesmo tempo quando o username nao existe
    // (evita enumeracao de utilizadores por timing).
    private final String dummyHash;

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtTokenService jwt;
    private final ServiceAccountRegistry serviceAccounts;
    private final AuditLogger audit;

    public AuthController(UserRepository users, PasswordEncoder encoder, JwtTokenService jwt,
                          ServiceAccountRegistry serviceAccounts, AuditLogger audit) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
        this.serviceAccounts = serviceAccounts;
        this.audit = audit;
        this.dummyHash = encoder.encode("dummy-password-for-timing");
    }

    @Operation(summary = "Login de utilizador: devolve um JWT")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletRequest http) {
        String ip = http.getRemoteAddr();
        Optional<User> found = users.findByUsername(request.username());

        boolean passwordOk = encoder.matches(request.password(),
                found.map(User::getPasswordHash).orElse(dummyHash));

        if (found.isEmpty() || !passwordOk || !found.get().isEnabled()) {
            audit.log("LOGIN", request.username(), "DENIED", ip, "invalid credentials");
            // Mensagem unica: nao revela se o username existe.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials"));
        }

        User user = found.get();
        JwtTokenService.IssuedToken token = jwt.issueUserToken(user.getUsername(), user.getRole());
        audit.log("LOGIN", user.getUsername(), "SUCCESS", ip, "role=" + user.getRole());
        return ResponseEntity.ok(TokenResponse.bearer(token.value(), token.expiresInSeconds()));
    }

    @Operation(summary = "Token de servico (autenticacao entre servicos/peers)")
    @PostMapping("/service-token")
    public ResponseEntity<?> serviceToken(@Valid @RequestBody ServiceTokenRequest request, HttpServletRequest http) {
        String ip = http.getRemoteAddr();
        if (!serviceAccounts.matches(request.serviceId(), request.secret())) {
            audit.log("SERVICE_TOKEN", request.serviceId(), "DENIED", ip, "invalid service credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials"));
        }
        JwtTokenService.IssuedToken token = jwt.issueServiceToken(request.serviceId());
        audit.log("SERVICE_TOKEN", request.serviceId(), "SUCCESS", ip, "-");
        return ResponseEntity.ok(TokenResponse.bearer(token.value(), token.expiresInSeconds()));
    }
}
