package net.franzka.sgilt.core.onboarding.service;

import jakarta.persistence.EntityNotFoundException;
import net.franzka.sgilt.core.config.ConfirmationTokenProperties;
import net.franzka.sgilt.core.jwt.ConfirmationTokenHmacService;
import net.franzka.sgilt.core.onboarding.domain.ConfirmationToken;
import net.franzka.sgilt.core.onboarding.domain.ConfirmationTokenState;
import net.franzka.sgilt.core.onboarding.exception.TokenAlreadyUsedException;
import net.franzka.sgilt.core.onboarding.exception.TokenExpiredException;
import net.franzka.sgilt.core.onboarding.repository.ConfirmationTokenRepository;
import net.franzka.sgilt.core.reservation.domain.Reservation;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service métier pour l'entité {@link ConfirmationToken}.
 */
@Service
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final ConfirmationTokenHmacService confirmationTokenHmacService;
    private final ConfirmationTokenProperties confirmationTokenProperties;

    /**
     * Construit le service avec ses dépendances.
     *
     * @param confirmationTokenRepository le repository des tokens de confirmation
     * @param confirmationTokenHmacService le service HMAC de génération et vérification des tokens
     * @param confirmationTokenProperties les propriétés de configuration des tokens
     */
    public ConfirmationTokenService(
            ConfirmationTokenRepository confirmationTokenRepository,
            ConfirmationTokenHmacService confirmationTokenHmacService,
            ConfirmationTokenProperties confirmationTokenProperties) {
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.confirmationTokenHmacService = confirmationTokenHmacService;
        this.confirmationTokenProperties = confirmationTokenProperties;
    }

    /**
     * Crée et persiste un token de confirmation pour la réservation donnée.
     * Génère le token HMAC en interne, extrait le payload et sauvegarde le {@link ConfirmationToken}.
     *
     * @param reservation la réservation pour laquelle créer le token
     * @return le token complet {@code payload-signature} à envoyer par mail
     */
    public String createForReservation(Reservation reservation) {
        String token = confirmationTokenHmacService.generateToken();
        String payload = token.substring(0, token.lastIndexOf("-"));

        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .payload(payload)
                .email(reservation.getEvenement().getEmail())
                .reservation(reservation)
                .state(ConfirmationTokenState.OPEN)
                .expiresAt(LocalDateTime.now().plusHours(
                        confirmationTokenProperties.confirmationExpirationHours()))
                .createdAt(LocalDateTime.now())
                .build();

        confirmationTokenRepository.save(confirmationToken);
        return token;
    }

    /**
     * Valide le token de confirmation : vérifie la signature HMAC, l'existence en BDD,
     * l'état utilisé et l'expiration.
     *
     * @param token le token de confirmation reçu par email, sous la forme {@code payload-signature}
     * @return l'entité {@link ConfirmationToken} validée
     * @throws net.franzka.sgilt.core.onboarding.exception.InvalidTokenException si la signature HMAC est invalide
     * @throws EntityNotFoundException   si aucun token ne correspond au payload
     * @throws TokenAlreadyUsedException si le token a déjà été consommé
     * @throws TokenExpiredException     si le token est expiré
     */
    public ConfirmationToken validate(String token) {
        String payload = confirmationTokenHmacService.verify(token);

        ConfirmationToken confirmationToken = confirmationTokenRepository.findByPayload(payload)
                .orElseThrow(EntityNotFoundException::new);

        if (ConfirmationTokenState.USED.equals(confirmationToken.getState())) {
            throw new TokenAlreadyUsedException();
        }

        if (!confirmationToken.getExpiresAt().isAfter(LocalDateTime.now())) {
            throw new TokenExpiredException();
        }

        return confirmationToken;
    }

    /**
     * Marque un token de confirmation comme utilisé et persiste la modification.
     *
     * @param token le token à consommer
     */
    public void markAsUsed(ConfirmationToken token) {
        token.setState(ConfirmationTokenState.USED);
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
