package com.skodin.filters;

import com.skodin.dto.ErrorDto;
import com.skodin.exceptions.InvalidTokenException;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final RestTemplate restTemplate;

    public AuthenticationFilter(RestTemplate restTemplate) {
        super(Config.class);
        this.restTemplate = restTemplate;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                throw new RuntimeException("missing authorization header");
            }

            String token = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            String url = "http://localhost:8765/api/auth/validate?token=" + token;
            System.err.println(url);

            ResponseEntity<Boolean> response = restTemplate.getForEntity(url, Boolean.class);
            System.err.println(response.getBody());

            if (response.getBody() == null || !response.getBody()) {
                throw new InvalidTokenException("Token is invalid");
            }

            return chain.filter(exchange);
        });
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorDto> handleException(InvalidTokenException e) {
        return ResponseEntity
                .status(403)
                .body(new ErrorDto("FORBIDDEN", e.getMessage()));
    }

    public static class Config { }
}