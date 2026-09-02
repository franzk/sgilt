package net.franzka.sgilt.core.jwt.service;

import jakarta.persistence.EntityNotFoundException;
import net.franzka.sgilt.core.config.ConfirmationTokenProperties;
import net.franzka.sgilt.core.jwt.domain.ActionToken;
import net.franzka.sgilt.core.jwt.domain.ActionType;
import net.franzka.sgilt.core.jwt.repository.ActionTokenRepository;
import net.franzka.sgilt.core.onboarding.exception.InvalidTokenException;
import net.franzka.sgilt.core.onboarding.exception.TokenExpiredException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActionTokenServiceTest {

    @Mock
    private ActionTokenRepository actionTokenRepository;

    @Mock
    private VerificationTokenHmacService verificationTokenHmacService;

    @Mock
    private ConfirmationTokenProperties confirmationTokenProperties;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ActionTokenService actionTokenService;

    private static final String EMAIL = "pro@sgilt.fr";

    // -------------------------------------------------------------------------
    // createToken
    // -------------------------------------------------------------------------

    @Nested
    class CreateToken {

        @Test
        void givenPayload_whenCreateToken_thenExpiresAtUsesPrestataireOnboardingExpirationHours() {
            when(verificationTokenHmacService.generate())
                    .thenReturn(new VerificationTokenHmacService.GeneratedToken("payload", "payload-signature"));
            when(confirmationTokenProperties.prestataireOnboardingExpirationHours()).thenReturn(168);
            when(objectMapper.writeValueAsString(any())).thenReturn("{\"email\":\"pro@sgilt.fr\"}");
            ArgumentCaptor<ActionToken> captor = ArgumentCaptor.forClass(ActionToken.class);
            when(actionTokenRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

            actionTokenService.createToken(ActionType.PRESTATAIRE_ONBOARDING, Map.of("email", EMAIL));

            assertThat(captor.getValue().getExpiresAt())
                    .isCloseTo(LocalDateTime.now().plusHours(168), within3Seconds());
        }

        @Test
        void givenPayloadSerializationFails_whenCreateToken_thenThrowsIllegalStateException() {
            when(verificationTokenHmacService.generate())
                    .thenReturn(new VerificationTokenHmacService.GeneratedToken("payload", "payload-signature"));
            when(objectMapper.writeValueAsString(any())).thenThrow(mock(JacksonException.class));

            assertThatThrownBy(() -> actionTokenService.createToken(ActionType.PRESTATAIRE_ONBOARDING, Map.of("email", EMAIL)))
                    .isInstanceOf(IllegalStateException.class);
            verify(actionTokenRepository, never()).save(any());
        }
    }

    // -------------------------------------------------------------------------
    // checkToken
    // -------------------------------------------------------------------------

    @Nested
    class CheckToken {

        @Test
        void givenValidNonExpiredToken_whenCheckToken_thenReturnsToken() {
            ActionToken token = tokenFor(EMAIL);
            when(verificationTokenHmacService.verify("payload-signature")).thenReturn(token.getHmacPayload());
            when(actionTokenRepository.findByHmacPayload(token.getHmacPayload())).thenReturn(Optional.of(token));

            assertThat(actionTokenService.checkToken("payload-signature")).isEqualTo(token);
        }

        @Test
        void givenInvalidHmacSignature_whenCheckToken_thenThrowsInvalidToken() {
            when(verificationTokenHmacService.verify("bad-signature")).thenThrow(new InvalidTokenException());

            assertThatThrownBy(() -> actionTokenService.checkToken("bad-signature"))
                    .isInstanceOf(InvalidTokenException.class);
        }

        @Test
        void givenNoTokenForPayload_whenCheckToken_thenThrowsNotFound() {
            when(verificationTokenHmacService.verify("payload-signature")).thenReturn("unknown-payload");
            when(actionTokenRepository.findByHmacPayload("unknown-payload")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> actionTokenService.checkToken("payload-signature"))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        void givenExpiredToken_whenCheckToken_thenThrowsTokenExpired() {
            ActionToken token = tokenFor(EMAIL);
            token.setExpiresAt(LocalDateTime.now().minusHours(1));
            when(verificationTokenHmacService.verify("payload-signature")).thenReturn(token.getHmacPayload());
            when(actionTokenRepository.findByHmacPayload(token.getHmacPayload())).thenReturn(Optional.of(token));

            assertThatThrownBy(() -> actionTokenService.checkToken("payload-signature"))
                    .isInstanceOf(TokenExpiredException.class);
        }
    }

    // -------------------------------------------------------------------------
    // findById
    // -------------------------------------------------------------------------

    @Nested
    class FindById {

        @Test
        void givenExistingId_whenFindById_thenReturnsToken() {
            ActionToken token = tokenFor(EMAIL);
            when(actionTokenRepository.findById(token.getId())).thenReturn(Optional.of(token));

            assertThat(actionTokenService.findById(token.getId())).isEqualTo(token);
        }

        @Test
        void givenUnknownId_whenFindById_thenThrowsNotFound() {
            UUID id = UUID.randomUUID();
            when(actionTokenRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> actionTokenService.findById(id)).isInstanceOf(EntityNotFoundException.class);
        }
    }

    // -------------------------------------------------------------------------
    // consume
    // -------------------------------------------------------------------------

    @Nested
    class Consume {

        @Test
        void givenToken_whenConsume_thenDeletesIt() {
            ActionToken token = tokenFor(EMAIL);

            actionTokenService.consume(token);

            verify(actionTokenRepository).delete(token);
        }
    }

    // -------------------------------------------------------------------------
    // findAllByType
    // -------------------------------------------------------------------------

    @Nested
    class FindAllByType {

        @Test
        void givenTokensForType_whenFindAllByType_thenReturnsThem() {
            List<ActionToken> tokens = List.of(tokenFor(EMAIL));
            when(actionTokenRepository.findByType(ActionType.PRESTATAIRE_ONBOARDING)).thenReturn(tokens);

            assertThat(actionTokenService.findAllByType(ActionType.PRESTATAIRE_ONBOARDING)).isEqualTo(tokens);
        }
    }

    // -------------------------------------------------------------------------
    // findPendingByEmail
    // -------------------------------------------------------------------------

    @Nested
    class FindPendingByEmail {

        @Test
        void givenTokenForEmail_whenFindPendingByEmail_thenReturnsIt() {
            ActionToken token = tokenFor(EMAIL);
            when(actionTokenRepository.findByType(ActionType.PRESTATAIRE_ONBOARDING)).thenReturn(List.of(token));
            when(objectMapper.readValue(eq(token.getPayload()), eq(Map.class))).thenReturn(Map.of("email", EMAIL));

            assertThat(actionTokenService.findPendingByEmail(ActionType.PRESTATAIRE_ONBOARDING, EMAIL))
                    .isEqualTo(token);
        }

        @Test
        void givenNoTokenForEmail_whenFindPendingByEmail_thenThrowsNotFound() {
            when(actionTokenRepository.findByType(ActionType.PRESTATAIRE_ONBOARDING)).thenReturn(List.of());

            assertThatThrownBy(() -> actionTokenService.findPendingByEmail(ActionType.PRESTATAIRE_ONBOARDING, EMAIL))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    // -------------------------------------------------------------------------
    // renewExpiration
    // -------------------------------------------------------------------------

    @Nested
    class RenewExpiration {

        @Test
        void givenExistingToken_whenRenewExpiration_thenExpiresAtIsResetAndTokenRebuilt() {
            ActionToken token = tokenFor(EMAIL);
            token.setExpiresAt(LocalDateTime.now().minusDays(10));
            when(confirmationTokenProperties.prestataireOnboardingExpirationHours()).thenReturn(168);
            when(verificationTokenHmacService.buildToken(token.getHmacPayload())).thenReturn("payload-signature");

            String hmacToken = actionTokenService.renewExpiration(token);

            assertThat(hmacToken).isEqualTo("payload-signature");
            assertThat(token.getExpiresAt()).isCloseTo(LocalDateTime.now().plusHours(168), within3Seconds());
            verify(actionTokenRepository).save(token);
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private ActionToken tokenFor(String email) {
        return ActionToken.builder()
                .id(UUID.randomUUID())
                .hmacPayload("payload-" + UUID.randomUUID())
                .type(ActionType.PRESTATAIRE_ONBOARDING)
                .payload("{\"email\":\"" + email + "\"}")
                .expiresAt(LocalDateTime.now().plusHours(168))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private org.assertj.core.data.TemporalUnitOffset within3Seconds() {
        return org.assertj.core.api.Assertions.within(3, java.time.temporal.ChronoUnit.SECONDS);
    }
}
