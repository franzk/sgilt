package net.franzka.sgilt.core.onboarding.service;

import net.franzka.sgilt.core.jwt.TokenJwtService;
import net.franzka.sgilt.core.onboarding.domain.Onboarding;
import net.franzka.sgilt.core.onboarding.dto.SetPasswordTokenDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service pour la confirmation de la demande initiale par email.
 */
@Service
public class VerifyService {

    private final OnboardingSessionService onboardingSessionService;
    private final TokenJwtService setPasswordTokenJwtService;

    /**
     * Construit le service avec ses dépendances.
     *
     * @param onboardingSessionService   le service métier des sessions d'onboarding
     * @param setPasswordTokenJwtService le service JWT qualifié pour les tokens set-password
     */
    public VerifyService(
            OnboardingSessionService onboardingSessionService,
            @Qualifier("setPasswordTokenJwtService") TokenJwtService setPasswordTokenJwtService) {
        this.onboardingSessionService = onboardingSessionService;
        this.setPasswordTokenJwtService = setPasswordTokenJwtService;
    }

    /**
     * Vérifie le token HMAC reçu par email et fait progresser la session vers PENDING_CONFIRMATION.
     *
     * @param token le token {@code payload-signature} reçu par email
     * @return la session d'onboarding validée
     */
    public Onboarding validateVerificationToken(String token) {
        Onboarding onboarding = onboardingSessionService.checkToken(token);
        onboardingSessionService.advanceToConfirmation(onboarding);
        return onboarding;
    }

    /**
     * Génère un JWT set-password à partir de la session d'onboarding validée.
     *
     * @param onboarding la session d'onboarding validée
     * @return l'email et le JWT set-password
     */
    public SetPasswordTokenDto generateSetPasswordResponse(Onboarding onboarding) {
        Map<String, Object> claims = Map.of("onboardingId", onboarding.getId().toString());
        String setPasswordToken = setPasswordTokenJwtService.generateToken(claims, onboarding.getEmail());
        return new SetPasswordTokenDto(onboarding.getEmail(), setPasswordToken);
    }
}
