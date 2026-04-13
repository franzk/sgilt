package net.franzka.sgilt.core.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Service technique générique de gestion JWT.
 * Ne contient aucune logique métier — délègue la sémantique des claims aux services spécialisés.
 */
@Service
public class JwtService {

    /**
     * Construit et signe un JWT avec les claims, le sujet, la durée d'expiration et la clé fournis.
     *
     * @param claims     les claims métier à inclure dans le payload
     * @param subject    le sujet du JWT (typiquement l'email)
     * @param expiration la durée de validité du token
     * @param key        la clé HMAC utilisée pour signer
     * @return le JWT signé sous forme de chaîne compacte
     */
    public String generateToken(Map<String, Object> claims, String subject, Duration expiration, SecretKey key) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plus(expiration)))
                .signWith(key)
                .compact();
    }

    /**
     * Parse et vérifie la signature du JWT, puis retourne ses claims.
     *
     * @param token le JWT à parser
     * @param key   la clé HMAC utilisée pour vérifier la signature
     * @return les claims du JWT
     * @throws io.jsonwebtoken.JwtException si la signature est invalide ou le token malformé
     */
    public Claims extractClaims(String token, SecretKey key) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Indique si le JWT est expiré, en capturant l'exception levée par JJWT pour les tokens expirés.
     *
     * @param token le JWT à vérifier
     * @param key   la clé HMAC utilisée pour parser le token
     * @return {@code true} si le token est expiré, {@code false} sinon
     */
    public boolean isExpired(String token, SecretKey key) {
        try {
            return extractClaims(token, key).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * Dérive une clé HMAC-SHA256 à partir d'un secret base64 et d'un sel.
     * Algorithme : décode le secret en bytes, concatène le sel en UTF-8, hash SHA-256.
     *
     * @param secret le secret encodé en base64
     * @param salt   le sel textuel propre à chaque usage (ex. "confirmation", "set-password")
     * @return la clé HMAC dérivée
     * @throws IllegalStateException si SHA-256 n'est pas disponible (ne devrait jamais arriver)
     */
    public SecretKey deriveKey(String secret, String salt) {
        try {
            byte[] secretBytes = Decoders.BASE64URL.decode(secret);
            byte[] saltBytes = salt.getBytes(StandardCharsets.UTF_8);
            byte[] combined = new byte[secretBytes.length + saltBytes.length];
            System.arraycopy(secretBytes, 0, combined, 0, secretBytes.length);
            System.arraycopy(saltBytes, 0, combined, secretBytes.length, saltBytes.length);
            byte[] keyBytes = MessageDigest.getInstance("SHA-256").digest(combined);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
