package net.franzka.sgilt.integration;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Client HTTP minimal (JDK {@link HttpClient}, sans dépendance JSON) pour récupérer de vrais JWT
 * auprès de Keycloak. Les users de test eux-mêmes sont pré-déclarés dans le realm import
 * ({@code keycloak/realm-test.json}) plutôt que créés via l'API Admin — ce sont des fixtures fixes,
 * pas un flow métier à exercer, donc pas besoin de la reconstruire côté test.
 */
final class KeycloakTestClient {

    private static final Pattern ACCESS_TOKEN_PATTERN = Pattern.compile("\"access_token\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern EMAIL_CLAIM_PATTERN = Pattern.compile("\"email\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern GIVEN_NAME_CLAIM_PATTERN = Pattern.compile("\"given_name\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern FAMILY_NAME_CLAIM_PATTERN = Pattern.compile("\"family_name\"\\s*:\\s*\"([^\"]+)\"");

    /**
     * Identité extraite d'un JWT (claims {@code email}/{@code given_name}/{@code family_name}, scope
     * {@code profile} standard) — évite de dupliquer prénom/nom/email en Java alors qu'ils sont déjà
     * la source de vérité dans {@code keycloak/realm-test.json}.
     */
    record TokenIdentity(String email, String firstName, String lastName) {}

    private final String authServerUrl;
    private final String realm;
    private final HttpClient http = HttpClient.newHttpClient();

    KeycloakTestClient(String authServerUrl, String realm) {
        this.authServerUrl = authServerUrl;
        this.realm = realm;
    }

    /** Récupère un JWT réel pour un user existant via le grant {@code password} (client {@code sgilt-front}). */
    String fetchUserToken(String email, String password) {
        String form = "grant_type=password&client_id=sgilt-front&username=" + email + "&password=" + password;
        HttpResponse<String> response = send(HttpRequest.newBuilder()
                .uri(URI.create(authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(BodyPublishers.ofString(form))
                .build());
        return extract(ACCESS_TOKEN_PATTERN, response.body());
    }

    /** Décode (sans vérifier la signature — usage test uniquement) le payload d'un JWT. */
    static TokenIdentity decodeIdentity(String jwt) {
        String payloadSegment = jwt.split("\\.")[1];
        String padded = payloadSegment + "=".repeat((4 - payloadSegment.length() % 4) % 4);
        String payload = new String(Base64.getUrlDecoder().decode(padded), StandardCharsets.UTF_8);
        return new TokenIdentity(
                extract(EMAIL_CLAIM_PATTERN, payload),
                extract(GIVEN_NAME_CLAIM_PATTERN, payload),
                extract(FAMILY_NAME_CLAIM_PATTERN, payload));
    }

    private HttpResponse<String> send(HttpRequest request) {
        try {
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 300) {
                throw new IllegalStateException("Keycloak call failed [%d] %s: %s"
                        .formatted(response.statusCode(), request.uri(), response.body()));
            }
            return response;
        } catch (Exception e) {
            throw new IllegalStateException("Keycloak call failed: " + request.uri(), e);
        }
    }

    private static String extract(Pattern pattern, String body) {
        Matcher matcher = pattern.matcher(body);
        if (!matcher.find()) {
            throw new IllegalStateException("Pattern " + pattern + " not found in: " + body);
        }
        return matcher.group(1);
    }
}
