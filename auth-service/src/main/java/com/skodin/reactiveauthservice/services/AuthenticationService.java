package com.skodin.reactiveauthservice.services;

import com.skodin.reactiveauthservice.dtos.AuthenticationRequest;
import com.skodin.reactiveauthservice.dtos.AuthenticationResponse;
import com.skodin.reactiveauthservice.entities.UserEntity;
import com.skodin.reactiveauthservice.exceptions.AccountDisableException;
import com.skodin.reactiveauthservice.exceptions.BadRequestException;
import com.skodin.reactiveauthservice.exceptions.NotFoundException;
import com.skodin.reactiveauthservice.util.MailSandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Log4j2
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final JwtService jwtService;
    private final MailSandler mailSandler;

    @Value("${enable.link}")
    private String enableLink;
    private static final String MAIL_SUBJECT = "Activate your account";
    public Mono<AuthenticationResponse> register(UserEntity user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userService.save(user)
                .flatMap(savedUser -> {
                    String accessToken = jwtService.generateAccessToken(savedUser);
                    String refreshToken = jwtService.generateRefreshToken(savedUser);
                    AuthenticationResponse authenticationResponse = new AuthenticationResponse(accessToken, refreshToken);
                    String link = enableLink + user.getActivationCode();
                    mailSandler.sendActivationCodeMessage(
                            user.getEmail(), MAIL_SUBJECT, link, user.getUsername());
                    return Mono.just(authenticationResponse);
                });
    }

    public Mono<AuthenticationResponse> authenticate(AuthenticationRequest request) {
        return userService.findByUsername(request.getUsername())
                .flatMap(user -> {

                    if (!user.isEnabled()) {
                        return Mono.error(new AccountDisableException("Account disabled"));
                    }

                    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                        return Mono.error(new BadRequestException("Invalid password"));
                    }

                    String accessToken = jwtService.generateAccessToken(user);
                    String refreshToken = jwtService.generateRefreshToken(user);

                    AuthenticationResponse authenticationResponse = new AuthenticationResponse(accessToken, refreshToken);
                    return Mono.just(authenticationResponse);
                }).switchIfEmpty(Mono.error(
                        new NotFoundException("User with username %s is not found".formatted(request.getUsername()))));
    }

    public Mono<AuthenticationResponse> refresh(String refreshToken) {
        return jwtService.refreshUserToken(refreshToken);
    }

    public Mono<Boolean> activate(String code) {
        if (code == null) {
            return Mono.error(new BadRequestException("Code cannot be null"));
        }
        return userService.findByActivationCode(code)
                .flatMap(user -> userService.activateUser(user)
                        .flatMap(user1 -> Mono.just(true))
                        .switchIfEmpty(Mono.just(false)))
                .switchIfEmpty(Mono.error(new BadRequestException("No user with such code")));
    }

    public Mono<Boolean> isTokenValid(String token) {
        return jwtService.isTokenValid(token);
    }
}
