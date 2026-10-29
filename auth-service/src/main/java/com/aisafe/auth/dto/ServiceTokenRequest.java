package com.aisafe.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record ServiceTokenRequest(@NotBlank String serviceId, @NotBlank String secret) {}
