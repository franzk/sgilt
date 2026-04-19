package net.franzka.sgilt.core.keycloak;

import net.franzka.sgilt.core.config.KeycloakProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;

import java.util.List;

/**
 * Service métier pour les interactions avec l'API Keycloak.
 */
@Service
public class KeycloakAdminService {

    private final KeycloakTokenClient keycloakTokenClient;
    private final KeycloakAdminClient keycloakAdminClient;
    private final KeycloakProperties keycloakProperties;

    /**
     * Construit le service avec ses dépendances.
     *
     * @param keycloakTokenClient le client HTTP pour le token endpoint Keycloak
     * @param keycloakAdminClient le client HTTP pour l'API Admin Keycloak
     * @param keycloakProperties  les propriétés de configuration Keycloak
     */
    public KeycloakAdminService(
            KeycloakTokenClient keycloakTokenClient,
            KeycloakAdminClient keycloakAdminClient,
            KeycloakProperties keycloakProperties) {
        this.keycloakTokenClient = keycloakTokenClient;
        this.keycloakAdminClient = keycloakAdminClient;
        this.keycloakProperties = keycloakProperties;
    }

    /**
     * Crée un utilisateur dans Keycloak avec le mot de passe fourni.
     * Obtient d'abord un token admin via le grant {@code client_credentials},
     * puis appelle l'API Admin pour créer l'utilisateur avec {@code enabled: true}
     * et {@code emailVerified: true}.
     *
     * @param email     l'adresse email (également utilisée comme username)
     * @param firstName le prénom de l'utilisateur
     * @param lastName  le nom de l'utilisateur
     * @param password  le mot de passe en clair
     * @throws KeycloakException en cas d'erreur lors de l'appel Keycloak
     */
    public void createUser(String email, String firstName, String lastName, String password) {
        try {
            KeycloakTokenResponse adminToken = fetchAdminToken();

            KeycloakUserRequest userRequest = new KeycloakUserRequest(
                    email,
                    email,
                    firstName,
                    lastName,
                    true,
                    true,
                    List.of(new KeycloakCredential("password", password, false))
            );

            keycloakAdminClient.createUser(
                    keycloakProperties.realm(),
                    "Bearer " + adminToken.accessToken(),
                    userRequest
            );
        } catch (RestClientException e) {
            throw new KeycloakException("Erreur lors de la création de l'utilisateur dans Keycloak.", e);
        }
    }

    /**
     * Récupère les tokens Keycloak d'un utilisateur via le grant {@code password}.
     *
     * @param email    l'adresse email de l'utilisateur
     * @param password le mot de passe en clair
     * @return les tokens Keycloak (access token et refresh token)
     * @throws KeycloakException en cas d'erreur lors de l'appel Keycloak
     */
    public KeycloakTokenResponse getUserTokens(String email, String password) {
        try {
            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("grant_type", "password");
            form.add("client_id", keycloakProperties.frontClientId());
            form.add("username", email);
            form.add("password", password);
            return keycloakTokenClient.fetchToken(keycloakProperties.realm(), form);
        } catch (RestClientException e) {
            throw new KeycloakException("Erreur lors de la récupération des tokens utilisateur dans Keycloak.", e);
        }
    }

    /**
     * Obtient un token admin via le grant {@code client_credentials}.
     *
     * @return le token admin Keycloak
     */
    private KeycloakTokenResponse fetchAdminToken() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", keycloakProperties.adminClientId());
        form.add("client_secret", keycloakProperties.adminClientSecret());
        return keycloakTokenClient.fetchToken(keycloakProperties.realm(), form);
    }
}
