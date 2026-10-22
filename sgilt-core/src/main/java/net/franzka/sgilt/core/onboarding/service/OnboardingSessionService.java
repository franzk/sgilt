package net.franzka.sgilt.core.onboarding.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.config.ConfirmationTokenProperties;
import net.franzka.sgilt.core.evenement.service.EvenementService;
import net.franzka.sgilt.core.jwt.service.VerificationTokenHmacService;
import net.franzka.sgilt.core.onboarding.domain.Onboarding;
import net.franzka.sgilt.core.onboarding.domain.OnboardingState;
import net.franzka.sgilt.core.onboarding.dto.InitOnboardingRequest;
import net.franzka.sgilt.core.onboarding.exception.TokenAlreadyUsedException;
import net.franzka.sgilt.core.onboarding.exception.TokenExpiredException;
import net.franzka.sgilt.core.onboarding.repository.OnboardingRepository;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import net.franzka.sgilt.core.utilisateur.service.UtilisateurService;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service métier pour l'entité {@link Onboarding}.
 * Gère le cycle de vie d'une session d'onboarding (un visiteur crée un événement et son compte) :
 * initiation, validation du lien email, consommation et création de l'utilisateur et de l'événement.
 */
@Service
@Slf4j
@AllArgsConstructor
public class OnboardingSessionService {

    private final OnboardingRepository onboardingRepository;
    private final VerificationTokenHmacService verificationTokenHmacService;
    private final ConfirmationTokenProperties confirmationTokenProperties;
    private final UtilisateurService utilisateurService;
    private final EvenementService evenementService;
    private final ObjectMapper objectMapper;

    /**
     * Résultat de la création d'une session : l'entité persistée et le token HMAC à envoyer par email.
     *
     * @param onboarding l'entité persistée
     * @param hmacToken  le token {@code payload-signature} à inclure dans le lien de confirmation
     */
    public record InitiationResult(Onboarding onboarding, String hmacToken) {}

    /**
     * Session d'onboarding en attente, avec l'événement qu'elle porte (suivi admin).
     *
     * @param onboarding la session
     * @param content    les coordonnées et l'événement complet
     */
    public record PendingOnboarding(Onboarding onboarding, InitOnboardingRequest content) {}

    /**
     * Crée et persiste une session d'onboarding en sérialisant l'événement et les coordonnées.
     *
     * @param email   l'adresse email du visiteur
     * @param request l'événement complet et les coordonnées
     * @return le résultat contenant la session persistée et le token HMAC complet
     */
    public InitiationResult initiate(String email, InitOnboardingRequest request) {
        try {
            VerificationTokenHmacService.GeneratedToken generated = verificationTokenHmacService.generate();

            Onboarding onboarding = Onboarding.builder()
                    .hmacPayload(generated.payload())
                    .email(email)
                    .expiresAt(LocalDateTime.now().plusHours(
                            confirmationTokenProperties.confirmationExpirationHours()))
                    .data(objectMapper.writeValueAsString(request))
                    .build();

            onboardingRepository.save(onboarding);
            return new InitiationResult(onboarding, generated.fullToken());
        } catch (JacksonException e) {
            throw new RuntimeException("Échec de la sérialisation de l'événement de l'onboarding", e);
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
                && !onboarding.getExpiresAt().isAfter(LocalDateTime.now())) {
            throw new TokenExpiredException();
        }

        return onboarding;
    }

    /**
     * Fait progresser une session OPEN vers PENDING_CONFIRMATION.
     * Sans effet si la session est déjà en PENDING_CONFIRMATION (idempotent).
     * La session reste valide jusqu'à son expiration originale ({@code expiresAt}).
     *
     * @param onboarding la session à faire progresser.
     */
    public void advanceToConfirmation(Onboarding onboarding) {
        if (OnboardingState.OPEN.equals(onboarding.getState())) {
            onboarding.setState(OnboardingState.PENDING_CONFIRMATION);
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
     * Retourne les sessions d'onboarding en attente (OPEN ou PENDING_CONFIRMATION), avec leur
     * événement, triées par date de création décroissante — pour le suivi admin.
     *
     * @return les sessions en attente
     */
    public List<PendingOnboarding> listPending() {
        return onboardingRepository.findByStateInOrderByCreatedAtDesc(
                        List.of(OnboardingState.OPEN, OnboardingState.PENDING_CONFIRMATION)).stream()
                .map(onboarding -> new PendingOnboarding(onboarding, readContent(onboarding)))
                .toList();
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
     * Désérialise l'événement et les coordonnées de la session, puis la supprime.
     * Doit être appelé dans un contexte transactionnel.
     *
     * @param onboarding la session à consommer
     * @return les coordonnées et l'événement complet
     */
    public InitOnboardingRequest consume(Onboarding onboarding) {
        InitOnboardingRequest request = readContent(onboarding);
        onboardingRepository.delete(onboarding);
        return request;
    }

    /** Désérialise l'événement et les coordonnées stockés dans la session. */
    private InitOnboardingRequest readContent(Onboarding onboarding) {
        try {
            return objectMapper.readValue(onboarding.getData(), InitOnboardingRequest.class);
        } catch (JacksonException e) {
            throw new RuntimeException("Échec de la désérialisation de l'événement de l'onboarding", e);
        }
    }

    /**
     * Matérialise l'onboarding : crée l'utilisateur, puis l'événement avec ses rubriques et une
     * réservation par demande initiée.
     *
     * @param request les coordonnées et l'événement complet
     * @param email    l'adresse email du visiteur, utilisée pour créer l'utilisateur
     * @return l'identifiant de l'événement créé
     */
    public UUID createEntities(InitOnboardingRequest request, String email) {
        Utilisateur utilisateur = utilisateurService.createUtilisateur(
                request.firstName(), request.lastName(), email, request.telephone());

        // enregistrement de l'acceptation des CGU et de la politique de confidentialité
        utilisateurService.acceptCgu(utilisateur);

        return evenementService.createFromDto(utilisateur, request.evenement()).getId();
    }
}
