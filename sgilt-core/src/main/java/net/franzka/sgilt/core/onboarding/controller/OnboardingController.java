package net.franzka.sgilt.core.onboarding.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.onboarding.api.OnboardingApi;
import net.franzka.sgilt.core.onboarding.dto.ConfirmAccountRequest;
import net.franzka.sgilt.core.onboarding.dto.ConfirmAccountResponse;
import net.franzka.sgilt.core.onboarding.dto.DemandeInitialeRequest;
import net.franzka.sgilt.core.onboarding.dto.DemandeInitialeResponse;
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

    /**
     * Traite la demande initiale du tunnel : crée l'événement, la réservation et envoie le mail de confirmation.
     *
     * @param request les données de la demande (nom, email, prestataireId)
     * @return 202 Accepted avec l'email en réponse
     */
    @Override
    @Transactional
    public ResponseEntity<DemandeInitialeResponse> submitDemandeInitiale(
            @RequestBody @Valid DemandeInitialeRequest request
    ) {
        log.info("POST /onboarding — email={} prestataireId={}", request.email(), request.prestataireId());
        return ResponseEntity.accepted().body(onboardingService.createDemandeReservation(request));
    }

    /**
     * Confirme la création du compte :
     * - valide le JWT set-password,
     * - crée l'utilisateur dans Keycloak (TODO),
     * - passe la réservation en NOUVELLE
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
