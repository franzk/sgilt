package net.franzka.sgilt.core.jwt.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.config.ConfirmationTokenProperties;
import net.franzka.sgilt.core.jwt.domain.ActionToken;
import net.franzka.sgilt.core.jwt.domain.ActionType;
import net.franzka.sgilt.core.jwt.repository.ActionTokenRepository;
import net.franzka.sgilt.core.onboarding.exception.TokenExpiredException;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service métier pour l'entité {@link ActionToken}.
 */
@Service
@RequiredArgsConstructor
public class ActionTokenService {

    private final ActionTokenRepository actionTokenRepository;
    private final VerificationTokenHmacService verificationTokenHmacService;
    private final ConfirmationTokenProperties confirmationTokenProperties;
    private final ObjectMapper objectMapper;

    /**
     * Résultat de la création d'un token d'action : l'entité persistée et le token HMAC complet
     * généré au moment de la création — évite de le reconstruire (recalcul de la signature) juste
     * après coup.
     *
     * @param token     l'entité persistée
     * @param hmacToken le token {@code payload-signature} complet à inclure dans l'URL d'action
     */
    public record TokenCreationResult(ActionToken token, String hmacToken) {}

    /**
     * Crée et persiste un token d'action pour le flow donné.
     * {@code payload} est propre à chaque flow (ex. une map contenant l'email du destinataire) —
     * le mécanisme générique ne présuppose rien sur sa forme, seulement qu'il est sérialisable en JSON.
     * Seul le payload HMAC du token est stocké en base ; le token complet généré ici est retourné
     * pour que l'appelant n'ait pas à le reconstruire.
     *
     * @param type    le type d'action (discriminant du flow)
     * @param payload les données propres au flow, sérialisées en jsonb
     * @return l'entité persistée et le token HMAC complet
     * @throws IllegalStateException si le payload ne peut pas être sérialisé en JSON
     */
    public TokenCreationResult createToken(ActionType type, Object payload) {
        try {
            // 1. génération d'un token HMAC (payload aléatoire + signature)
            VerificationTokenHmacService.GeneratedToken generated = verificationTokenHmacService.generate();

            ActionToken token = ActionToken.builder()
                    .hmacPayload(generated.payload())
                    .type(type)
                    .payload(objectMapper.writeValueAsString(payload))
                    .expiresAt(LocalDateTime.now().plusHours(confirmationTokenProperties.prestataireOnboardingExpirationHours()))
                    .build();

            return new TokenCreationResult(actionTokenRepository.save(token), generated.fullToken());
        } catch (JacksonException e) {
            throw new IllegalStateException("Échec de sérialisation du payload du token d'action", e);
        }
    }

    /**
     * Vérifie le token HMAC et charge le token d'action correspondant.
     * Ne modifie/ne consomme rien.
     *
     * @param token le token {@code payload-signature} reçu par email
     * @return le token d'action chargé et vérifié
     * @throws net.franzka.sgilt.core.onboarding.exception.InvalidTokenException si la signature HMAC est invalide
     * @throws EntityNotFoundException si aucun token ne correspond au payload
     * @throws TokenExpiredException   si le token est expiré
     */
    public ActionToken checkToken(String token) {
        String hmacPayload = verificationTokenHmacService.verify(token);

        ActionToken actionToken = actionTokenRepository.findByHmacPayload(hmacPayload)
                .orElseThrow(EntityNotFoundException::new);

        if (!actionToken.getExpiresAt().isAfter(LocalDateTime.now())) {
            throw new TokenExpiredException();
        }

        return actionToken;
    }

    /**
     * Charge un token d'action par son identifiant.
     *
     * @param id l'identifiant du token
     * @return le token correspondant
     * @throws EntityNotFoundException si aucun token ne correspond à cet identifiant
     */
    public ActionToken findById(UUID id) {
        return actionTokenRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    /**
     * Supprime le token d'action, le marquant comme consommé.
     * À n'appeler qu'après le succès de l'action que le token protégeait.
     *
     * @param actionToken le token à consommer
     */
    public void consume(ActionToken actionToken) {
        actionTokenRepository.delete(actionToken);
    }

    /**
     * Liste tous les tokens d'action en attente pour un flow donné, quel que soit leur état
     * d'expiration. Utilisé par le back-office pour afficher les onboardings dont le lien n'a
     * pas encore été cliqué.
     *
     * @param type le type d'action recherché
     * @return les tokens en attente pour ce type
     */
    public List<ActionToken> findAllByType(ActionType type) {
        return actionTokenRepository.findByType(type);
    }

    /**
     * Charge le token d'action en attente correspondant à l'email donné, pour un flow donné.
     * Le payload étant un jsonb libre propre à chaque flow, la recherche filtre en mémoire les
     * tokens du type demandé — acceptable ici car le volume de tokens en attente reste faible
     * (provisionnement manuel admin, un seul type de flow aujourd'hui).
     *
     * @param type  le type d'action recherché
     * @param email l'email du destinataire, tel que stocké dans le payload à la création
     * @return le token correspondant
     * @throws EntityNotFoundException si aucun token en attente ne correspond à cet email
     */
    public ActionToken findPendingByEmail(ActionType type, String email) {
        return findAllByType(type).stream()
                .filter(token -> email.equals(readPayload(token).get("email")))
                .findFirst()
                .orElseThrow(EntityNotFoundException::new);
    }

    /**
     * Réinitialise la période de validité d'un token d'action existant à la durée de validité
     * courante du flow prestataire. Le token lui-même n'est pas régénéré : le lien déjà transmis
     * au destinataire reste valide, seule sa date d'expiration change.
     *
     * @param actionToken le token dont la validité doit être réinitialisée
     * @return le token complet ({@code payload-signature}) à inclure dans le lien renvoyé
     */
    public String renewExpiration(ActionToken actionToken) {
        actionToken.setExpiresAt(
                LocalDateTime.now().plusHours(confirmationTokenProperties.prestataireOnboardingExpirationHours()));
        actionTokenRepository.save(actionToken);
        return verificationTokenHmacService.buildToken(actionToken.getHmacPayload());
    }

    /**
     * Désérialise le payload jsonb du token en une map générique.
     * Le service ne connaît pas la forme du payload — à l'appelant d'en interpréter les clés
     * (chaque flow décide de ce qu'il y met à la création).
     *
     * @param actionToken le token dont on désérialise le payload
     * @return le payload désérialisé
     */
    public Map<String, Object> readPayload(ActionToken actionToken) {
        return objectMapper.readValue(actionToken.getPayload(), Map.class);
    }
}
