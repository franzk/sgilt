package net.franzka.sgilt.core.onboarding.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.onboarding.domain.ConfirmationToken;
import net.franzka.sgilt.core.onboarding.api.OnboardingApi;
import net.franzka.sgilt.core.onboarding.dto.ConfirmAccountRequest;
import net.franzka.sgilt.core.onboarding.dto.ConfirmAccountResponse;
import net.franzka.sgilt.core.onboarding.dto.DemandeInitialeResponse;
import net.franzka.sgilt.core.onboarding.dto.NewEvenementRequest;
import net.franzka.sgilt.core.onboarding.dto.VerifyTokenResponse;
import net.franzka.sgilt.core.onboarding.service.OnboardingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller HTTP du process d'onboarding.
 */
@RestController
@RequiredArgsConstructor
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
    public ResponseEntity<DemandeInitialeResponse> submitDemandeTunnel(
            @RequestBody @Valid NewEvenementRequest request
    ) {
        return ResponseEntity.accepted().body(
                onboardingService.submitDemandeTunnel(request)
        );
    }

    /**
     * Vérifie le token de confirmation reçu par email, le marque comme utilisé
     * et retourne un JWT set-password valide 5 minutes.
     *
     * @param token le JWT de confirmation extrait du lien email
     * @return 200 OK avec l'email et le JWT set-password
     */
    @Override
    @Transactional
    public ResponseEntity<VerifyTokenResponse> verifyToken(
            @RequestParam String token
    ) {
        ConfirmationToken confirmationToken = onboardingService.validateConfirmationToken(token);
        onboardingService.consumeConfirmationToken(confirmationToken);
        return ResponseEntity.ok(onboardingService.generateSetPasswordResponse(confirmationToken));
    }

    /**
     * Confirme la création du compte : valide le JWT set-password,
     * crée l'utilisateur dans Keycloak (TODO),
     * passe la réservation en NOUVELLE
     * et envoie le mail de bienvenue.
     *
     * @param request le JWT set-password et le mot de passe choisi
     * @return 200 OK avec les tokens d'accès Keycloak (mockés)
     */
    @Override
    @Transactional
    public ResponseEntity<ConfirmAccountResponse> confirmAccount(
            @RequestBody @Valid ConfirmAccountRequest request
    ) {
        return ResponseEntity.ok(onboardingService.confirmAccount(request));
    }
}
