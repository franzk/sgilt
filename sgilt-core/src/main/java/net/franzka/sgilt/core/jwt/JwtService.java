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

@Service
public class JwtService {

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

    public Claims extractClaims(String token, SecretKey key) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isExpired(String token, SecretKey key) {
        try {
            return extractClaims(token, key).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    public SecretKey deriveKey(String secret, String salt) {
        try {
            byte[] secretBytes = Decoders.BASE64.decode(secret);
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
