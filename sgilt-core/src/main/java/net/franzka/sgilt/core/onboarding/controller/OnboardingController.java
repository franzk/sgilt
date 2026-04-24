package net.franzka.sgilt.core.onboarding.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.onboarding.api.OnboardingApi;
import net.franzka.sgilt.core.onboarding.domain.Onboarding;
import net.franzka.sgilt.core.onboarding.dto.*;
import net.franzka.sgilt.core.onboarding.service.VerifyService;
import net.franzka.sgilt.core.onboarding.service.OnboardingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller HTTP du process d'onboarding :
 * <li> Prise en charge de la demande initiale (après soumission du formulaire de demande initiale)</li>
 * <li> Confirmation finale et création du compte client</li>
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class OnboardingController implements OnboardingApi {

    private final OnboardingService onboardingService;
    private final VerifyService verifyService;

    /**
     * ETAPE 1 : initialisation de l'onboarding.
     * Traite la demande initiale du tunnel :
     * stocke les données envoyées par l'utilisateur
     * et envoie le mail de vérification.
     *
     * @param request les données de la demande de réservation
     * @return 202 Accepted avec l'email en réponse
     */
    @Override
    @Transactional
    public ResponseEntity<InitOnboardingResponse> initOnboarding(
            @RequestBody @Valid InitOnboardingRequest request
    ) {
        log.info("POST /onboarding — email={} prestataireId={}", request.email(), request.prestataireId());
        return ResponseEntity.accepted().body(onboardingService.initOnboardingSession(request));
    }

    /**
     * ETAPE 2 : vérification de l'email du client.
     * Vérifie le token de confirmation reçu par email, fait passer la session en PENDING_CONFIRMATION
     * et retourne un JWT set-password valide 5 minutes.
     *
     * @param token le token de confirmation extrait du lien email
     * @return 200 OK avec l'email et le JWT set-password
     */
    @Override
    @Transactional
    public ResponseEntity<SetPasswordTokenDto> verifyToken(String token) {
        log.info("GET /confirmation — token présent: {}", token != null && !token.isBlank());
        Onboarding onboarding = verifyService.validateVerificationToken(token);
        return ResponseEntity.ok(verifyService.generateSetPasswordResponse(onboarding));
    }

    /**
     * ETAPE 3 : confirmation du compte.
     * - valide le JWT set-password,
     * - crée l'utilisateur dans Keycloak
     * - crée l'utilisateur, l'événement, la réservation dans la base de données
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
        return ResponseEntity.ok(onboardingService.confirmAccount(request));
    }
}
