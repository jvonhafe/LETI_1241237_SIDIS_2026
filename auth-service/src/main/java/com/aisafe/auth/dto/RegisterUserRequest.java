package com.aisafe.auth.dto;

import com.aisafe.auth.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @NotBlank @Size(min = 3, max = 50) String username,
        @NotBlank @Size(min = 10, max = 100) String password,
        @NotNull Role role) {}
