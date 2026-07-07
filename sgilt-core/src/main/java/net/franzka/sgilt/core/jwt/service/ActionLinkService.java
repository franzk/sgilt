package net.franzka.sgilt.core.jwt.service;

import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.jwt.domain.ActionType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Crée un lien d'action complet : persiste le token d'action en base, puis construit l'URL front
 * correspondante ({@code frontPath} du type + token signé). Ne produit aucun effet de bord externe
 * (pas d'envoi de mail) — sûr à appeler à l'intérieur d'une transaction DB.
 */
@Service
@RequiredArgsConstructor
public class ActionLinkService {

    private final ActionTokenService actionTokenService;

    @Value("${sgilt.frontend.url}")
    private String frontendUrl;

    /**
     * Crée le token d'action et construit l'URL front à laquelle il pointe.
     * L'URL ne porte que le token signé — {@code action} n'y figure pas : ce n'est pas couvert par
     * la signature HMAC, donc falsifiable sans invalider le token. Le front la recevra plus tard,
     * dérivée côté serveur du {@code type} en base au moment de la vérification (étape 4).
     *
     * @param type    le type d'action (discriminant + description du flow front)
     * @param payload les données propres au flow (ex. une map contenant l'email du destinataire)
     * @return l'URL front complète (avec le token signé), prête à être transmise (mail, étape 3)
     */
    public String createLink(ActionType type, Object payload) {
        ActionTokenService.TokenCreationResult result = actionTokenService.createToken(type, payload);
        return frontendUrl + type.frontPath()
                + "?token=" + URLEncoder.encode(result.hmacToken(), StandardCharsets.UTF_8);
    }
}
