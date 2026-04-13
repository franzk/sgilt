package net.franzka.sgilt.core.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class JwtServiceTest {

    // "secretForUnitTests" encodé en base64
    private static final String SECRET_A = "c2VjcmV0Rm9yVW5pdFRlc3Rz";
    // "otherSecretForTests" encodé en base64
    private static final String SECRET_B = "b3RoZXJTZWNyZXRGb3JUZXN0cw==";
    private static final String SUBJECT  = "user@example.com";

    private JwtService jwtService;
    private SecretKey testKey;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        testKey = jwtService.deriveKey(SECRET_A, "test");
    }

    // -------------------------------------------------------------------------
    // generateToken
    // -------------------------------------------------------------------------

    @Nested
    class GenerateToken {

        @Test
        void givenValidInputs_whenGenerateToken_thenReturnsNonBlankToken() {
            String token = jwtService.generateToken(Map.of(), SUBJECT, Duration.ofMinutes(10), testKey);

            assertThat(token).isNotBlank();
        }

        @Test
        void givenSubject_whenGenerateToken_thenSubjectIsEmbedded() {
            String token = jwtService.generateToken(Map.of(), SUBJECT, Duration.ofMinutes(10), testKey);

            assertThat(jwtService.extractClaims(token, testKey).getSubject()).isEqualTo(SUBJECT);
        }

        @Test
        void givenCustomClaims_whenGenerateToken_thenClaimsAreEmbedded() {
            Map<String, Object> claims = Map.of("reservationId", "abc-123", "role", "user");

            String token = jwtService.generateToken(claims, SUBJECT, Duration.ofMinutes(10), testKey);
            Claims extracted = jwtService.extractClaims(token, testKey);

            assertThat(extracted.get("reservationId", String.class)).isEqualTo("abc-123");
            assertThat(extracted.get("role", String.class)).isEqualTo("user");
        }

        @Test
        void givenTwoCalls_whenGenerateToken_thenJtiAreUnique() {
            String token1 = jwtService.generateToken(Map.of(), SUBJECT, Duration.ofMinutes(10), testKey);
            String token2 = jwtService.generateToken(Map.of(), SUBJECT, Duration.ofMinutes(10), testKey);

            String jti1 = jwtService.extractClaims(token1, testKey).getId();
            String jti2 = jwtService.extractClaims(token2, testKey).getId();

            assertThat(jti1).isNotEqualTo(jti2);
        }
    }

    // -------------------------------------------------------------------------
    // extractClaims
    // -------------------------------------------------------------------------

    @Nested
    class ExtractClaims {

        @Test
        void givenValidToken_whenExtractClaims_thenReturnsCorrectClaims() {
            String token = jwtService.generateToken(Map.of("k", "v"), SUBJECT, Duration.ofMinutes(10), testKey);

            Claims claims = jwtService.extractClaims(token, testKey);

            assertThat(claims.getSubject()).isEqualTo(SUBJECT);
            assertThat(claims.get("k", String.class)).isEqualTo("v");
            assertThat(claims.getId()).isNotBlank();
        }

        @Test
        void givenTamperedSignature_whenExtractClaims_thenThrowsJwtException() {
            String token = jwtService.generateToken(Map.of(), SUBJECT, Duration.ofMinutes(10), testKey);
            String tampered = token.substring(0, token.length() - 5) + "XXXXX";

            assertThatExceptionOfType(JwtException.class)
                    .isThrownBy(() -> jwtService.extractClaims(tampered, testKey));
        }

        @Test
        void givenWrongKey_whenExtractClaims_thenThrowsJwtException() {
            SecretKey wrongKey = jwtService.deriveKey(SECRET_A, "other-salt");
            String token = jwtService.generateToken(Map.of(), SUBJECT, Duration.ofMinutes(10), testKey);

            assertThatExceptionOfType(JwtException.class)
                    .isThrownBy(() -> jwtService.extractClaims(token, wrongKey));
        }

        @Test
        void givenExpiredToken_whenExtractClaims_thenThrowsExpiredJwtException() {
            String token = jwtService.generateToken(Map.of(), SUBJECT, Duration.ofMillis(-1), testKey);

            assertThatExceptionOfType(ExpiredJwtException.class)
                    .isThrownBy(() -> jwtService.extractClaims(token, testKey));
        }
    }

    // -------------------------------------------------------------------------
    // isExpired
    // -------------------------------------------------------------------------

    @Nested
    class IsExpired {

        @Test
        void givenFreshToken_whenIsExpired_thenReturnsFalse() {
            String token = jwtService.generateToken(Map.of(), SUBJECT, Duration.ofMinutes(10), testKey);

            assertThat(jwtService.isExpired(token, testKey)).isFalse();
        }

        @Test
        void givenExpiredToken_whenIsExpired_thenReturnsTrue() {
            String token = jwtService.generateToken(Map.of(), SUBJECT, Duration.ofMillis(-1), testKey);

            assertThat(jwtService.isExpired(token, testKey)).isTrue();
        }
    }

    // -------------------------------------------------------------------------
    // deriveKey
    // -------------------------------------------------------------------------

    @Nested
    class DeriveKey {

        @Test
        void givenValidSecretAndSalt_whenDeriveKey_thenReturnsNonNullKey() {
            SecretKey key = jwtService.deriveKey(SECRET_A, "salt");

            assertThat(key).isNotNull();
        }

        @Test
        void givenSameInputs_whenDeriveKey_thenProducesSameKey() {
            SecretKey key1 = jwtService.deriveKey(SECRET_A, "salt");
            SecretKey key2 = jwtService.deriveKey(SECRET_A, "salt");

            assertThat(key1.getEncoded()).isEqualTo(key2.getEncoded());
        }

        @Test
        void givenDifferentSalts_whenDeriveKey_thenProducesDifferentKeys() {
            SecretKey key1 = jwtService.deriveKey(SECRET_A, "confirmation");
            SecretKey key2 = jwtService.deriveKey(SECRET_A, "set-password");

            assertThat(key1.getEncoded()).isNotEqualTo(key2.getEncoded());
        }

        @Test
        void givenDifferentSecrets_whenDeriveKey_thenProducesDifferentKeys() {
            SecretKey key1 = jwtService.deriveKey(SECRET_A, "salt");
            SecretKey key2 = jwtService.deriveKey(SECRET_B, "salt");

            assertThat(key1.getEncoded()).isNotEqualTo(key2.getEncoded());
        }
    }
}
