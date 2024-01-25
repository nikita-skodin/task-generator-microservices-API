package com.skodin.reactiveauthservice.config;

import com.skodin.reactiveauthservice.entities.UserEntity;
import com.skodin.reactiveauthservice.exceptions.InvalidToken;
import com.skodin.reactiveauthservice.services.UserService;
import com.skodin.reactiveauthservice.util.CustomPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final UserService userService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
        return userService.findByUsername(principal.getName())
                .filter(UserEntity::isEnabled)
                .switchIfEmpty(Mono.error(new InvalidToken("User disabled")))
                .map(user -> authentication);
    }
}
