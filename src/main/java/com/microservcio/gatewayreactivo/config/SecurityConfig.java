package com.microservcio.gatewayreactivo.config;


import com.microservcio.gatewayreactivo.config.filters.JwtAuthenticationFilter;
import com.microservcio.gatewayreactivo.other.JwtAuthenticationEntryPoint;
import com.microservcio.gatewayreactivo.service.UserDetailServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
//@EnableWebFluxSecurity
public class SecurityConfig {

//    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
//
//    public SecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
//        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
//    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
//        corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:8080", "http://127.0.0.1:8080"));
        corsConfiguration.setAllowedOrigins(List.of("*"));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS")); // Acepta todos los métodos
        corsConfiguration.setAllowedHeaders(List.of("*")); // Acepta todos los headers
//        corsConfiguration.setExposedHeaders(List.of("*")); // Acepta todos los headers expuestos
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors( corsSpec -> corsSpec.configurationSource(corsConfigurationSource()) )
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance()) //* Will prevent it to create a WebSesion(STATELESS)
//                .exceptionHandling(e -> e.authenticationEntryPoint(jwtAuthenticationEntryPoint)) //* Handle Exceptions
                .authorizeExchange(exchanges  -> exchanges
                        .pathMatchers(HttpMethod.POST, "/auth/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/graphiql").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/user/all").hasAnyRole("ADMIN", "SELLER")
                        .pathMatchers(HttpMethod.POST, "/api/vehiculo").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.GET, "/api/vehiculo/all").hasAnyRole("ADMIN", "SELLER")
                        .pathMatchers(HttpMethod.POST, "/api/combustible").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.GET, "/api/combustible/all").hasAnyRole("ADMIN", "SELLER")
                        .pathMatchers(HttpMethod.POST, "/api/tanque").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.GET, "/api/tanque/all").hasAnyRole("ADMIN", "SELLER")
                        .pathMatchers(HttpMethod.POST, "/api/bomba").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.GET, "/api/bomba/all").hasAnyRole("ADMIN", "SELLER")
//                        .pathMatchers(HttpMethod.GET, "/api/user/all").hasAnyRole("ADMIN", "SUPERADMIN")
//                        .pathMatchers(HttpMethod.GET, "/api/user/all").hasAuthority("CREATE")
//                        .pathMatchers(HttpMethod.GET, "/api/user/all").hasAnyAuthority("CREATE", "DELETE")
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
