package net.franzka.sgilt.core.evenement.billetterie.commande.domain;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Code d'entrée d'une commande et contenu du QR de son e-billet, présenté à l'entrée (fait entrer
 * toutes ses places). Seul le code est stocké ; le contenu du QR le préfixe de son type, {@code SGILT:C:} —
 * format défini ici seulement, relu au scan.
 */
public final class EntryCodeUtils {

    // 128 bits aléatoires par code : impossible à deviner, donc à forger pour entrer.
    private static final int CODE_BYTES = 16;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String COMMANDE_QR_PREFIX = "SGILT:C:";

    private EntryCodeUtils() {
    }

    /**
     * Tire un nouveau code d'entrée : 16 octets aléatoires en base64url sans padding (22 caractères).
     *
     * @return le code
     */
    public static String newCode() {
        byte[] bytes = new byte[CODE_BYTES];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Contenu du QR de l'e-billet d'une commande, qui fait entrer toutes ses places.
     *
     * @param code le code de la commande
     * @return {@code SGILT:C:<code>}
     */
    public static String commandeQrContent(String code) {
        return COMMANDE_QR_PREFIX + code;
    }
}
