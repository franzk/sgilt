package net.franzka.sgilt.core.onboarding.service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.config.ConfirmationTokenProperties;
import net.franzka.sgilt.core.jwt.JwtService;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SetPasswordTokenJwtService {

    private static final String SALT = "set-password";
    private static final Duration EXPIRATION = Duration.ofMinutes(5);

    private final JwtService jwtService;
    private final ConfirmationTokenProperties props;

    public String generateToken(String email, UUID reservationId) {
        Map<String, Object> claims = Map.of(
                "reservationId", reservationId.toString()
        );
        return jwtService.generateToken(
                claims,
                email,
                EXPIRATION,
                deriveKey()
        );
    }

    public Claims extractClaims(String token) {
        return jwtService.extractClaims(token, deriveKey());
    }

    public boolean isExpired(String token) {
        return jwtService.isExpired(token, deriveKey());
    }

    private SecretKey deriveKey() {
        return jwtService.deriveKey(props.confirmationSecret(), SALT);
    }
}
