package net.franzka.sgilt.core.jwt;

import net.franzka.sgilt.core.config.ConfirmationTokenProperties;
import net.franzka.sgilt.core.onboarding.exception.InvalidTokenException;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfirmationTokenHmacServiceTest {

    private static final String SECRET  = "test-secret-for-hmac";
    private static final String PAYLOAD = "testPayload123";

    @Mock
    private ConfirmationTokenProperties confirmationTokenProperties;

    @InjectMocks
    private ConfirmationTokenHmacService confirmationTokenHmacService;

    // -------------------------------------------------------------------------
    // generatePayload
    // -------------------------------------------------------------------------

    @Nested
    class GeneratePayload {

        @Test
        void whenGeneratePayload_thenReturnsNonBlankString() {
            assertThat(confirmationTokenHmacService.generatePayload()).isNotBlank();
        }

        @Test
        void whenGeneratePayload_thenReturns22Chars() {
            // 16 octets en Base64 URL-safe sans padding = 22 caractères
            assertThat(confirmationTokenHmacService.generatePayload()).hasSize(22);
        }

        @Test
        void whenGeneratePayloadTwice_thenReturnsDifferentValues() {
            String first = confirmationTokenHmacService.generatePayload();
            String second = confirmationTokenHmacService.generatePayload();
            assertThat(first).isNotEqualTo(second);
        }
    }

    // -------------------------------------------------------------------------
    // buildToken
    // -------------------------------------------------------------------------

    @Nested
    class BuildToken {

        @Test
        void givenPayload_whenBuildToken_thenTokenStartsWithPayload() {
            when(confirmationTokenProperties.confirmationSecret()).thenReturn(SECRET);

            String token = confirmationTokenHmacService.buildToken(PAYLOAD);

            assertThat(token).startsWith(PAYLOAD + "-");
        }

        @Test
        void givenPayload_whenBuildToken_thenSignatureIsHmacSha256HexOfPayload() {
            when(confirmationTokenProperties.confirmationSecret()).thenReturn(SECRET);
            String expectedSignature = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, SECRET).hmacHex(PAYLOAD);

            String token = confirmationTokenHmacService.buildToken(PAYLOAD);

            String actualSignature = token.substring(token.lastIndexOf("-") + 1);
            assertThat(actualSignature).isEqualTo(expectedSignature);
        }

        @Test
        void givenSamePayload_whenBuildTokenTwice_thenReturnsSameToken() {
            when(confirmationTokenProperties.confirmationSecret()).thenReturn(SECRET);

            String token1 = confirmationTokenHmacService.buildToken(PAYLOAD);
            String token2 = confirmationTokenHmacService.buildToken(PAYLOAD);

            assertThat(token1).isEqualTo(token2);
        }
    }

    // -------------------------------------------------------------------------
    // generateToken
    // -------------------------------------------------------------------------

    @Nested
    class GenerateToken {

        @Test
        void whenGenerateToken_thenReturnedTokenContainsSeparator() {
            when(confirmationTokenProperties.confirmationSecret()).thenReturn(SECRET);

            assertThat(confirmationTokenHmacService.generateToken()).contains("-");
        }

        @Test
        void whenGenerateTokenTwice_thenReturnsDifferentTokens() {
            when(confirmationTokenProperties.confirmationSecret()).thenReturn(SECRET);

            String token1 = confirmationTokenHmacService.generateToken();
            String token2 = confirmationTokenHmacService.generateToken();

            assertThat(token1).isNotEqualTo(token2);
        }
    }

    // -------------------------------------------------------------------------
    // verify
    // -------------------------------------------------------------------------

    @Nested
    class Verify {

        @Test
        void givenValidToken_whenVerify_thenReturnsPayload() {
            when(confirmationTokenProperties.confirmationSecret()).thenReturn(SECRET);
            String token = confirmationTokenHmacService.buildToken(PAYLOAD);

            String result = confirmationTokenHmacService.verify(token);

            assertThat(result).isEqualTo(PAYLOAD);
        }

        @Test
        void givenPayloadContainingHyphen_whenVerify_thenReturnsPayload() {
            when(confirmationTokenProperties.confirmationSecret()).thenReturn(SECRET);
            String payloadWithHyphen = "abc-def";
            String token = confirmationTokenHmacService.buildToken(payloadWithHyphen);

            String result = confirmationTokenHmacService.verify(token);

            assertThat(result).isEqualTo(payloadWithHyphen);
        }

        @Test
        void givenTokenWithoutSeparator_whenVerify_thenThrowsInvalidTokenException() {
            assertThatExceptionOfType(InvalidTokenException.class)
                    .isThrownBy(() -> confirmationTokenHmacService.verify("noseparatortoken"));
        }

        @Test
        void givenTokenWithWrongSignature_whenVerify_thenThrowsInvalidTokenException() {
            when(confirmationTokenProperties.confirmationSecret()).thenReturn(SECRET);
            String tampered = PAYLOAD + "-" + "0".repeat(64);

            assertThatExceptionOfType(InvalidTokenException.class)
                    .isThrownBy(() -> confirmationTokenHmacService.verify(tampered));
        }

        @Test
        void givenTokenSignedWithDifferentSecret_whenVerify_thenThrowsInvalidTokenException() {
            when(confirmationTokenProperties.confirmationSecret()).thenReturn(SECRET);
            String signatureWithOtherSecret = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, "other-secret").hmacHex(PAYLOAD);
            String token = PAYLOAD + "-" + signatureWithOtherSecret;

            assertThatExceptionOfType(InvalidTokenException.class)
                    .isThrownBy(() -> confirmationTokenHmacService.verify(token));
        }

        @Test
        void givenGeneratedToken_whenVerify_thenRoundTripExtractsConsistentPayload() {
            when(confirmationTokenProperties.confirmationSecret()).thenReturn(SECRET);
            String token = confirmationTokenHmacService.generateToken();

            String extractedPayload = confirmationTokenHmacService.verify(token);

            assertThat(token).startsWith(extractedPayload + "-");
        }
    }
}
