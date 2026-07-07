package net.franzka.sgilt.core.onboarding.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.service.EvenementService;
import net.franzka.sgilt.core.jwt.domain.ActionToken;
import net.franzka.sgilt.core.jwt.service.ActionTokenService;
import net.franzka.sgilt.core.jwt.service.TokenJwtService;
import net.franzka.sgilt.core.keycloak.KeycloakAdminService;
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
    private final ActionTokenService actionTokenService;

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
            onboardingMailerService.sendSecurityAlertEmail(request.email());
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
     * Etape finale du process d'onboarding : confirmation du compte, quel que soit le flow
     * (client ou prestataire). Décode le JWT set-password une seule fois, puis dispatche vers
     * le traitement du bon flow selon le claim présent ({@code actionTokenId} ou {@code onboardingId}).
     *
     * @param request le JWT set-password et le mot de passe choisi
     * @return les tokens d'accès Keycloak
     * @throws TokenExpiredException si le JWT set-password est expiré
     * @throws InvalidTokenException si le JWT est invalide
     */
    public ConfirmAccountResponse confirmOnboarding(ConfirmAccountRequest request) {
        String token = request.setPasswordToken();

        // 1. le token est-il encore valide ?
        // isExpired/extractClaims vérifient aussi la signature (Jwts.parser().verifyWith(key)...) :
        // un token forgé ou altéré lève une JwtException ici, pas seulement s'il est expiré.
        if (setPasswordTokenJwtService.isExpired(token)) {
            log.info("confirmAccount — token expiré pour token: {}", token);
            throw new TokenExpiredException();
        }

        // 2. extraction des claims
        Claims claims;
        try {
            claims = setPasswordTokenJwtService.extractClaims(token);
        } catch (JwtException _) {
            throw new InvalidTokenException();
        }

        // 3. est-on dans le flow d'onboarding d'un prestataire ?
        String actionTokenId = claims.get("actionTokenId", String.class);
        if (actionTokenId != null) {
            return confirmPrestataireOnboarding(UUID.fromString(actionTokenId), request.password());
        }

        // 4. sinon, onboarding d'un client
        return confirmAccount(claims, request.password());
    }

    /**
     * Confirme l'onboarding d'un client : consomme la session d'onboarding, crée le compte
     * Keycloak, crée l'utilisateur, l'événement et la réservation, puis retourne les tokens Keycloak.
     *
     * @param claims   les claims du JWT set-password, déjà décodé par {@link #confirmOnboarding}
     * @param password le mot de passe choisi par le client
     * @return les tokens d'accès Keycloak
     */
    private ConfirmAccountResponse confirmAccount(Claims claims, String password) {
        UUID onboardingId = UUID.fromString(claims.get("onboardingId", String.class));
        String email = claims.getSubject();

        Onboarding onboarding = onboardingSessionService.findById(onboardingId);
        OnboardingSessionService.OnboardingContent content = onboardingSessionService.consume(onboarding);

        InitOnboardingRequest formData = content.formData();
        Prestataire prestataire = content.prestataire();

        log.info("confirmAccount — création compte Keycloak pour {}", email);
        keycloakAdminService.createClientUser(email, formData.firstName(), formData.lastName(), password);

        // création de l'utilisateur, de la réservation et de l'événement
        UUID eventId = onboardingSessionService.createEntities(formData, prestataire, email);

        log.info("confirmAccount — compte créé, envoi mail bienvenue à {}", email);
        onboardingMailerService.sendWelcomeEmail(email);

        String loginUrl = keycloakAdminService.getMagicLoginUrl(email, "/app/events/" + eventId);
        log.info("confirmAccount — session SSO créée pour {}", email);
        return new ConfirmAccountResponse(loginUrl);
    }

    /**
     * Confirme l'onboarding d'un prestataire :
     * - définit son mot de passe sur le compte Keycloak déjà existant
     * - consomme le token d'action seulement une fois le mot de passe posé avec succès,
     * - puis ouvre une session SSO vers la fiche éditable.
     *
     * @param actionTokenId l'identifiant du token d'action extrait du JWT set-password
     * @param password      le mot de passe choisi par le prestataire
     * @return l'URL de connexion SSO vers l'espace pro
     */
    private ConfirmAccountResponse confirmPrestataireOnboarding(UUID actionTokenId, String password) {
        ActionToken actionToken = actionTokenService.findById(actionTokenId);
        String email = (String) actionTokenService.readPayload(actionToken).get("email");

        log.info("confirmAccount — définition du mot de passe Keycloak pour {}", email);
        String userId = keycloakAdminService.getUserIdByEmail(email);
        keycloakAdminService.resetPassword(userId, password);

        actionTokenService.consume(actionToken);

        String loginUrl = keycloakAdminService.getMagicLoginUrl(email, "/pro/page-edition");
        log.info("confirmAccount — session SSO créée pour {}", email);
        return new ConfirmAccountResponse(loginUrl);
    }
}
