package com.safepay.api_gateway.filters;

import java.util.List;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.safepay.api_gateway.util.JwtUtil;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private static final List<String> PUBLIC_PATHS = List.of(
            "/auth/signup",
            "/auth/login"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getPath().value();
        String normalizedPath = path.replaceAll("/+$", "");


        if (PUBLIC_PATHS.contains(normalizedPath)) {
            return chain.filter(exchange);
        }

   
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {
           
            String token = authHeader.substring(7);
            Claims claims = JwtUtil.validateToken(token);

       
            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(
                            exchange.getRequest()
                                    .mutate()
                                    .header("X-User-Email", claims.getSubject())
                                    .build()
                    )
                    .build();

            return chain.filter(mutatedExchange);

        } catch (Exception ex) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
