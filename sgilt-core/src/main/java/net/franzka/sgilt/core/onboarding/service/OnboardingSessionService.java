package net.franzka.sgilt.core.onboarding.service;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.config.ConfirmationTokenProperties;
import net.franzka.sgilt.core.jwt.ConfirmationTokenHmacService;
import net.franzka.sgilt.core.onboarding.domain.Onboarding;
import net.franzka.sgilt.core.onboarding.domain.OnboardingState;
import net.franzka.sgilt.core.onboarding.dto.DemandeInitialeRequest;
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
public class OnboardingSessionService {

    private final OnboardingRepository onboardingRepository;
    private final ConfirmationTokenHmacService confirmationTokenHmacService;
    private final ConfirmationTokenProperties confirmationTokenProperties;
    private final ObjectMapper objectMapper;

    /**
     * Données désérialisées d'une session d'onboarding consommée.
     *
     * @param formData    les données saisies dans le tunnel
     * @param prestataire le prestataire ciblé par la demande
     */
    public record OnboardingContent(DemandeInitialeRequest formData, Prestataire prestataire) {}

    /**
     * Résultat de la création d'une session : l'entité persistée et le token HMAC à envoyer par email.
     *
     * @param onboarding l'entité persistée
     * @param hmacToken  le token {@code payload-signature} à inclure dans le lien de confirmation
     */
    public record InitiationResult(Onboarding onboarding, String hmacToken) {}

    /**
     * Construit le service avec ses dépendances.
     *
     * @param onboardingRepository          le repository des sessions d'onboarding
     * @param confirmationTokenHmacService  le service HMAC de génération et vérification des tokens
     * @param confirmationTokenProperties   les propriétés de configuration (durée de validité)
     * @param objectMapper                  le mapper Jackson pour la sérialisation des données du tunnel
     */
    public OnboardingSessionService(
            OnboardingRepository onboardingRepository,
            ConfirmationTokenHmacService confirmationTokenHmacService,
            ConfirmationTokenProperties confirmationTokenProperties,
            ObjectMapper objectMapper) {
        this.onboardingRepository = onboardingRepository;
        this.confirmationTokenHmacService = confirmationTokenHmacService;
        this.confirmationTokenProperties = confirmationTokenProperties;
        this.objectMapper = objectMapper;
    }

    /**
     * Crée et persiste une session d'onboarding en sérialisant les données du tunnel.
     *
     * @param email       l'adresse email du demandeur
     * @param prestataire le prestataire ciblé
     * @param request     les données saisies dans le tunnel
     * @return le résultat contenant la session persistée et le token HMAC complet
     */
    public InitiationResult initiate(String email, Prestataire prestataire, DemandeInitialeRequest request) {
        try {
            String hmacToken = confirmationTokenHmacService.generateToken();
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
     * Valide le token HMAC reçu par email et fait progresser l'état de la session.
     * <ul>
     *   <li>OPEN : vérifie l'expiration, passe en PENDING_CONFIRMATION avec une grâce de 5 minutes.</li>
     *   <li>PENDING_CONFIRMATION : vérifie que la période de grâce n'est pas dépassée.</li>
     *   <li>USED ou CANCELLED : lève {@link TokenAlreadyUsedException}.</li>
     * </ul>
     *
     * @param token le token {@code payload-signature} reçu par email
     * @return la session d'onboarding validée
     * @throws net.franzka.sgilt.core.onboarding.exception.InvalidTokenException si la signature HMAC est invalide
     * @throws EntityNotFoundException   si aucune session ne correspond au payload
     * @throws TokenAlreadyUsedException si la session a déjà été consommée ou annulée
     * @throws TokenExpiredException     si la session ou la période de grâce est expirée
     */
    public Onboarding validate(String token) {
        String hmacPayload = confirmationTokenHmacService.verify(token);

        Onboarding onboarding = onboardingRepository.findByHmacPayload(hmacPayload)
                .orElseThrow(EntityNotFoundException::new);

        OnboardingState state = onboarding.getState();

        if (OnboardingState.USED.equals(state) || OnboardingState.CANCELLED.equals(state)) {
            throw new TokenAlreadyUsedException();
        }

        if (OnboardingState.OPEN.equals(state)) {
            if (!onboarding.getExpiresAt().isAfter(LocalDateTime.now())) {
                throw new TokenExpiredException();
            }
            onboarding.setState(OnboardingState.PENDING_CONFIRMATION);
            onboarding.setConfirmationPeriodExpiresAt(LocalDateTime.now().plusMinutes(5));
            onboardingRepository.save(onboarding);
        }

        if (OnboardingState.PENDING_CONFIRMATION.equals(state)
                && onboarding.getConfirmationPeriodExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException();
        }

        return onboarding;
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
            DemandeInitialeRequest formData = objectMapper.readValue(onboarding.getData(), DemandeInitialeRequest.class);
            Prestataire prestataire = onboarding.getPrestataire();
            onboardingRepository.delete(onboarding);
            return new OnboardingContent(formData, prestataire);
        } catch (JacksonException e) {
            throw new RuntimeException("Échec de la désérialisation des données du tunnel", e);
        }
    }
}
