package net.franzka.sgilt.core.onboarding.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.service.EvenementService;
import net.franzka.sgilt.core.onboarding.domain.ConfirmationToken;
import net.franzka.sgilt.core.onboarding.dto.ConfirmAccountRequest;
import net.franzka.sgilt.core.onboarding.dto.ConfirmAccountResponse;
import net.franzka.sgilt.core.onboarding.dto.DemandeInitialeResponse;
import net.franzka.sgilt.core.onboarding.dto.NewEvenementRequest;
import net.franzka.sgilt.core.onboarding.dto.VerifyTokenResponse;
import net.franzka.sgilt.core.onboarding.exception.InvalidTokenException;
import net.franzka.sgilt.core.onboarding.exception.TokenExpiredException;
import net.franzka.sgilt.core.onboarding.mailer.OnboardingMailerService;
import net.franzka.sgilt.core.reservation.domain.Reservation;
import net.franzka.sgilt.core.reservation.service.ReservationService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OnboardingService {

    private final EvenementService evenementService;
    private final ReservationService reservationService;
    private final ConfirmationTokenService confirmationTokenService;
    private final SetPasswordTokenJwtService setPasswordTokenJwtService;
    private final OnboardingMailerService onboardingMailerService;

    /**
     * Crée un événement, une réservation et un token de confirmation, puis déclenche l'envoi du mail de confirmation.
     *
     * @param request les données de la demande initiale (nom, email, prestataireId)
     * @return l'email de l'utilisateur encapsulé dans la réponse
     */
    public DemandeInitialeResponse submitDemandeTunnel(NewEvenementRequest request) {

        Evenement evenement = evenementService.createDraft(
                request.firstname(),
                request.lastname(),
                request.email()
        );

        Reservation reservation = reservationService.createDraft(
                evenement,
                request.prestataireId()
        );

        String jwt = confirmationTokenService.createForReservation(reservation);

        // TODO: KC Admin API — vérifier si email existe
        // → si oui : onboardingMailerService.sendSecurityAlertEmail()
        // → si non : onboardingMailerService.sendConfirmationEmail()
        onboardingMailerService.sendConfirmationEmail(request.email(), jwt);

        // TODO: à retirer quand le mailer sera branché
        log.info("Confirmation JWT: {}", jwt);

        return new DemandeInitialeResponse(request.email());
    }

    /**
     * Valide le token de confirmation JWT : vérifie l'expiration, la signature, l'existence en BDD,
     * l'état utilisé, et la cohérence du claim {@code reservationCreatedAt} avec la réservation en BDD.
     *
     * @param token le JWT de confirmation reçu par email
     * @return l'entité {@link ConfirmationToken} validée
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
    public VerifyTokenResponse generateSetPasswordResponse(ConfirmationToken confirmationToken) {
        String email = confirmationToken.getEmail();
        String setPasswordToken = setPasswordTokenJwtService.generateToken(
                email,
                confirmationToken.getReservation().getId()
        );
        return new VerifyTokenResponse(email, setPasswordToken);
    }

    /**
     * Valide le JWT set-password, charge la réservation, passe son statut à NOUVELLE,
     * supprime le token de confirmation, et déclenche l'envoi du mail de bienvenue.
     *
     * @param request le JWT set-password et le mot de passe choisi par l'utilisateur
     * @return les tokens d'accès Keycloak (mockés en attendant l'intégration KC)
     * @throws TokenExpiredException   si le JWT set-password est expiré
     * @throws InvalidTokenException   si le JWT est invalide
     * @throws EntityNotFoundException si la réservation est introuvable
     */
    public ConfirmAccountResponse confirmAccount(ConfirmAccountRequest request) {
        String token = request.setPasswordToken();

        if (setPasswordTokenJwtService.isExpired(token)) {
            throw new TokenExpiredException();
        }

        Claims claims;
        try {
            claims = setPasswordTokenJwtService.extractClaims(token);
        } catch (JwtException e) {
            throw new InvalidTokenException();
        }

        UUID reservationId = UUID.fromString(claims.get("reservationId", String.class));

        reservationService.activateReservation(reservationId);

        // TODO: KC Admin API — créer user (email + password)
        // TODO: KC Admin API — set password (temporary=false)
        // TODO: KC — récupérer access + refresh token

        confirmationTokenService.deleteByReservation(reservationId);

        String email = claims.getSubject();
        onboardingMailerService.sendWelcomeEmail(email);

        return new ConfirmAccountResponse(
                "TODO_KC_ACCESS_TOKEN",
                "TODO_KC_REFRESH_TOKEN"
        );
    }
}
