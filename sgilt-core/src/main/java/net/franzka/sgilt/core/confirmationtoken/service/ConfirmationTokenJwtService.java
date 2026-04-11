package net.franzka.sgilt.core.confirmationtoken.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.config.ConfirmationTokenProperties;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;

import io.jsonwebtoken.security.Keys;

@Service
@RequiredArgsConstructor
public class ConfirmationTokenJwtService {

    private final ConfirmationTokenProperties props;

    public String generateToken(String email, UUID reservationId, LocalDateTime reservationCreatedAt) {
        Date now = new Date();
        Date expiration = Date.from(
                LocalDateTime.now()
                        .plusHours(props.confirmationExpirationHours())
                        .toInstant(ZoneOffset.UTC)
        );

        return Jwts.builder()
                .subject(email)
                .claim("reservationId", reservationId.toString())
                .claim("reservationCreatedAt", reservationCreatedAt.toString())
                .id(UUID.randomUUID().toString())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    public Claims verifyAndExtractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractJti(String token) {
        return verifyAndExtractClaims(token).getId();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(props.confirmationSecret().getBytes(StandardCharsets.UTF_8));
    }
}
