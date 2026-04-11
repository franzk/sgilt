package net.franzka.sgilt.core.onboarding.service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.config.ConfirmationTokenProperties;
import net.franzka.sgilt.core.jwt.JwtService;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConfirmationTokenJwtService {

    private static final String SALT = "confirmation";

    private final JwtService jwtService;
    private final ConfirmationTokenProperties props;

    public String generateToken(String email, UUID reservationId, LocalDateTime reservationCreatedAt) {
        Map<String, Object> claims = Map.of(
                "reservationId", reservationId.toString(),
                "reservationCreatedAt", reservationCreatedAt.toString()
        );
        return jwtService.generateToken(
                claims,
                email,
                Duration.ofHours(props.confirmationExpirationHours()),
                deriveKey()
        );
    }

    public Claims extractClaims(String token) {
        return jwtService.extractClaims(token, deriveKey());
    }

    public boolean isExpired(String token) {
        return jwtService.isExpired(token, deriveKey());
    }

    public String extractJti(String token) {
        return extractClaims(token).getId();
    }

    private SecretKey deriveKey() {
        return jwtService.deriveKey(props.confirmationSecret(), SALT);
    }
}
