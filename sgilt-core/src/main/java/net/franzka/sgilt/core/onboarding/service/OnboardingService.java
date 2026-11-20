package net.franzka.sgilt.core.onboarding.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.evenement.dto.CreateEventRequest;
import net.franzka.sgilt.core.evenement.dto.DemandeInitieeDto;
import net.franzka.sgilt.core.evenement.dto.EvenementDto;
import net.franzka.sgilt.core.evenement.service.EvenementService;
import net.franzka.sgilt.core.jwt.domain.ActionToken;
import net.franzka.sgilt.core.jwt.service.ActionTokenService;
import net.franzka.sgilt.core.jwt.service.TokenJwtService;
import net.franzka.sgilt.core.keycloak.KeycloakAdminService;
import net.franzka.sgilt.core.onboarding.domain.Onboarding;
import net.franzka.sgilt.core.onboarding.dto.ConfirmAccountRequest;
import net.franzka.sgilt.core.onboarding.dto.ConfirmAccountResponse;
import net.franzka.sgilt.core.onboarding.dto.InitOnboardingDemandeRequest;
import net.franzka.sgilt.core.onboarding.dto.InitOnboardingRequest;
import net.franzka.sgilt.core.onboarding.dto.InitOnboardingResponse;
import net.franzka.sgilt.core.onboarding.exception.InvalidTokenException;
import net.franzka.sgilt.core.onboarding.exception.TokenExpiredException;
import net.franzka.sgilt.core.onboarding.mailer.OnboardingMailerService;
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

    private final PrestataireService prestataireService;
    private final OnboardingSessionService onboardingSessionService;
    private final TokenJwtService setPasswordTokenJwtService;
    private final OnboardingMailerService onboardingMailerService;
    private final UtilisateurService utilisateurService;
    private final KeycloakAdminService keycloakAdminService;
    private final ActionTokenService actionTokenService;
    private final EvenementService evenementService;
    private final ReservationService reservationService;

    /**
     * Un visiteur passe par la fiche d'un prestataire : c'est aussi la création d'un événement,
     * construit ici (rubriques du template du type, demande rangée dans la sienne), puis la
     * session est ouverte.
     *
     * @param request les champs du tunnel de demande
     * @return l'email encapsulé dans la réponse
     */
    public InitOnboardingResponse initOnboardingDemande(InitOnboardingDemandeRequest request) {
        if (utilisateurService.existsByEmail(request.email())) {
            alertExistingAccount(request.email());
            // Réponse identique à celle d'un nouvel email : l'appelant ne doit pas pouvoir déduire de la
            // réponse qu'un compte existe pour cet email (énumération de comptes). Seul le propriétaire
            // de l'adresse fait la différence, par le mail qu'il reçoit.
            return new InitOnboardingResponse(request.email());
        }

        // Vérification de la validité du prestataire
        prestataireService.ensurePublished(request.prestataireId()); // lève PrestataireNotFoundException si le prestataire n'existe pas ou n'est pas publié

        // L'événement est construit comme pour une création connectée, mais il n'est enregistré qu'à
        // l'activation du compte.
        EvenementDto evenement = evenementService.buildEvenement(new CreateEventRequest(
                request.title(), request.eventType(), request.ambiance(), request.momentCle(), request.description(),
                request.date(), request.ville(), request.nbInvites(), request.lieu(),
                new DemandeInitieeDto(request.prestataireId(), request.prestataireMessage())));
        return startSession(new InitOnboardingRequest(
                request.firstName(), request.lastName(), request.email(), request.telephone(), evenement));
    }

    // Email déjà associé à un compte : prévient son propriétaire par une alerte de sécurité.
    private void alertExistingAccount(String email) {
        log.info("initOnboarding — email déjà connu, envoi alerte sécurité : {}", email);
        onboardingMailerService.sendSecurityAlertEmail(email);
    }

    // Annule les sessions OPEN de l'email, crée la nouvelle session et envoie le mail de vérification.
    private InitOnboardingResponse startSession(InitOnboardingRequest request) {
        log.info("initOnboarding — nouvel email, ouverture de la session de création d'événement : {}", request.email());
        onboardingSessionService.cancelExistingForEmail(request.email());

        OnboardingSessionService.InitiationResult result =
                onboardingSessionService.initiate(request.email(), request);

        log.info("initOnboarding — session {} créée, envoi mail confirmation à {}",
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

        // 3. est-on dans un flow à jeton d'action (onboarding prestataire, invitation client) ?
        String actionTokenId = claims.get("actionTokenId", String.class);
        if (actionTokenId != null) {
            return confirmActionTokenOnboarding(UUID.fromString(actionTokenId), request.password());
        }

        // 4. sinon, onboarding d'un client
        return confirmAccount(claims, request.password());
    }

    /**
     * Confirme l'onboarding d'un client : consomme la session d'onboarding, crée le compte
     * Keycloak, crée l'utilisateur et son événement (avec ses réservations), puis retourne les tokens Keycloak.
     *
     * @param claims   les claims du JWT set-password, déjà décodé par {@link #confirmOnboarding}
     * @param password le mot de passe choisi par le client
     * @return les tokens d'accès Keycloak
     */
    private ConfirmAccountResponse confirmAccount(Claims claims, String password) {
        UUID onboardingId = UUID.fromString(claims.get("onboardingId", String.class));
        String email = claims.getSubject();

        Onboarding onboarding = onboardingSessionService.findById(onboardingId);
        InitOnboardingRequest formData = onboardingSessionService.consume(onboarding);

        log.info("confirmAccount — création compte Keycloak pour {}", email);
        keycloakAdminService.createClientUser(email, formData.firstName(), formData.lastName(), password);

        // création de l'utilisateur, de l'événement et des réservations initiées
        UUID eventId = onboardingSessionService.createEntities(formData, email);

        log.info("confirmAccount — compte créé, envoi mail bienvenue à {}", email);
        onboardingMailerService.sendWelcomeEmail(email);

        String loginUrl = keycloakAdminService.getMagicLoginUrl(email, "/app/events/" + eventId);
        log.info("confirmAccount — session SSO créée pour {}", email);
        return new ConfirmAccountResponse(loginUrl);
    }

    /**
     * Confirme un onboarding porté par un token d'action, dont le compte Keycloak existe déjà
     * (créé sans mot de passe) :
     * - définit son mot de passe sur le compte Keycloak,
     * - termine le flow propre au type du token : rien de plus pour un prestataire ; activation du
     *   compte pour un client invité depuis le BO,
     * - consomme le token d'action seulement une fois tout cela fait avec succès,
     * - puis ouvre une session SSO vers la page d'arrivée du flow (fiche éditable pour un
     *   prestataire, sa réservation pour un client invité).
     *
     * @param actionTokenId l'identifiant du token d'action extrait du JWT set-password
     * @param password      le mot de passe choisi
     * @return l'URL de connexion SSO vers la page d'arrivée
     */
    private ConfirmAccountResponse confirmActionTokenOnboarding(UUID actionTokenId, String password) {
        ActionToken actionToken = actionTokenService.findById(actionTokenId);
        String email = (String) actionTokenService.readPayload(actionToken).get("email");

        log.info("confirmAccount — définition du mot de passe Keycloak pour {}", email);
        String userId = keycloakAdminService.getUserIdByEmail(email);
        keycloakAdminService.resetPassword(userId, password);

        String landingPath = switch (actionToken.getType()) {
            case PRESTATAIRE_ONBOARDING -> "/pro/page-edition";
            case CLIENT_INVITATION      -> invitedReservationPath(activateClientInvitation(email));
        };

        actionTokenService.consume(actionToken);

        String loginUrl = keycloakAdminService.getMagicLoginUrl(email, landingPath);
        log.info("confirmAccount — session SSO créée pour {}", email);
        return new ConfirmAccountResponse(loginUrl);
    }

    // Active le compte d'un client invité depuis le BO : INVITED → ACTIVE, et acceptation des CGU
    // (cochées sur la page d'activation).
    private Utilisateur activateClientInvitation(String email) {
        Utilisateur utilisateur = utilisateurService.activateInvitedUtilisateur(email);
        utilisateurService.acceptCgu(utilisateur);
        log.info("confirmAccount — compte du client invité {} activé", email);
        return utilisateur;
    }

    // Arrivée d'un client invité : directement sa réservation (un seul événement portant une seule
    // réservation, par construction), dont le retour mène exceptionnellement à l'event board
    // (?from=invitation), qu'il découvre.
    private String invitedReservationPath(Utilisateur utilisateur) {
        UUID eventId = evenementService.getUserEvents(utilisateur.getId()).getFirst().id();
        UUID reservationId = reservationService.getReservationSummaries(eventId).getFirst().id();
        return "/app/events/" + eventId + "/reservations/" + reservationId + "?from=invitation";
    }
}
