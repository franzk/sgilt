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

/**
 * Service JWT dédié aux tokens de confirmation d'email envoyés lors de l'onboarding.
 * Utilise une clé dérivée avec le sel {@code "confirmation"}.
 */
@Service
@RequiredArgsConstructor
public class ConfirmationTokenJwtService {

    private static final String SALT = "confirmation";

    private final JwtService jwtService;
    private final ConfirmationTokenProperties props;

    /**
     * Génère un JWT de confirmation contenant l'email, l'identifiant de réservation
     * et la date de création de la réservation (utilisée comme claim de sécurité croisé).
     *
     * @param email                 l'email du destinataire
     * @param reservationId         l'identifiant de la réservation associée
     * @param reservationCreatedAt  la date de création de la réservation
     * @return le JWT de confirmation signé
     */
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

    /**
     * Parse et vérifie le JWT de confirmation, puis retourne ses claims.
     *
     * @param token le JWT de confirmation
     * @return les claims du token
     * @throws io.jsonwebtoken.JwtException si la signature est invalide ou le token malformé
     */
    public Claims extractClaims(String token) {
        return jwtService.extractClaims(token, deriveKey());
    }

    /**
     * Indique si le JWT de confirmation est expiré.
     *
     * @param token le JWT de confirmation
     * @return {@code true} si expiré, {@code false} sinon
     */
    public boolean isExpired(String token) {
        return jwtService.isExpired(token, deriveKey());
    }

    /**
     * Extrait le jti (identifiant unique) du JWT de confirmation.
     *
     * @param token le JWT de confirmation
     * @return le jti du token
     */
    public String extractJti(String token) {
        return extractClaims(token).getId();
    }

    private SecretKey deriveKey() {
        return jwtService.deriveKey(props.confirmationSecret(), SALT);
    }
}
