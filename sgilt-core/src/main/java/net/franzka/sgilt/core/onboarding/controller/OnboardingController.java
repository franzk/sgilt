package net.franzka.sgilt.core.onboarding.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.onboarding.api.OnboardingApi;
import net.franzka.sgilt.core.onboarding.dto.*;
import net.franzka.sgilt.core.onboarding.service.VerifyService;
import net.franzka.sgilt.core.onboarding.service.OnboardingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller HTTP de l'onboarding : un visiteur crée un événement et, au passage, son compte.
 * <li> Ouverture de la session à l'envoi de l'événement (construit par le visiteur, ou né d'une
 * demande à un prestataire)</li>
 * <li> Vérification de l'email, puis confirmation finale : création du compte, de l'événement et
 * de ses réservations</li>
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class OnboardingController implements OnboardingApi {

    private final OnboardingService onboardingService;
    private final VerifyService verifyService;

    /**
     * ETAPE 1 : initialisation de l'onboarding à partir d'un événement complet.
     * Stocke l'événement et les coordonnées, et envoie le mail de vérification.
     *
     * @param request les coordonnées et l'événement complet
     * @return 202 Accepted avec l'email en réponse
     */
    @Override
    @Transactional
    public ResponseEntity<InitOnboardingResponse> initOnboarding(
            @RequestBody @Valid InitOnboardingRequest request
    ) {
        log.info("POST /onboarding — email={}", request.email());
        return ResponseEntity.accepted().body(onboardingService.initOnboardingSession(request));
    }

    /**
     * ETAPE 1 bis : initialisation de l'onboarding par demande unique (fiche d'un prestataire).
     * Le back construit l'événement complet, le stocke et envoie le mail de vérification.
     *
     * @param request les champs du tunnel de demande
     * @return 202 Accepted avec l'email en réponse
     */
    @Override
    @Transactional
    public ResponseEntity<InitOnboardingResponse> initOnboardingDemande(
            @RequestBody @Valid InitOnboardingDemandeRequest request
    ) {
        log.info("POST /onboarding/demande — email={} prestataireId={}", request.email(), request.prestataireId());
        return ResponseEntity.accepted().body(onboardingService.initOnboardingDemande(request));
    }

    /**
     * ETAPE 2 : vérification de l'email du client, ou du token d'action prestataire.
     * Effectue les actions en rapport avec le flow (onboarding client ou prestataire)
     * et retourne un JWT set-password valide 5 minutes.
     *
     * @param token le token de confirmation extrait du lien email
     * @return 200 OK avec l'email et le JWT set-password
     */
    @Override
    @Transactional
    public ResponseEntity<SetPasswordTokenDto> verifyToken(String token) {
        log.info("GET /confirmation — token présent: {}", token != null && !token.isBlank());
        return ResponseEntity.ok(verifyService.verify(token));
    }

    /**
     * ETAPE 3 : confirmation du compte.
     * - valide le JWT set-password,
     * - crée l'utilisateur dans Keycloak
     * - crée l'utilisateur, l'événement et ses réservations dans la base de données
     * - envoie le mail de bienvenue
     * - renvoie les tokens Keycloak pour que le front puisse être immédiatement connecté
     *
     * @param request le JWT set-password et le mot de passe choisi
     * @return 200 OK avec les tokens d'accès Keycloak (mockés temporairement)
     */
    @Override
    @Transactional
    public ResponseEntity<ConfirmAccountResponse> confirmAccount(@RequestBody @Valid ConfirmAccountRequest request) {
        log.info("POST /onboarding/confirm-account");
        return ResponseEntity.ok(onboardingService.confirmOnboarding(request));
    }
}
