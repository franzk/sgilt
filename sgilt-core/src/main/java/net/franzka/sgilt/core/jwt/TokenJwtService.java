package net.franzka.sgilt.core.jwt;

import io.jsonwebtoken.Claims;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Map;

/**
 * Service JWT générique, configurable par constructeur.
 * Instancié via {@link JwtConfig} avec une clé dérivée et une durée d'expiration propres à chaque usage.
 * Non annoté {@code @Service} — géré comme bean Spring via {@code @Configuration}.
 */
public class TokenJwtService {

    private final JwtService jwtService;
    private final SecretKey key;
    private final Duration expiration;

    /**
     * Construit un service JWT pour un usage spécifique.
     * Dérive la clé HMAC au moment de la construction à partir du secret et du sel fournis.
     *
     * @param jwtService le service JWT technique de bas niveau
     * @param secret     le secret encodé en base64 utilisé pour dériver la clé
     * @param salt       le sel propre à cet usage (ex. {@code "confirmation"}, {@code "set-password"})
     * @param expiration la durée de validité des tokens générés
     */
    public TokenJwtService(JwtService jwtService, String secret, String salt, Duration expiration) {
        this.jwtService = jwtService;
        this.key = jwtService.deriveKey(secret, salt);
        this.expiration = expiration;
    }

    /**
     * Génère un JWT signé avec les claims et le sujet donnés,
     * en utilisant la clé et l'expiration configurées à la construction.
     *
     * @param claims  les claims métier à inclure dans le payload
     * @param subject le sujet du JWT (typiquement l'email)
     * @return le JWT signé sous forme de chaîne compacte
     */
    public String generateToken(Map<String, Object> claims, String subject) {
        return jwtService.generateToken(claims, subject, expiration, key);
    }

    /**
     * Parse et vérifie la signature du JWT, puis retourne ses claims.
     *
     * @param token le JWT à parser
     * @return les claims du JWT
     * @throws io.jsonwebtoken.JwtException si la signature est invalide ou le token malformé
     */
    public Claims extractClaims(String token) {
        return jwtService.extractClaims(token, key);
    }

    /**
     * Indique si le JWT est expiré.
     *
     * @param token le JWT à vérifier
     * @return {@code true} si le token est expiré, {@code false} sinon
     */
    public boolean isExpired(String token) {
        return jwtService.isExpired(token, key);
    }

    /**
     * Extrait le jti (identifiant unique) du JWT.
     *
     * @param token le JWT dont on extrait le jti
     * @return le jti du token
     */
    public String extractJti(String token) {
        return extractClaims(token).getId();
    }
}
