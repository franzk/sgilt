package net.franzka.sgilt.core.keycloak;

import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * Client HTTP déclaratif pour le token endpoint de Keycloak.
 * Instancié comme bean Spring via {@link KeycloakConfig}.
 */
@HttpExchange
public interface KeycloakTokenClient {

    /**
     * Appelle le token endpoint Keycloak avec les paramètres de formulaire fournis.
     * Supporte les grants {@code client_credentials} et {@code password}.
     *
     * @param realm    le realm Keycloak cible
     * @param formData les paramètres de formulaire (grant_type, client_id, etc.)
     * @return la réponse contenant l'access token et le refresh token
     */
    @PostExchange(
            url = "/realms/{realm}/protocol/openid-connect/token",
            contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    KeycloakTokenResponse fetchToken(
            @PathVariable String realm,
            @RequestBody MultiValueMap<String, String> formData
    );
}
