package net.franzka.sgilt.core.jwt.service;

import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.config.ConfirmationTokenProperties;
import net.franzka.sgilt.core.jwt.domain.ActionToken;
import net.franzka.sgilt.core.jwt.domain.ActionType;
import net.franzka.sgilt.core.jwt.repository.ActionTokenRepository;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

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
                    .expiresAt(LocalDateTime.now().plusHours(confirmationTokenProperties.confirmationExpirationHours()))
                    .build();

            return new TokenCreationResult(actionTokenRepository.save(token), generated.fullToken());
        } catch (JacksonException e) {
            throw new IllegalStateException("Échec de sérialisation du payload du token d'action", e);
        }
    }
}
