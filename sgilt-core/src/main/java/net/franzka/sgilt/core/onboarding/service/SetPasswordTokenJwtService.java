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

/**
 * Service JWT dédié aux tokens de définition de mot de passe, émis après vérification
 * du token de confirmation. Expiration fixe de 5 minutes.
 * Utilise une clé dérivée avec le sel {@code "set-password"}.
 */
@Service
@RequiredArgsConstructor
public class SetPasswordTokenJwtService {

    private static final String SALT = "set-password";
    private static final Duration EXPIRATION = Duration.ofMinutes(5);

    private final JwtService jwtService;
    private final ConfirmationTokenProperties props;

    /**
     * Génère un JWT set-password contenant l'email et l'identifiant de réservation.
     *
     * @param email         l'email de l'utilisateur
     * @param reservationId l'identifiant de la réservation à confirmer
     * @return le JWT set-password signé, valide 5 minutes
     */
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

    /**
     * Parse et vérifie le JWT set-password, puis retourne ses claims.
     *
     * @param token le JWT set-password
     * @return les claims du token
     * @throws io.jsonwebtoken.JwtException si la signature est invalide ou le token malformé
     */
    public Claims extractClaims(String token) {
        return jwtService.extractClaims(token, deriveKey());
    }

    /**
     * Indique si le JWT set-password est expiré.
     *
     * @param token le JWT set-password
     * @return {@code true} si expiré, {@code false} sinon
     */
    public boolean isExpired(String token) {
        return jwtService.isExpired(token, deriveKey());
    }

    private SecretKey deriveKey() {
        return jwtService.deriveKey(props.confirmationSecret(), SALT);
    }
}
