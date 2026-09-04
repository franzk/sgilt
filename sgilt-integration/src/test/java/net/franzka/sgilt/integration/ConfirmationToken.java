package net.franzka.sgilt.integration;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

/**
 * Reconstruit le token de confirmation {@code payload-signature} envoyé par email lors de
 * l'onboarding, à partir du {@code hmac_payload} lu en base ({@link Fixtures#getOnboardingHmacPayloadByEmail}).
 * Même algorithme que {@code VerificationTokenHmacService} côté sgilt-core (HMAC-SHA256 hex du
 * payload, secret partagé — {@code sgilt.jwt.confirmation-secret}, configuré identique des deux
 * côtés dans {@link IntegrationTestContext}). Évite d'avoir à capturer l'email réellement envoyé
 * (pas de MailHog dans ce module) : le payload est en base, la signature est déterministe.
 */
final class ConfirmationToken {

    private ConfirmationToken() {}

    static String build(String payload, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signature = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return payload + "-" + HexFormat.of().formatHex(signature);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to build confirmation token", e);
        }
    }
}
