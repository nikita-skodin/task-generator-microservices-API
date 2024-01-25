package com.skodin.reactiveauthservice.controllers;

import com.skodin.reactiveauthservice.dtos.AuthenticationRequest;
import com.skodin.reactiveauthservice.dtos.AuthenticationResponse;
import com.skodin.reactiveauthservice.dtos.ErrorDTO;
import com.skodin.reactiveauthservice.dtos.RegisterRequest;
import com.skodin.reactiveauthservice.entities.UserEntity;
import com.skodin.reactiveauthservice.enums.Role;
import com.skodin.reactiveauthservice.exceptions.BadRequestException;
import com.skodin.reactiveauthservice.services.AuthenticationService;
import com.skodin.reactiveauthservice.validators.UserValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Log4j2
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserValidator userValidator;
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public Mono<ResponseEntity<AuthenticationResponse>> register(
            @Valid @RequestBody Mono<RegisterRequest> request) {

        return request
                .flatMap(registerRequest -> {
                    Errors errors = new DirectFieldBindingResult(registerRequest, "registerRequest");

                    UserEntity user = UserEntity.builder()
                            .username(registerRequest.getUsername())
                            .password(registerRequest.getPassword())
                            .email(registerRequest.getEmail())
                            .role(Role.valueOf(registerRequest.getRole()))
                            .activationCode(UUID.randomUUID().toString())
                            .build();

                    return userValidator.validate(user, errors)
                            .then(Mono.defer(() -> {
                                checkErrors(errors);
                                log.info("Tokens have been issued for user with username {}",
                                        user.getUsername());
                                return authenticationService.register(user)
                                        .map(ResponseEntity::ok);
                            }));
                });
    }

    @PostMapping("/authenticate")
    public Mono<ResponseEntity<AuthenticationResponse>> authenticate(
            @Valid @RequestBody Mono<AuthenticationRequest> authenticationRequest
    ) {
        return authenticationRequest
                .flatMap(request -> {
                    log.info("Attempt to authenticate");
                    return authenticationService.authenticate(request).map(ResponseEntity::ok);
                });
    }

    @GetMapping("/refresh")
    public Mono<ResponseEntity<AuthenticationResponse>> refresh(
            @RequestBody String refreshToken
    ) {
        log.info("Attempt to refresh tokens");
        return authenticationService.refresh(refreshToken).map(ResponseEntity::ok);
    }

    @GetMapping("/activate/{code}")
    public Mono<ResponseEntity<Boolean>> enable(
            @PathVariable String code) {
        // TODO add some beautiful html instead of bol
        return authenticationService.activate(code).map(ResponseEntity::ok);
    }

    @GetMapping("/validate")
    public Mono<ResponseEntity<Boolean>> validate(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        String token = extractTokenFromAuthorizationHeader(authorizationHeader);

        return authenticationService.isTokenValid(token)
                .map(ResponseEntity::ok);
    }

    private String extractTokenFromAuthorizationHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring("Bearer ".length());
        }
        throw new BadRequestException("HttpHeader AUTHORIZATION is invalid");
    }

    private void checkErrors(Errors errors) {
        if (errors.hasErrors()) {
            List<ObjectError> allErrors = errors.getAllErrors();
            for (var error : allErrors) {
                if (Objects.equals(error.getCode(), "400")) {
                    log.warn("Exception during validation: " + error.getDefaultMessage());
                    throw new BadRequestException(error.getDefaultMessage());
                }
            }
        }
    }

    @ExceptionHandler(WebExchangeBindException.class)
    private ResponseEntity<List<ErrorDTO>> handleException(WebExchangeBindException e) {
        BindingResult bindingResult = e.getBindingResult();
        List<ErrorDTO> errorDTOS = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            for (var error : allErrors) {
                log.warn("Exception in body: " + e.getReason());
                errorDTOS.add(new ErrorDTO(error.getCode(), error.getDefaultMessage()));
            }
        }
        return ResponseEntity.badRequest().body(errorDTOS);
    }

    @ExceptionHandler
    private Mono<ResponseEntity<ErrorDTO>> handleExceptions(ResponseStatusException e) {
        log.warn("Exception with response status: " + e.getReason());
        return Mono.just(ResponseEntity
                .status(e.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorDTO(e.getStatusCode().toString(), e.getReason())));
    }
}