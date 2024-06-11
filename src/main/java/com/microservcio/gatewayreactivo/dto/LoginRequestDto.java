package com.microservcio.gatewayreactivo.dto;

import jakarta.validation.constraints.NotBlank;


public record LoginRequestDto(@NotBlank String username,
                              @NotBlank String password) {
}
