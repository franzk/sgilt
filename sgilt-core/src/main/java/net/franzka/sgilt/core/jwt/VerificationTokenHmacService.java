package net.franzka.sgilt.core.jwt;

import net.franzka.sgilt.core.config.ConfirmationTokenProperties;
import net.franzka.sgilt.core.onboarding.exception.InvalidTokenException;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Service de génération et vérification des tokens de confirmation par HMAC.
 * Le token produit est de la forme {@code payload-signature} où :
 * <ul>
 *   <li>{@code payload} est 16 octets aléatoires encodés en Base64 URL-safe sans padding</li>
 *   <li>{@code signature} est le HMAC-SHA256 du payload encodé en hexadécimal</li>
 * </ul>
 * Le secret utilisé pour le calcul HMAC provient de {@link ConfirmationTokenProperties#confirmationSecret()}.
 */
@Service
public class VerificationTokenHmacService {

    private final ConfirmationTokenProperties confirmationTokenProperties;
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Construit le service avec ses dépendances.
     *
     * @param confirmationTokenProperties les propriétés contenant le secret HMAC
     */
    public VerificationTokenHmacService(ConfirmationTokenProperties confirmationTokenProperties) {
        this.confirmationTokenProperties = confirmationTokenProperties;
    }

    /**
     * Génère un payload aléatoire de 16 octets encodé en Base64 URL-safe sans padding.
     *
     * @return le payload sous forme de chaîne Base64 URL-safe
     */
    public String generatePayload() {
        byte[] bytes = new byte[16];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Construit un token de confirmation à partir d'un payload existant.
     * Calcule la signature HMAC-SHA256 du payload et retourne le token complet.
     *
     * @param payload le payload à signer
     * @return le token complet sous la forme {@code payload-signature}
     */
    public String buildToken(String payload) {
        String signature = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, confirmationTokenProperties.confirmationSecret())
                .hmacHex(payload);
        return payload + "-" + signature;
    }

    /**
     * Génère un token de confirmation opaque et URL-safe.
     * Génère un payload aléatoire via {@link #generatePayload()},
     * puis construit le token via {@link #buildToken(String)}.
     *
     * @return le token complet sous la forme {@code payload-signature}
     */
    public String generateToken() {
        return buildToken(generatePayload());
    }

    /**
     * Vérifie un token de confirmation et retourne le payload s'il est valide.
     * Découpe le token sur le dernier {@code -} pour séparer payload et signature,
     * recalcule le HMAC-SHA256 du payload et le compare à la signature reçue.
     *
     * @param token le token complet de la forme {@code payload-signature}
     * @return le payload extrait du token si la signature est valide
     * @throws InvalidTokenException si le format du token est invalide ou si la signature HMAC ne correspond pas
     */
    public String verify(String token) {
        int separatorIndex = token.lastIndexOf("-");
        if (separatorIndex < 0) {
            throw new InvalidTokenException();
        }
        String payload = token.substring(0, separatorIndex);
        String receivedSignature = token.substring(separatorIndex + 1);
        String expectedSignature = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, confirmationTokenProperties.confirmationSecret())
                .hmacHex(payload);
        if (!expectedSignature.equals(receivedSignature)) {
            throw new InvalidTokenException();
        }
        return payload;
    }
}
