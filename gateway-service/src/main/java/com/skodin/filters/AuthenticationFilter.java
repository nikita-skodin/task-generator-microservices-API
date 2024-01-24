package com.skodin.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skodin.dto.ErrorDto;
import com.skodin.exceptions.InvalidTokenException;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Log4j2
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public AuthenticationFilter(WebClient webClient, ObjectMapper objectMapper) {
        super(Config.class);
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public GatewayFilter apply(Config config) {

        return (exchange, chain) -> {
            String token = extractToken(exchange.getRequest());
            return webClient.get()
                    .uri("/validate")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .bodyToMono(String.class)
                    .flatMap(response -> {
                        if ("true".equals(response)) {
                            return chain.filter(exchange);
                        } else {
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            ServerHttpResponse exchangeResponse = exchange.getResponse();
                            exchangeResponse.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
                            String errorBody;
                            try {
                                errorBody = objectMapper.writeValueAsString(new ErrorDto("Invalid Token", "401"));
                            } catch (JsonProcessingException e) {
                                errorBody = "Some error";
                            }
                            return exchangeResponse.writeWith(Mono.just(exchangeResponse.bufferFactory().wrap(errorBody.getBytes())))
                                    .then(Mono.defer(exchangeResponse::setComplete));
                        }
                    }).onErrorResume(e -> {
                        log.error(e);
                        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
                        ServerHttpResponse exchangeResponse = exchange.getResponse();
                        exchangeResponse.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
                        String errorBody;
                        try {
                            errorBody = objectMapper.writeValueAsString(new ErrorDto("Service unavailable", "503"));
                        } catch (JsonProcessingException jsonProcessingException) {
                            errorBody = "Some error";
                        }
                        return exchangeResponse.writeWith(Mono.just(exchangeResponse.bufferFactory().wrap(errorBody.getBytes())))
                                .then(Mono.defer(exchangeResponse::setComplete));
                    });
        };
    }

    @ExceptionHandler
    private Mono<ResponseEntity<ErrorDto>> handleExceptions(ResponseStatusException e) {
        log.warn("Exception with response status: " + e.getReason());
        return Mono.just(ResponseEntity
                .status(e.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorDto(e.getStatusCode().toString(), e.getReason())));
    }

    private String extractToken(ServerHttpRequest request) {
        String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring("Bearer ".length());
        }
        throw new InvalidTokenException("Token not found");
    }

    public static class Config {
    }
}
