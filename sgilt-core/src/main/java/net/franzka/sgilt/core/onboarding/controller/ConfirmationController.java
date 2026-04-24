package net.franzka.sgilt.core.onboarding.controller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.onboarding.api.ConfirmationApi;
import net.franzka.sgilt.core.onboarding.domain.Onboarding;
import net.franzka.sgilt.core.onboarding.dto.SetPasswordTokenDto;
import net.franzka.sgilt.core.onboarding.service.ConfirmationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller pour la confirmation de la demande initiale par email.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class ConfirmationController implements ConfirmationApi {

    private final ConfirmationService confirmationService;

    /**
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
        Onboarding onboarding = confirmationService.validateConfirmationToken(token);
        return ResponseEntity.ok(confirmationService.generateSetPasswordResponse(onboarding));
    }
}
