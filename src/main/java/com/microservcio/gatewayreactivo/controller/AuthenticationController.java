package com.microservcio.gatewayreactivo.controller;


import com.microservcio.gatewayreactivo.dto.LoginRequestDto;
import com.microservcio.gatewayreactivo.dto.ResponseDto;
import com.microservcio.gatewayreactivo.dto.SignUpRequestDto;
import com.microservcio.gatewayreactivo.service.UserDetailServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
//@Validated
public class AuthenticationController {

    @Autowired
    private UserDetailServiceImpl userDetailServiceImpl;

    @PostMapping("/login")
    public Mono<ResponseEntity<ResponseDto>> login(@RequestBody @Valid LoginRequestDto userRequest){
        System.out.println("Entre al login");
        return userDetailServiceImpl.loginUser(userRequest)
                .flatMap( responseDto -> Mono.just(new ResponseEntity<>(responseDto, HttpStatus.OK)))
                .onErrorResume( error -> {
                    ResponseDto responseDto = ResponseDto.builder().error(error.getMessage()).build();
                    if (error instanceof BadCredentialsException) {
                        return Mono.just(new ResponseEntity<>(responseDto, HttpStatus.UNAUTHORIZED));
                    }
                    return Mono.just(new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR));
                });
    };
    @PostMapping("/signup")
    public Mono<ResponseEntity<ResponseDto>> signup(@RequestBody @Valid SignUpRequestDto signUpRequestDto){
        return userDetailServiceImpl.signupUser(signUpRequestDto)
                .flatMap( responseDto -> Mono.just(new ResponseEntity<>(responseDto, HttpStatus.OK)))
                .onErrorResume( error -> {
                    ResponseDto responseDto = ResponseDto.builder().error(error.getMessage()).build();
                    return Mono.just(new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST));
                });
    }

}
