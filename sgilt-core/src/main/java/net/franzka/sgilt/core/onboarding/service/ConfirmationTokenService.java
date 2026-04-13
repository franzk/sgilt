package net.franzka.sgilt.core.onboarding.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import net.franzka.sgilt.core.config.ConfirmationTokenProperties;
import net.franzka.sgilt.core.jwt.TokenJwtService;
import net.franzka.sgilt.core.onboarding.domain.ConfirmationToken;
import net.franzka.sgilt.core.onboarding.exception.InvalidTokenException;
import net.franzka.sgilt.core.onboarding.exception.TokenAlreadyUsedException;
import net.franzka.sgilt.core.onboarding.exception.TokenExpiredException;
import net.franzka.sgilt.core.onboarding.repository.ConfirmationTokenRepository;
import net.franzka.sgilt.core.reservation.domain.Reservation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Service métier pour l'entité {@link ConfirmationToken}.
 */
@Service
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final TokenJwtService confirmationTokenJwtService;
    private final ConfirmationTokenProperties confirmationTokenProperties;

    /**
     * Construit le service avec ses dépendances.
     *
     * @param confirmationTokenRepository le repository des tokens de confirmation
     * @param confirmationTokenJwtService le service JWT qualifié pour les tokens de confirmation
     * @param confirmationTokenProperties les propriétés de configuration des tokens
     */
    public ConfirmationTokenService(
            ConfirmationTokenRepository confirmationTokenRepository,
            @Qualifier("confirmationTokenJwtService") TokenJwtService confirmationTokenJwtService,
            ConfirmationTokenProperties confirmationTokenProperties) {
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.confirmationTokenJwtService = confirmationTokenJwtService;
        this.confirmationTokenProperties = confirmationTokenProperties;
    }

    /**
     * Crée et persiste un token de confirmation pour la réservation donnée.
     * Génère le JWT en interne, extrait le jti et sauvegarde le ConfirmationToken.
     *
     * @param reservation la réservation pour laquelle créer le token
     * @return le JWT complet à envoyer par mail
     */
    public String createForReservation(Reservation reservation) {
        Map<String, Object> claims = Map.of(
                "reservationId", reservation.getId().toString(),
                "reservationCreatedAt", reservation.getCreatedAt().toString()
        );
        String jwt = confirmationTokenJwtService.generateToken(claims, reservation.getEvenement().getEmail());
        String jti = confirmationTokenJwtService.extractJti(jwt);

        ConfirmationToken token = ConfirmationToken.builder()
                .jti(jti)
                .email(reservation.getEvenement().getEmail())
                .reservation(reservation)
                .used(false)
                .expiresAt(LocalDateTime.now().plusHours(
                        confirmationTokenProperties.confirmationExpirationHours()))
                .createdAt(LocalDateTime.now())
                .build();

        confirmationTokenRepository.save(token);
        return jwt;
    }

    /**
     * Valide le JWT de confirmation : vérifie l'expiration, la signature, l'existence en BDD,
     * l'état utilisé, et la cohérence du claim {@code reservationCreatedAt}.
     *
     * @param token le JWT de confirmation reçu par email
     * @return l'entité {@link ConfirmationToken} validée
     * @throws TokenExpiredException     si le token est expiré
     * @throws InvalidTokenException     si la signature est invalide ou si les claims ne correspondent pas
     * @throws EntityNotFoundException   si aucun token ne correspond au jti
     * @throws TokenAlreadyUsedException si le token a déjà été consommé
     */
    public ConfirmationToken validate(String token) {
        if (confirmationTokenJwtService.isExpired(token)) {
            throw new TokenExpiredException();
        }

        Claims claims;
        try {
            claims = confirmationTokenJwtService.extractClaims(token);
        } catch (JwtException e) {
            throw new InvalidTokenException();
        }

        ConfirmationToken confirmationToken = confirmationTokenRepository.findByJti(claims.getId())
                .orElseThrow(EntityNotFoundException::new);

        if (confirmationToken.isUsed()) {
            throw new TokenAlreadyUsedException();
        }

        String claimedCreatedAt = claims.get("reservationCreatedAt", String.class);
        if (!confirmationToken.getReservation().getCreatedAt().toString().equals(claimedCreatedAt)) {
            throw new InvalidTokenException();
        }

        return confirmationToken;
    }

    /**
     * Marque un token de confirmation comme utilisé et persiste la modification.
     *
     * @param token le token à consommer
     */
    public void markAsUsed(ConfirmationToken token) {
        token.setUsed(true);
        confirmationTokenRepository.save(token);
    }

    /**
     * Supprime le token de confirmation associé à une réservation, s'il existe.
     *
     * @param reservationId l'identifiant de la réservation dont on supprime le token
     */
    public void deleteByReservation(UUID reservationId) {
        confirmationTokenRepository.findByReservationId(reservationId)
                .ifPresent(confirmationTokenRepository::delete);
    }
}
