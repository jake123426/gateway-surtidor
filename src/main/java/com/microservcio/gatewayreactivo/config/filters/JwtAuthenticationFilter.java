package com.microservcio.gatewayreactivo.config.filters;


import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.microservcio.gatewayreactivo.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Component
public class JwtAuthenticationFilter implements WebFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String jwtToken = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        String token = null;

        if (jwtToken != null && jwtToken.startsWith("Bearer")) {
            token = jwtToken.substring(7);

            DecodedJWT decodedJWT = jwtUtils.validateToken(token);

            if (decodedJWT == null) {
                return Mono.error(new JWTVerificationException("Token invalid, not Authorized"));
            }
            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                String username = jwtUtils.extractUsername(decodedJWT);
                String stringAuthorities = jwtUtils.getSpecificClaim(decodedJWT, "authorities").asString();

                Collection<? extends GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(stringAuthorities);

                Authentication authenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authenticationToken);

                return chain.filter(exchange).contextWrite(
                        ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
            }
        }
        return chain.filter(exchange);
    }
}
