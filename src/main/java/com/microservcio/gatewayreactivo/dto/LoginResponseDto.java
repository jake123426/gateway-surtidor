package com.microservcio.gatewayreactivo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponseDto {
    private String id;
    private String name;
    private String password;
    private String email;
    private String bomba;
    private List<String> roles;
    private List<String> permisos;
}
