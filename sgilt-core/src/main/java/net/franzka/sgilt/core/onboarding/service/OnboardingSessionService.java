package net.franzka.sgilt.core.onboarding.service;

import lombok.AllArgsConstructor;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.config.ConfirmationTokenProperties;
import net.franzka.sgilt.core.jwt.VerificationTokenHmacService;
import net.franzka.sgilt.core.onboarding.domain.Onboarding;
import net.franzka.sgilt.core.onboarding.domain.OnboardingState;
import net.franzka.sgilt.core.onboarding.dto.InitOnboardingRequest;
import net.franzka.sgilt.core.onboarding.exception.TokenAlreadyUsedException;
import net.franzka.sgilt.core.onboarding.exception.TokenExpiredException;
import net.franzka.sgilt.core.onboarding.repository.OnboardingRepository;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service métier pour l'entité {@link Onboarding}.
 * Gère le cycle de vie d'une session d'onboarding : initiation, validation du lien email, consommation.
 */
@Service
@Slf4j
@AllArgsConstructor
public class OnboardingSessionService {

    private final OnboardingRepository onboardingRepository;
    private final VerificationTokenHmacService verificationTokenHmacService;
    private final ConfirmationTokenProperties confirmationTokenProperties;
    private final ObjectMapper objectMapper;

    /**
     * Données désérialisées d'une session d'onboarding consommée.
     *
     * @param formData    les données saisies dans le tunnel
     * @param prestataire le prestataire ciblé par la demande
     */
    public record OnboardingContent(InitOnboardingRequest formData, Prestataire prestataire) {}

    /**
     * Résultat de la création d'une session : l'entité persistée et le token HMAC à envoyer par email.
     *
     * @param onboarding l'entité persistée
     * @param hmacToken  le token {@code payload-signature} à inclure dans le lien de confirmation
     */
    public record InitiationResult(Onboarding onboarding, String hmacToken) {}

    /**
     * Crée et persiste une session d'onboarding en sérialisant les données du tunnel.
     *
     * @param email       l'adresse email du demandeur
     * @param prestataire le prestataire ciblé
     * @param request     les données saisies dans le tunnel
     * @return le résultat contenant la session persistée et le token HMAC complet
     */
    public InitiationResult initiate(String email, Prestataire prestataire, InitOnboardingRequest request) {
        try {
            String hmacToken = verificationTokenHmacService.generateToken();
            String payload = hmacToken.substring(0, hmacToken.lastIndexOf("-"));

            Onboarding onboarding = Onboarding.builder()
                    .hmacPayload(payload)
                    .email(email)
                    .expiresAt(LocalDateTime.now().plusHours(
                            confirmationTokenProperties.confirmationExpirationHours()))
                    .prestataire(prestataire)
                    .data(objectMapper.writeValueAsString(request))
                    .build();

            onboardingRepository.save(onboarding);
            return new InitiationResult(onboarding, hmacToken);
        } catch (JacksonException e) {
            throw new RuntimeException("Échec de la sérialisation des données du tunnel", e);
        }
    }

    /**
     * Vérifie le token HMAC et charge la session correspondante.
     * Contrôle l'état (USED/CANCELLED → exception) et l'expiration (OPEN → TokenExpiredException,
     * PENDING_CONFIRMATION → vérification de la période de grâce).
     * Ne modifie pas l'état de la session.
     *
     * @param token le token {@code payload-signature} reçu par email
     * @return la session d'onboarding chargée et vérifiée
     * @throws net.franzka.sgilt.core.onboarding.exception.InvalidTokenException si la signature HMAC est invalide
     * @throws EntityNotFoundException   si aucune session ne correspond au payload
     * @throws TokenAlreadyUsedException si la session a déjà été consommée ou annulée
     * @throws TokenExpiredException     si la session ou la période de grâce est expirée
     */
    public Onboarding checkToken(String token) {
        String hmacPayload = verificationTokenHmacService.verify(token);

        Onboarding onboarding = onboardingRepository.findByHmacPayload(hmacPayload)
                .orElseThrow(EntityNotFoundException::new);

        OnboardingState state = onboarding.getState();

        if (OnboardingState.USED.equals(state) || OnboardingState.CANCELLED.equals(state)) {
            throw new TokenAlreadyUsedException();
        }
        if (OnboardingState.OPEN.equals(state) && !onboarding.getExpiresAt().isAfter(LocalDateTime.now())) {
            throw new TokenExpiredException();
        }
        if (OnboardingState.PENDING_CONFIRMATION.equals(state)
                && onboarding.getConfirmationPeriodExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException();
        }

        return onboarding;
    }

    /**
     * Fait progresser une session OPEN vers PENDING_CONFIRMATION avec une période de grâce de 5 minutes.
     * Sans effet si la session est déjà en PENDING_CONFIRMATION (idempotent).
     *
     * @param onboarding la session à faire progresser.
     */
    public void advanceToConfirmation(Onboarding onboarding) {
        if (OnboardingState.OPEN.equals(onboarding.getState())) {
            onboarding.setState(OnboardingState.PENDING_CONFIRMATION);
            onboarding.setConfirmationPeriodExpiresAt(LocalDateTime.now().plusMinutes(5));
            onboardingRepository.save(onboarding);
        }
    }

    /**
     * Annule les sessions d'onboarding en état OPEN associées à l'email donné.
     *
     * @param email l'adresse email dont on cherche les sessions OPEN à annuler
     */
    public void cancelExistingForEmail(String email) {
        onboardingRepository.findByEmailAndState(email, OnboardingState.OPEN)
                .forEach(o -> {
                    o.setState(OnboardingState.CANCELLED);
                    onboardingRepository.save(o);
                });
    }

    /**
     * Charge une session d'onboarding par son identifiant.
     *
     * @param id l'identifiant de la session
     * @return la session correspondante
     * @throws EntityNotFoundException si aucune session ne correspond à cet identifiant
     */
    public Onboarding findById(UUID id) {
        return onboardingRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    /**
     * Désérialise les données du tunnel, charge le prestataire, puis supprime la session.
     * Doit être appelé dans un contexte transactionnel.
     *
     * @param onboarding la session à consommer
     * @return les données du formulaire et le prestataire ciblé
     */
    public OnboardingContent consume(Onboarding onboarding) {
        try {
            InitOnboardingRequest formData = objectMapper.readValue(onboarding.getData(), InitOnboardingRequest.class);
            Prestataire prestataire = onboarding.getPrestataire();
            onboardingRepository.delete(onboarding);
            return new OnboardingContent(formData, prestataire);
        } catch (JacksonException e) {
            throw new RuntimeException("Échec de la désérialisation des données du tunnel", e);
        }
    }
}
