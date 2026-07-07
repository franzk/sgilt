package net.franzka.sgilt.core.onboarding.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.jwt.domain.ActionToken;
import net.franzka.sgilt.core.jwt.service.ActionTokenService;
import net.franzka.sgilt.core.jwt.service.TokenJwtService;
import net.franzka.sgilt.core.onboarding.domain.Onboarding;
import net.franzka.sgilt.core.onboarding.dto.SetPasswordTokenDto;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service pour la confirmation de la demande initiale par email.
 * Vérifie le token reçu par email et génère un JWT set-password court terme — que le token
 * appartienne au flux client ({@link Onboarding}) ou au flux prestataire ({@link ActionToken}) :
 * essaie d'abord le flux client, retombe sur le flux prestataire si le token n'y correspond pas.
 * Pont temporaire en attendant une vraie unification des deux mécanismes de token.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VerifyService {

    private final OnboardingSessionService onboardingSessionService;
    private final ActionTokenService actionTokenService;
    private final TokenJwtService setPasswordTokenJwtService;


    /**
     * Vérifie le token reçu par email et génère la réponse set-password correspondante.
     * Essaie d'abord le flux client (session {@link Onboarding}) ; si le token ne correspond à
     * aucune session, retombe sur le flux prestataire ({@link ActionToken}).
     *
     * @param token le token {@code payload-signature} reçu par email
     * @return l'email et le JWT set-password
     */
    public SetPasswordTokenDto verify(String token) {
        try {
            return verifyClientOnboarding(token);
        } catch (EntityNotFoundException _) {
            try {
                return verifyPrestataireOnboarding(token);
            } catch (EntityNotFoundException e) {
                log.warn("verify — token inconnu des deux flows (client et prestataire)");
                throw e;
            }
        }
    }

    private SetPasswordTokenDto verifyClientOnboarding(String token) {
        Onboarding onboarding = onboardingSessionService.checkToken(token);
        onboardingSessionService.advanceToConfirmation(onboarding);

        Map<String, Object> claims = Map.of("onboardingId", onboarding.getId().toString());
        String setPasswordToken = setPasswordTokenJwtService.generateToken(claims, onboarding.getEmail());
        return new SetPasswordTokenDto(onboarding.getEmail(), setPasswordToken);
    }

    private SetPasswordTokenDto verifyPrestataireOnboarding(String token) {
        ActionToken actionToken = actionTokenService.checkToken(token);
        String email = (String) actionTokenService.readPayload(actionToken).get("email");

        Map<String, Object> claims = Map.of("actionTokenId", actionToken.getId().toString());
        String setPasswordToken = setPasswordTokenJwtService.generateToken(claims, email);
        return new SetPasswordTokenDto(email, setPasswordToken);
    }
}
