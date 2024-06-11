package com.microservcio.gatewayreactivo.dto;

import java.util.List;

public record SignUpResponseDto(
        String id,
        String name,
        String password,
        String email,
        List<String> roles,
        List<String> permisos

) {
}
