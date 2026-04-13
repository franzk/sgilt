package net.franzka.sgilt.core.jwt;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenJwtServiceTest {

    private final String SECRET = randomBase64UrlSecret();
    private static final String SALT       = "test-salt";

    private static String randomBase64UrlSecret() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    private static final Duration EXPIRATION = Duration.ofMinutes(10);
    private static final String TOKEN      = "header.payload.signature";
    private static final String SUBJECT    = "user@example.com";

    @Mock
    private JwtService jwtService;

    @Mock
    private SecretKey derivedKey;

    private TokenJwtService tokenJwtService;

    @BeforeEach
    void setUp() {
        when(jwtService.deriveKey(SECRET, SALT)).thenReturn(derivedKey);
        tokenJwtService = new TokenJwtService(jwtService, SECRET, SALT, EXPIRATION);
    }

    // -------------------------------------------------------------------------
    // Constructeur — dérivation de clé
    // -------------------------------------------------------------------------

    @Nested
    class Constructor {

        @Test
        void givenSecretAndSalt_whenConstructed_thenDerivesKeyFromJwtService() {
            verify(jwtService).deriveKey(SECRET, SALT);
        }
    }

    // -------------------------------------------------------------------------
    // generateToken
    // -------------------------------------------------------------------------

    @Nested
    class GenerateToken {

        @Test
        void givenClaimsAndSubject_whenGenerateToken_thenDelegatesToJwtServiceWithDerivedKey() {
            Map<String, Object> claims = Map.of("reservationId", "abc-123");
            when(jwtService.generateToken(claims, SUBJECT, EXPIRATION, derivedKey)).thenReturn("signed.jwt");

            String result = tokenJwtService.generateToken(claims, SUBJECT);

            assertThat(result).isEqualTo("signed.jwt");
            verify(jwtService).generateToken(claims, SUBJECT, EXPIRATION, derivedKey);
        }

        @Test
        void givenEmptyClaims_whenGenerateToken_thenDelegatesToJwtService() {
            when(jwtService.generateToken(Map.of(), SUBJECT, EXPIRATION, derivedKey)).thenReturn("signed.jwt");

            tokenJwtService.generateToken(Map.of(), SUBJECT);

            verify(jwtService).generateToken(Map.of(), SUBJECT, EXPIRATION, derivedKey);
        }
    }

    // -------------------------------------------------------------------------
    // extractClaims
    // -------------------------------------------------------------------------

    @Nested
    class ExtractClaims {

        @Test
        void givenToken_whenExtractClaims_thenDelegatesToJwtServiceWithDerivedKey() {
            Claims mockClaims = mock(Claims.class);
            when(jwtService.extractClaims(TOKEN, derivedKey)).thenReturn(mockClaims);

            Claims result = tokenJwtService.extractClaims(TOKEN);

            assertThat(result).isSameAs(mockClaims);
            verify(jwtService).extractClaims(TOKEN, derivedKey);
        }
    }

    // -------------------------------------------------------------------------
    // isExpired
    // -------------------------------------------------------------------------

    @Nested
    class IsExpired {

        @Test
        void givenExpiredToken_whenIsExpired_thenReturnsTrue() {
            when(jwtService.isExpired(TOKEN, derivedKey)).thenReturn(true);

            assertThat(tokenJwtService.isExpired(TOKEN)).isTrue();
        }

        @Test
        void givenFreshToken_whenIsExpired_thenReturnsFalse() {
            when(jwtService.isExpired(TOKEN, derivedKey)).thenReturn(false);

            assertThat(tokenJwtService.isExpired(TOKEN)).isFalse();
        }
    }

    // -------------------------------------------------------------------------
    // extractJti
    // -------------------------------------------------------------------------

    @Nested
    class ExtractJti {

        @Test
        void givenTokenWithJti_whenExtractJti_thenReturnsJti() {
            Claims mockClaims = mock(Claims.class);
            when(jwtService.extractClaims(TOKEN, derivedKey)).thenReturn(mockClaims);
            when(mockClaims.getId()).thenReturn("550e8400-e29b-41d4-a716-446655440000");

            String jti = tokenJwtService.extractJti(TOKEN);

            assertThat(jti).isEqualTo("550e8400-e29b-41d4-a716-446655440000");
        }

        @Test
        void givenToken_whenExtractJti_thenDelegatesToExtractClaimsWithDerivedKey() {
            Claims mockClaims = mock(Claims.class);
            when(jwtService.extractClaims(TOKEN, derivedKey)).thenReturn(mockClaims);
            when(mockClaims.getId()).thenReturn("some-jti");

            tokenJwtService.extractJti(TOKEN);

            verify(jwtService).extractClaims(TOKEN, derivedKey);
        }
    }
}
