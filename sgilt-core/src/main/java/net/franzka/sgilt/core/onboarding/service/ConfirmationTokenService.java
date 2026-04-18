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
     * Valide le token de confirmation selon son état :
     * <ul>
     *   <li>OPEN : vérifie l'expiration, passe en PENDING_CONFIRMATION avec une période de grâce de 5 minutes et sauvegarde.</li>
     *   <li>PENDING_CONFIRMATION : vérifie que la période de grâce n'est pas dépassée.</li>
     *   <li>USED ou CANCELLED : lève {@link TokenAlreadyUsedException}.</li>
     * </ul>
     *
     * @param token le token de confirmation reçu par email, sous la forme {@code payload-signature}
     * @return l'entité {@link ConfirmationToken} validée
     * @throws net.franzka.sgilt.core.onboarding.exception.InvalidTokenException si la signature HMAC est invalide
     * @throws EntityNotFoundException   si aucun token ne correspond au payload
     * @throws TokenAlreadyUsedException si le token a déjà été consommé ou annulé
     * @throws TokenExpiredException     si le token ou la période de grâce est expirée
     */
    public ConfirmationToken validate(String token) {
        String payload = confirmationTokenHmacService.verify(token);

        ConfirmationToken confirmationToken = confirmationTokenRepository.findByPayload(payload)
                .orElseThrow(EntityNotFoundException::new);

        ConfirmationTokenState state = confirmationToken.getState();

        if (ConfirmationTokenState.USED.equals(state) || ConfirmationTokenState.CANCELLED.equals(state)) {
            throw new TokenAlreadyUsedException();
        }

        if (ConfirmationTokenState.OPEN.equals(state)) {
            if (!confirmationToken.getExpiresAt().isAfter(LocalDateTime.now())) {
                throw new TokenExpiredException();
            }
            confirmationToken.setState(ConfirmationTokenState.PENDING_CONFIRMATION);
            confirmationToken.setConfirmationPeriodExpiresAt(LocalDateTime.now().plusMinutes(5));
            confirmationTokenRepository.save(confirmationToken);
        }

        if (ConfirmationTokenState.PENDING_CONFIRMATION.equals(state)
            && confirmationToken.getConfirmationPeriodExpiresAt().isBefore(LocalDateTime.now())) {
                throw new TokenExpiredException();
        }

        return confirmationToken;
    }

    /**
     * Annule le token de confirmation en état OPEN associé à l'email donné, s'il en existe un.
     * Sans effet si aucun token OPEN n'est trouvé pour cet email.
     *
     * @param email l'adresse email dont on cherche un token OPEN à annuler
     */
    public void cancelExistingTokenForEmail(String email) {
        confirmationTokenRepository.findByEmailAndState(email, ConfirmationTokenState.OPEN)
                .forEach(token -> {
                    token.setState(ConfirmationTokenState.CANCELLED);
                    confirmationTokenRepository.save(token);
                });
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
