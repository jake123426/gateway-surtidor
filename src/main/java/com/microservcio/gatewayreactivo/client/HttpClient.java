package com.microservcio.gatewayreactivo.client;

import com.microservcio.gatewayreactivo.dto.LoginResponseDto;
import com.microservcio.gatewayreactivo.dto.SignUpRequestDto;
import com.microservcio.gatewayreactivo.dto.SignUpResponseDto;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

public class HttpClient {

    private final WebClient webClient;

    public HttpClient() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8090/")
                .build();
    }

    public Mono<LoginResponseDto> getUserByEmail(String username) {
        return webClient.get()
                .uri("/api/user/" + username)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        clientResponse -> Mono.error(new BadCredentialsException("Usuario o contraseña incorrectas")))
                .bodyToMono(LoginResponseDto.class);
    }

    public Mono<SignUpResponseDto> createUser(SignUpRequestDto signUpRequestDto) {
        return webClient.post()
                .uri("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(signUpRequestDto))
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        clientResponse -> Mono.error(new RuntimeException("Error al guardar el usuario")))
                .bodyToMono(SignUpResponseDto.class);
    }
}
