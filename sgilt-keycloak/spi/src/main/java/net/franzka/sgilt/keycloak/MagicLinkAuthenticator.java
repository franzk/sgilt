package net.franzka.sgilt.keycloak;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Authenticator KC qui valide un token magique one-shot.
 * Si le paramètre {@code magic_token} est absent, l'authenticator passe la main
 * à l'étape suivante du flow (username/password standard).
 */
class MagicLinkAuthenticator implements Authenticator {

    static final String TOKEN_PARAM = "magic_token";

    private final String secret;

    MagicLinkAuthenticator(String secret) {
        this.secret = secret;
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        String token = context.getUriInfo().getQueryParameters().getFirst(TOKEN_PARAM);

        if (token == null || token.isBlank()) {
            context.attempted();
            return;
        }

        if (secret == null || secret.isBlank()) {
            context.failure(AuthenticationFlowError.INTERNAL_ERROR);
            return;
        }

        try {
            String email = validateAndExtractEmail(token);

            UserModel user = context.getSession().users()
                    .getUserByEmail(context.getRealm(), email);

            if (user == null || !user.isEnabled()) {
                context.failure(AuthenticationFlowError.INVALID_USER);
                return;
            }

            context.setUser(user);
            context.success();

        } catch (SecurityException e) {
            context.failure(AuthenticationFlowError.INVALID_CREDENTIALS);
        } catch (Exception e) {
            context.failure(AuthenticationFlowError.INTERNAL_ERROR);
        }
    }

    /**
     * Valide la signature HMAC-SHA256 du token JWT et retourne l'email du sujet.
     *
     * @param token le JWT au format {@code header.payload.signature} (base64url)
     * @return l'email extrait du claim {@code sub}
     * @throws SecurityException si la signature est invalide ou le token expiré
     */
    private String validateAndExtractEmail(String token) throws Exception {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new SecurityException("Format de token invalide");
        }

        String dataToVerify = parts[0] + "." + parts[1];
        String expectedSig = base64UrlEncode(hmacSha256(dataToVerify, secret));

        if (!expectedSig.equals(parts[2])) {
            throw new SecurityException("Signature du token invalide");
        }

        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);

        String email = extractJsonField(payloadJson, "sub");
        String expStr = extractJsonField(payloadJson, "exp");

        if (email == null || expStr == null) {
            throw new SecurityException("Claims manquants dans le token");
        }

        long exp = Long.parseLong(expStr.trim());
        if (System.currentTimeMillis() / 1000L > exp) {
            throw new SecurityException("Token expiré");
        }

        return email;
    }

    private byte[] hmacSha256(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

    private String base64UrlEncode(byte[] data) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }

    /** Extraction minimaliste d'un champ depuis un JSON plat (sans dépendance Jackson). */
    private String extractJsonField(String json, String field) {
        String key = "\"" + field + "\"";
        int idx = json.indexOf(key);
        if (idx == -1) return null;
        int colon = json.indexOf(':', idx + key.length());
        if (colon == -1) return null;
        int start = colon + 1;
        while (start < json.length() && json.charAt(start) == ' ') start++;
        if (start >= json.length()) return null;
        if (json.charAt(start) == '"') {
            int end = json.indexOf('"', start + 1);
            return end == -1 ? null : json.substring(start + 1, end);
        }
        int end = start;
        while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}') end++;
        return json.substring(start, end).trim();
    }

    @Override
    public void action(AuthenticationFlowContext context) {}

    @Override
    public boolean requiresUser() { return false; }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) { return true; }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {}

    @Override
    public void close() {}
}
