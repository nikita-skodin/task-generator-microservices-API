package com.skodin.reactiveauthservice.services;

import com.skodin.reactiveauthservice.entities.UserEntity;
import com.skodin.reactiveauthservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Log4j2
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public static UserEntity getCurrentUser() {
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return (UserEntity) principal;
    }

    public Mono<Boolean> existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public Mono<Boolean> existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Mono<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Mono<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Mono<UserEntity> findByActivationCode(String code) {
        return userRepository.findByActivationCode(code);
    }

    @Transactional
    public Mono<UserEntity> save(UserEntity user) {
        log.info("User with username {} has been saved",
                user.getUsername());
        return userRepository.save(user);
    }

    @Transactional
    public Mono<UserEntity> activateUser(UserEntity user) {
        user.setActivationCode(null);
        return save(user);
    }
}
