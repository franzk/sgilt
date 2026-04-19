package net.franzka.sgilt.core.onboarding.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.service.EvenementService;
import net.franzka.sgilt.core.jwt.TokenJwtService;
import net.franzka.sgilt.core.onboarding.dto.ConfirmAccountRequest;
import net.franzka.sgilt.core.onboarding.dto.ConfirmAccountResponse;
import net.franzka.sgilt.core.onboarding.dto.DemandeInitialeResponse;
import net.franzka.sgilt.core.onboarding.dto.DemandeInitialeRequest;
import net.franzka.sgilt.core.onboarding.exception.InvalidTokenException;
import net.franzka.sgilt.core.onboarding.exception.TokenExpiredException;
import net.franzka.sgilt.core.keycloak.KeycloakAdminService;
import net.franzka.sgilt.core.keycloak.KeycloakTokenResponse;
import net.franzka.sgilt.core.onboarding.mailer.OnboardingMailerService;
import net.franzka.sgilt.core.reservation.domain.Reservation;
import net.franzka.sgilt.core.reservation.service.ReservationService;
import net.franzka.sgilt.core.utilisateur.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OnboardingService {

    private final EvenementService evenementService;
    private final ReservationService reservationService;
    private final ConfirmationTokenService confirmationTokenService;
    private final TokenJwtService setPasswordTokenJwtService;
    private final OnboardingMailerService onboardingMailerService;
    private final UtilisateurService utilisateurService;
    private final KeycloakAdminService keycloakAdminService;

    /**
     * Construit le service avec ses dépendances.
     *
     * @param evenementService           le service de gestion des événements
     * @param reservationService         le service de gestion des réservations
     * @param confirmationTokenService   le service métier des tokens de confirmation
     * @param setPasswordTokenJwtService le service JWT qualifié pour les tokens set-password
     * @param onboardingMailerService    le service d'envoi de mails d'onboarding
     * @param utilisateurService         le service métier des utilisateurs
     * @param keycloakAdminService       le service d'administration Keycloak
     */
    public OnboardingService(
            EvenementService evenementService,
            ReservationService reservationService,
            ConfirmationTokenService confirmationTokenService,
            @Qualifier("setPasswordTokenJwtService") TokenJwtService setPasswordTokenJwtService,
            OnboardingMailerService onboardingMailerService,
            UtilisateurService utilisateurService,
            KeycloakAdminService keycloakAdminService) {
        this.evenementService = evenementService;
        this.reservationService = reservationService;
        this.confirmationTokenService = confirmationTokenService;
        this.setPasswordTokenJwtService = setPasswordTokenJwtService;
        this.onboardingMailerService = onboardingMailerService;
        this.utilisateurService = utilisateurService;
        this.keycloakAdminService = keycloakAdminService;
    }

    /**
     * Traite une demande initiale de réservation.
     * Si l'email est déjà associé à un compte existant, envoie une alerte de sécurité et retourne sans créer de parcours.
     * Sinon, crée un événement en draft, une réservation en draft et un token de confirmation,
     * puis déclenche l'envoi du mail de confirmation.
     *
     * @param request les données de la demande initiale (nom, email, prestataireId)
     * @return l'email de l'utilisateur encapsulé dans la réponse
     */
    public DemandeInitialeResponse createDemandeReservation(DemandeInitialeRequest request) {

        if (utilisateurService.existsByEmail(request.email())) {
            onboardingMailerService.sendSecurityAlertEmail(request.email(), request.prestataireId());
            return new DemandeInitialeResponse(request.email());
        }

        confirmationTokenService.cancelExistingTokenForEmail(request.email());

        Evenement evenement = evenementService.createDraft(
                request.firstName(),
                request.lastName(),
                request.email()
        );

        Reservation reservation = reservationService.createDraft(
                evenement,
                request.prestataireId()
        );

        String jwt = confirmationTokenService.createForReservation(reservation);

        onboardingMailerService.sendConfirmationEmail(request.email(), jwt);

        return new DemandeInitialeResponse(request.email());
    }

    /**
     * Valide le JWT set-password, crée le compte Keycloak, crée l'utilisateur en base,
     * active la réservation, supprime le token de confirmation,
     * puis récupère et retourne les tokens Keycloak pour connecter le front immédiatement.
     *
     * @param request le JWT set-password et le mot de passe choisi par l'utilisateur
     * @return les tokens d'accès Keycloak (access token et refresh token)
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
        } catch (JwtException ex) {
            throw new InvalidTokenException();
        }

        UUID reservationId = UUID.fromString(claims.get("reservationId", String.class));
        String email = claims.getSubject();

        Evenement evenement = reservationService.getEvenement(reservationId);
        String firstName = evenement.getFirstName();
        String lastName = evenement.getLastName();

        keycloakAdminService.createUser(email, firstName, lastName, request.password());
        utilisateurService.createUtilisateur(firstName, lastName, email);
        reservationService.activateDemande(reservationId);
        confirmationTokenService.deleteByReservation(reservationId);
        onboardingMailerService.sendWelcomeEmail(email);

        KeycloakTokenResponse tokens = keycloakAdminService.getUserTokens(email, request.password());
        return new ConfirmAccountResponse(tokens.accessToken(), tokens.refreshToken());
    }
}
