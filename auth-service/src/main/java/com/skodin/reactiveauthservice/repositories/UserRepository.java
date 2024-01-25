package com.skodin.reactiveauthservice.repositories;

import com.skodin.reactiveauthservice.entities.UserEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends R2dbcRepository<UserEntity, Long> {
    Mono<UserEntity> findByUsername(String username);
    Mono<UserEntity> findByEmail(String email);
    Mono<Boolean> existsByUsername(String username);
    Mono<Boolean> existsByEmail(String email);
    Mono<UserEntity> findByActivationCode(String code);
}
