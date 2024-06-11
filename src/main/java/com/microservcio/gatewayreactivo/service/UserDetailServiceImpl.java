package com.microservcio.gatewayreactivo.service;

import com.microservcio.gatewayreactivo.client.HttpClient;
import com.microservcio.gatewayreactivo.dto.LoginRequestDto;
import com.microservcio.gatewayreactivo.dto.LoginResponseDto;
import com.microservcio.gatewayreactivo.dto.ResponseDto;
import com.microservcio.gatewayreactivo.dto.SignUpRequestDto;
import com.microservcio.gatewayreactivo.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailServiceImpl implements ReactiveUserDetailsService {

    @Autowired
    private JwtUtils jwtUtils;

    //    private UserClient userClient;
    private final HttpClient httpClient = new HttpClient();

    @Autowired
    private PasswordEncoder passwordEncoder;

    private LoginResponseDto loginResponseDto;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return httpClient.getUserByEmail(username)
                .map(response -> {
                    this.loginResponseDto = response;
                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    response.getRoles().forEach( role -> authorities.add( new SimpleGrantedAuthority("ROLE_"+ role) ));
                    response.getPermisos().forEach( permiso -> authorities.add( new SimpleGrantedAuthority(permiso) ));
                    return User.withUsername(response.getEmail()).password(response.getPassword()).accountExpired(false)
                            .accountLocked(false).credentialsExpired(false).disabled(false).authorities(authorities).build();
                });
    }

    public Mono<ResponseDto> loginUser(LoginRequestDto authLoginRequest) {
        String username = authLoginRequest.username();
        String password = authLoginRequest.password();
        return this.authenticate(username, password)
                .map( authentication -> {
                    String token = jwtUtils.createToken( authentication );
                    return ResponseDto.builder().data(loginResponseDto).token( token ).build();
                });
    }

    public Mono<ResponseDto> signupUser(SignUpRequestDto signUpRequestDto){
        signUpRequestDto.setPassword(passwordEncoder.encode(signUpRequestDto.getPassword()));
        return httpClient.createUser(signUpRequestDto)
                .map( response -> {
                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    response.roles().forEach( role -> authorities.add( new SimpleGrantedAuthority("ROLE_"+ role) ));
                    response.permisos().forEach( permiso -> authorities.add( new SimpleGrantedAuthority(permiso) ));
                    UserDetails userDatails = User.withUsername(response.email()).password(response.password()).accountExpired(false).accountLocked(false)
                            .credentialsExpired(false).disabled(false).authorities(authorities).build();
                    Authentication authentication = new UsernamePasswordAuthenticationToken(userDatails, null, authorities);
                    String accesToken = jwtUtils.createToken(authentication);
                    return ResponseDto.builder().data(response).token(accesToken).build();
                });
    }

    public Mono<Authentication> authenticate(String username, String password) {
        return this.findByUsername(username)
                .flatMap( userDetails -> {
                    if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                        return Mono.error(new BadCredentialsException("Usuario o contraseña incorrectas"));
                    }
                    return Mono.just(new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities()));
                });

    }
    //*  Obetner la contraseña encriptada
//    public static void main(String[] args) {
//        System.out.println(new BCryptPasswordEncoder().encode("123"));
//    }

}
