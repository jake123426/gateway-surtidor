package com.microservcio.gatewayreactivo.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@Profile("cors")
public class CorsConfig {

    @Bean
    CorsWebFilter corsWebFilter() {
        var corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of("*")); // Acepta todos los origines
        corsConfiguration.setAllowedMethods(List.of("*")); // Acepta todos los métodos
        corsConfiguration.setAllowedHeaders(List.of("*")); // Acepta todos los headers
        corsConfiguration.setExposedHeaders(List.of("*")); // Acepta todos los headers expuestos
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsWebFilter(source);
    }

}
