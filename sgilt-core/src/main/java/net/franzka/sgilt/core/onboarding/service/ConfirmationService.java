package net.franzka.sgilt.core.onboarding.service;

import jakarta.persistence.EntityNotFoundException;
import net.franzka.sgilt.core.jwt.TokenJwtService;
import net.franzka.sgilt.core.onboarding.domain.ConfirmationToken;
import net.franzka.sgilt.core.onboarding.dto.SetPasswordTokenDto;
import net.franzka.sgilt.core.onboarding.exception.InvalidTokenException;
import net.franzka.sgilt.core.onboarding.exception.TokenExpiredException;
import net.franzka.sgilt.core.onboarding.exception.TokenAlreadyUsedException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service pour la confirmation de la demande initiale par email
 */
@Service
public class ConfirmationService {

    private final ConfirmationTokenService confirmationTokenService;
    private final TokenJwtService setPasswordTokenJwtService;

    /**
     * Construit le service avec ses dépendances.
     *
     * @param confirmationTokenService    le service métier des tokens de confirmation
     * @param setPasswordTokenJwtService  le service JWT qualifié pour les tokens set-password
     */
    public ConfirmationService(
            ConfirmationTokenService confirmationTokenService,
            @Qualifier("setPasswordTokenJwtService") TokenJwtService setPasswordTokenJwtService) {
        this.confirmationTokenService = confirmationTokenService;
        this.setPasswordTokenJwtService = setPasswordTokenJwtService;
    }

    /**
     * Valide le token de confirmation JWT issu de l'url de validation envoyé par mail
     * Retourne l'entité correspondante trouvée en BDD
     *
     * @param token le JWT de confirmation reçu par email
     * @return l'entité {@link ConfirmationToken} validée
     *
     * @throws TokenExpiredException     si le token est expiré
     * @throws InvalidTokenException     si la signature est invalide ou si les claims ne correspondent pas
     * @throws EntityNotFoundException   si aucun token ne correspond au jti
     * @throws TokenAlreadyUsedException si le token a déjà été consommé
     */
    public ConfirmationToken validateConfirmationToken(String token) {
        return confirmationTokenService.validate(token);
    }

    /**
     * Marque le token de confirmation comme utilisé.
     *
     * @param confirmationToken le token à consommer
     */
    public void consumeConfirmationToken(ConfirmationToken confirmationToken) {
        confirmationTokenService.markAsUsed(confirmationToken);
    }

    /**
     * Génère un JWT set-password à partir du token de confirmation consommé et construit la réponse.
     *
     * @param confirmationToken le token de confirmation validé et consommé
     * @return la réponse contenant l'email et le JWT set-password
     */
    public SetPasswordTokenDto generateSetPasswordResponse(ConfirmationToken confirmationToken) {
        String email = confirmationToken.getEmail();
        Map<String, Object> claims = Map.of(
                "reservationId", confirmationToken.getReservation().getId().toString()
        );
        String setPasswordToken = setPasswordTokenJwtService.generateToken(claims, email);
        return new SetPasswordTokenDto(email, setPasswordToken);
    }
}
