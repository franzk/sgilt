package net.franzka.sgilt.core.onboarding.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.service.EvenementService;
import net.franzka.sgilt.core.jwt.TokenJwtService;
import net.franzka.sgilt.core.onboarding.domain.ConfirmationToken;
import net.franzka.sgilt.core.onboarding.dto.ConfirmAccountRequest;
import net.franzka.sgilt.core.onboarding.dto.ConfirmAccountResponse;
import net.franzka.sgilt.core.onboarding.dto.DemandeInitialeResponse;
import net.franzka.sgilt.core.onboarding.dto.DemandeInitialeRequest;
import net.franzka.sgilt.core.onboarding.dto.SetPasswordTokenDto;
import net.franzka.sgilt.core.onboarding.exception.InvalidTokenException;
import net.franzka.sgilt.core.onboarding.exception.TokenExpiredException;
import net.franzka.sgilt.core.onboarding.mailer.OnboardingMailerService;
import net.franzka.sgilt.core.reservation.domain.Reservation;
import net.franzka.sgilt.core.reservation.service.ReservationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class OnboardingService {

    private final EvenementService evenementService;
    private final ReservationService reservationService;
    private final ConfirmationTokenService confirmationTokenService;
    private final TokenJwtService setPasswordTokenJwtService;
    private final OnboardingMailerService onboardingMailerService;

    /**
     * Construit le service avec ses dépendances.
     *
     * @param evenementService           le service de gestion des événements
     * @param reservationService         le service de gestion des réservations
     * @param confirmationTokenService   le service métier des tokens de confirmation
     * @param setPasswordTokenJwtService le service JWT qualifié pour les tokens set-password
     * @param onboardingMailerService    le service d'envoi de mails d'onboarding
     */
    public OnboardingService(
            EvenementService evenementService,
            ReservationService reservationService,
            ConfirmationTokenService confirmationTokenService,
            @Qualifier("setPasswordTokenJwtService") TokenJwtService setPasswordTokenJwtService,
            OnboardingMailerService onboardingMailerService) {
        this.evenementService = evenementService;
        this.reservationService = reservationService;
        this.confirmationTokenService = confirmationTokenService;
        this.setPasswordTokenJwtService = setPasswordTokenJwtService;
        this.onboardingMailerService = onboardingMailerService;
    }

    /**
     * Crée
     * - un événement en draft,
     * - une réservation en draft
     * - un token de confirmation
     * --> puis déclenche l'envoi du mail de confirmation.
     *
     * @param request les données de la demande initiale (nom, email, prestataireId)
     * @return l'email de l'utilisateur encapsulé dans la réponse
     */
    public DemandeInitialeResponse createDemandeReservation(DemandeInitialeRequest request) {

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

        reservationService.activateDemande(reservationId);

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
