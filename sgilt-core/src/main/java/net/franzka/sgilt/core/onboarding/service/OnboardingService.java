package net.franzka.sgilt.core.onboarding.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.service.EvenementService;
import net.franzka.sgilt.core.jwt.TokenJwtService;
import net.franzka.sgilt.core.keycloak.KeycloakAdminService;
import net.franzka.sgilt.core.keycloak.KeycloakTokenResponse;
import net.franzka.sgilt.core.onboarding.domain.Onboarding;
import net.franzka.sgilt.core.onboarding.dto.ConfirmAccountRequest;
import net.franzka.sgilt.core.onboarding.dto.ConfirmAccountResponse;
import net.franzka.sgilt.core.onboarding.dto.InitOnboardingRequest;
import net.franzka.sgilt.core.onboarding.dto.InitOnboardingResponse;
import net.franzka.sgilt.core.onboarding.exception.InvalidTokenException;
import net.franzka.sgilt.core.onboarding.exception.TokenExpiredException;
import net.franzka.sgilt.core.onboarding.mailer.OnboardingMailerService;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.prestataire.service.PrestataireService;
import net.franzka.sgilt.core.reservation.service.ReservationService;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import net.franzka.sgilt.core.utilisateur.service.UtilisateurService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class OnboardingService {

    private final EvenementService evenementService;
    private final ReservationService reservationService;
    private final PrestataireService prestataireService;
    private final OnboardingSessionService onboardingSessionService;
    private final TokenJwtService setPasswordTokenJwtService;
    private final OnboardingMailerService onboardingMailerService;
    private final UtilisateurService utilisateurService;
    private final KeycloakAdminService keycloakAdminService;

    /**
     * Traite une demande initiale de réservation.
     * Si l'email est déjà associé à un compte existant, envoie une alerte de sécurité et retourne.
     * Sinon, annule les sessions OPEN existantes, crée une nouvelle session d'onboarding
     * et envoie le mail de vérification.
     *
     * @param request les données saisies dans le tunnel
     * @return l'email encapsulé dans la réponse
     */
    public InitOnboardingResponse initOnboardingSession(InitOnboardingRequest request) {

        if (utilisateurService.existsByEmail(request.email())) {
            log.info("createDemandeReservation — email déjà connu, envoi alerte sécurité : {}", request.email());
            onboardingMailerService.sendSecurityAlertEmail(request.email(), request.prestataireId());
            return new InitOnboardingResponse(request.email());
        }

        log.info("createDemandeReservation — nouvel email, création de la session : {}", request.email());
        onboardingSessionService.cancelExistingForEmail(request.email());

        Prestataire prestataire = prestataireService.getById(request.prestataireId());
        OnboardingSessionService.InitiationResult result =
                onboardingSessionService.initiate(request.email(), prestataire, request);

        log.info("createDemandeReservation — session {} créée, envoi mail confirmation à {}",
                result.onboarding().getId(), request.email());
        onboardingMailerService.sendVerificationEmail(request.email(), result.hmacToken());

        return new InitOnboardingResponse(request.email());
    }

    /**
     * Etape finale du process d'onboarding : confirmation du compte.
     * Valide le JWT set-password, consomme la session d'onboarding,
     * crée le compte Keycloak,
     * crée l'utilisateur, l'événement et la réservation,
     * puis retourne les tokens Keycloak.
     *
     * @param request le JWT set-password et le mot de passe choisi
     * @return les tokens d'accès Keycloak
     * @throws TokenExpiredException si le JWT set-password est expiré
     * @throws InvalidTokenException si le JWT est invalide
     */
    public ConfirmAccountResponse confirmAccount(ConfirmAccountRequest request) {
        String token = request.setPasswordToken();

        if (setPasswordTokenJwtService.isExpired(token)) {
            log.info("confirmAccount — token expiré pour token: {}", token);
            throw new TokenExpiredException();
        }

        Claims claims;
        try {
            claims = setPasswordTokenJwtService.extractClaims(token);
        } catch (JwtException ex) {
            throw new InvalidTokenException();
        }

        UUID onboardingId = UUID.fromString(claims.get("onboardingId", String.class));
        String email = claims.getSubject();

        Onboarding onboarding = onboardingSessionService.findById(onboardingId);
        OnboardingSessionService.OnboardingContent content = onboardingSessionService.consume(onboarding);
        InitOnboardingRequest formData = content.formData();
        Prestataire prestataire = content.prestataire();

        log.info("confirmAccount — création compte Keycloak pour {}", email);
        keycloakAdminService.createUser(email, formData.firstName(), formData.lastName(), request.password());

        // création de l'utilisateur, de la réservation et de l'événement
        Utilisateur utilisateur = utilisateurService.createUtilisateur(
                formData.firstName(), formData.lastName(), email, formData.telephone());
        Evenement evenement = evenementService.create(utilisateur, formData.eventType(), formData.date());
        reservationService.create(evenement, prestataire, utilisateur, formData.date());

        log.info("confirmAccount — compte créé, envoi mail bienvenue à {}", email);
        onboardingMailerService.sendWelcomeEmail(email);

        KeycloakTokenResponse tokens = keycloakAdminService.getUserTokens(email, request.password());
        log.info("confirmAccount — tokens Keycloak récupérés pour {}", email);
        return new ConfirmAccountResponse(tokens.accessToken(), tokens.refreshToken());
    }
}
