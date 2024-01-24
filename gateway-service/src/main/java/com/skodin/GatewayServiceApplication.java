package com.skodin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class GatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }

//    @Bean
//    @LoadBalanced // без него не работает
//    public RestTemplate restTemplate (RestTemplateBuilder restTemplateBuilder){
//        return restTemplateBuilder.build();
//    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

}
