package com.skodin.reactiveauthservice.services;

import com.skodin.reactiveauthservice.dtos.AuthenticationResponse;
import com.skodin.reactiveauthservice.entities.UserEntity;
import com.skodin.reactiveauthservice.exceptions.InvalidToken;
import com.skodin.reactiveauthservice.util.properties.JWTProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JWTProperties jwtProperties;
    private final UserService userService;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateAccessToken(UserEntity userEntity) {
        return generateAccessToken(Map.of(
                "id", userEntity.getId(),
                "roles", userEntity.getAuthorities()), userEntity);
    }

    public String generateAccessToken(Map<String, Object> extractClaims, UserEntity userEntity) {

        if (extractClaims == null) {
            extractClaims = Collections.emptyMap();
        }

        return Jwts
                .builder()
                .setClaims(extractClaims)
                .setSubject(userEntity.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getAccess()))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(UserEntity userEntity) {
        return generateRefreshToken(Map.of(
                "id", userEntity.getId(),
                "roles", userEntity.getAuthorities()), userEntity);
    }

    public String generateRefreshToken(Map<String, Object> extractClaims, UserEntity userEntity) {

        if (extractClaims == null) {
            extractClaims = Collections.emptyMap();
        }

        return Jwts
                .builder()
                .setClaims(extractClaims)
                .setSubject(userEntity.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getRefresh()))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Mono<AuthenticationResponse> refreshUserToken(String refreshToken) {

        String username;
        try {
            username = extractUsername(refreshToken);
        } catch (Exception e) {
            return Mono.error(new InvalidToken("Token is damaged"));
        }

        return userService.findByUsername(username)
                .flatMap(user -> isTokenValid(refreshToken)
                        .flatMap(isValid -> {
                            if (!isValid) {
                                return Mono.error(new InvalidToken("Token is invalid"));
                            }

                            return Mono.just(new AuthenticationResponse(
                                    generateAccessToken(extractAllClaims(refreshToken), user),
                                    generateRefreshToken(extractAllClaims(refreshToken), user)));
                        }));
    }

    public Mono<Boolean> isTokenValid(String token) {
        try {
            String username = extractUsername(token);
            return userService.findByUsername(username)
                    .flatMap(user -> Mono.just(!isTokenExpired(token) && user.isEnabled()));
        } catch (Exception e) {
            return Mono.just(false);
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
