package com.skodin.reactiveauthservice.validators;

import com.skodin.reactiveauthservice.entities.UserEntity;
import com.skodin.reactiveauthservice.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserValidator  {

    private final UserService userService;

    public Mono<Void> validate(Object target, Errors errors) {
        UserEntity user = (UserEntity) target;

        String password = user.getPassword();

        if (password != null && (password.length() < 8 || password.length() > 40)) {
            errors.rejectValue("password", "400",
                    "Password length must be between 8 and 40 characters");
        }

        Mono<Boolean> usernameExistsMono = userService.existsByUsername(user.getUsername());
        Mono<Boolean> emailExistsMono = userService.existsByEmail(user.getEmail());

        return usernameExistsMono
                .flatMap(usernameExists -> {
                    if (usernameExists) {
                        errors.rejectValue("username", "400",
                                "User with username " + user.getUsername() + " is already exist");
                    }
                    return emailExistsMono;
                })
                .flatMap(emailExists -> {
                    if (emailExists) {
                        errors.rejectValue("email", "400",
                                "User with email " + user.getEmail() + " is already exist");
                    }
                    return Mono.empty();
                });
    }
}
