package net.franzka.sgilt.core.keycloak;

import net.franzka.sgilt.core.config.KeycloakProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

/**
 * Service métier pour les interactions avec l'API Keycloak.
 */
@Service
public class KeycloakAdminService {

    private static final long MAGIC_TOKEN_TTL_SECONDS = 300L; // 5 minutes

    private final KeycloakTokenClient keycloakTokenClient;
    private final KeycloakAdminClient keycloakAdminClient;
    private final KeycloakProperties keycloakProperties;

    @Value("${sgilt.frontend.url:http://localhost:3000}")
    private String frontendUrl;

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
     * Crée un compte client dans Keycloak et lui assigne le rôle {@code USER}.
     *
     * @param email     l'adresse email (également utilisée comme username)
     * @param firstName le prénom de l'utilisateur
     * @param lastName  le nom de l'utilisateur
     * @param password  le mot de passe en clair
     * @throws KeycloakException en cas d'erreur lors des appels Keycloak
     */
    public void createClientUser(String email, String firstName, String lastName, String password) {
        try {
            KeycloakTokenResponse adminToken = fetchAdminToken();
            String auth = "Bearer " + adminToken.accessToken();

            var response = keycloakAdminClient.createUser(
                    keycloakProperties.realm(), auth,
                    new KeycloakUserRequest(email, email, firstName, lastName, true, true,
                            List.of(new KeycloakCredential("password", password, false)))
            );

            String location = response.getHeaders().getFirst("Location");
            if (location == null) {
                throw new KeycloakException("Header Location absent après création du user KC.");
            }
            String userId = location.substring(location.lastIndexOf('/') + 1);

            KeycloakRoleRepresentation userRole = keycloakAdminClient.getRole(
                    keycloakProperties.realm(), auth, "USER"
            );
            keycloakAdminClient.assignRoles(
                    keycloakProperties.realm(), auth, userId, List.of(userRole)
            );
        } catch (RestClientException e) {
            throw new KeycloakException("Erreur lors de la création du compte client dans Keycloak.", e);
        }
    }

    /**
     * Génère une URL de connexion KC contenant un token magique one-shot signé HMAC-SHA256.
     * Le SPI KC {@code magic-link} valide ce token et crée une vraie session SSO sans
     * que l'utilisateur ait à ressaisir son mot de passe.
     *
     * @param email l'email de l'utilisateur qui vient d'être créé
     * @return l'URL d'autorisation KC à transmettre au front
     * @throws KeycloakException si la génération du token échoue
     */
    public String getMagicLoginUrl(String email, String redirectPath) {
        try {
            String magicToken = buildMagicToken(email);
            String encodedRedirectUri = URLEncoder.encode(frontendUrl + redirectPath, StandardCharsets.UTF_8);
            String encodedToken      = URLEncoder.encode(magicToken, StandardCharsets.UTF_8);

            return keycloakProperties.adminUrl()
                    + "/realms/" + keycloakProperties.realm()
                    + "/protocol/openid-connect/auth"
                    + "?client_id=" + keycloakProperties.frontClientId()
                    + "&redirect_uri=" + encodedRedirectUri
                    + "&response_type=code"
                    + "&scope=openid"
                    + "&magic_token=" + encodedToken;
        } catch (Exception e) {
            throw new KeycloakException("Erreur lors de la génération du magic token.", e);
        }
    }

    /**
     * Construit un JWT minimal signé HMAC-SHA256 compatible avec le SPI KC {@code magic-link}.
     *
     * @param email le sujet du token
     * @return le JWT au format {@code header.payload.signature} (base64url, sans padding)
     */
    private String buildMagicToken(String email) throws Exception {
        long now = System.currentTimeMillis() / 1000L;
        long exp = now + MAGIC_TOKEN_TTL_SECONDS;

        String header  = b64url("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
        String payload = b64url("{\"sub\":\"" + email + "\",\"iat\":" + now + ",\"exp\":" + exp + "}");
        String sig     = b64url(hmacSha256(header + "." + payload, keycloakProperties.magicLinkSecret()));

        return header + "." + payload + "." + sig;
    }

    private String b64url(String value) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private byte[] hmacSha256(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

    private String b64url(byte[] data) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
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
