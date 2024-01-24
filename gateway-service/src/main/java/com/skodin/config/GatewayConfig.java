package com.skodin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GatewayConfig {
    @Bean
    public WebClient webClient (){
        return WebClient.builder().baseUrl("http://localhost:8080/api/auth").build();
    }
}