package com.microservcio.gatewayreactivo.config;


import com.microservcio.gatewayreactivo.config.filters.JwtAuthenticationFilter;
import com.microservcio.gatewayreactivo.other.JwtAuthenticationEntryPoint;
import com.microservcio.gatewayreactivo.service.UserDetailServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
//@EnableWebFluxSecurity
public class SecurityConfig {

//    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
//
//    public SecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
//        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
//    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance()) //* Will prevent it to create a WebSesion(STATELESS)
//                .exceptionHandling(e -> e.authenticationEntryPoint(jwtAuthenticationEntryPoint)) //* Handle Exceptions
                .authorizeExchange(exchanges  -> exchanges
                        .pathMatchers(HttpMethod.POST, "/auth/**").permitAll()
//                        .pathMatchers(HttpMethod.GET, GET_ROLE_NAME).hasRole("Role_Name")
                        .anyExchange().authenticated())
                .addFilterBefore( jwtAuthenticationFilter, SecurityWebFiltersOrder.HTTP_BASIC)
                .build();
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(UserDetailServiceImpl userDetailService, PasswordEncoder passwordEncoder) {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailService);
        authenticationManager.setPasswordEncoder(passwordEncoder);
        return authenticationManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
