package com.aisafe.auth.controller;

import com.aisafe.auth.audit.AuditLogger;
import com.aisafe.auth.dto.RegisterUserRequest;
import com.aisafe.auth.model.Role;
import com.aisafe.auth.model.User;
import com.aisafe.auth.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/users")
@Tag(name = "User administration", description = "Gestao de utilizadores (apenas ADMIN)")
@SecurityRequirement(name = "bearerAuth")
public class UserAdminController {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final AuditLogger audit;

    public UserAdminController(UserRepository users, PasswordEncoder encoder, AuditLogger audit) {
        this.users = users;
        this.encoder = encoder;
        this.audit = audit;
    }

    @Operation(summary = "Criar utilizador")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> register(@Valid @RequestBody RegisterUserRequest req,
                                      Authentication auth, HttpServletRequest http) {
        if (req.role() == Role.SERVICE) {
            return ResponseEntity.badRequest().body(Map.of("error", "SERVICE role is reserved for service accounts"));
        }
        if (users.existsByUsername(req.username())) {
            audit.log("USER_CREATE", auth.getName(), "DENIED", http.getRemoteAddr(), "duplicate username");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Username already exists"));
        }
        users.save(new User(req.username(), encoder.encode(req.password()), req.role()));
        audit.log("USER_CREATE", auth.getName(), "SUCCESS", http.getRemoteAddr(),
                "created=" + req.username() + " role=" + req.role());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("username", req.username(), "role", req.role().name()));
    }
}
