package com.skodin.reactiveauthservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
@SpringBootApplication
public class ReactiveAuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactiveAuthServiceApplication.class, args);
    }

}
