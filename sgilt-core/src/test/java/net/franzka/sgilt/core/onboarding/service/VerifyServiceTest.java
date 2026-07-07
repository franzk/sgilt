package net.franzka.sgilt.core.onboarding.service;

import jakarta.persistence.EntityNotFoundException;
import net.franzka.sgilt.core.jwt.domain.ActionToken;
import net.franzka.sgilt.core.jwt.service.ActionTokenService;
import net.franzka.sgilt.core.jwt.service.TokenJwtService;
import net.franzka.sgilt.core.onboarding.domain.Onboarding;
import net.franzka.sgilt.core.onboarding.dto.SetPasswordTokenDto;
import net.franzka.sgilt.core.onboarding.exception.InvalidTokenException;
import net.franzka.sgilt.core.onboarding.exception.TokenAlreadyUsedException;
import net.franzka.sgilt.core.onboarding.exception.TokenExpiredException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerifyServiceTest {

    private static final String TOKEN = "testpayload123456789-" + "a".repeat(64);
    private static final String EMAIL = "user@example.com";

    @Mock
    private OnboardingSessionService onboardingSessionService;

    @Mock
    private ActionTokenService actionTokenService;

    @Mock
    private TokenJwtService setPasswordTokenJwtService;

    @InjectMocks
    private VerifyService verifyService;

    // -------------------------------------------------------------------------
    // verify — flux client (Onboarding)
    // -------------------------------------------------------------------------

    @Nested
    class VerifyClientOnboarding {

        @Test
        void givenValidToken_whenVerify_thenChecksTokenAndAdvancesToConfirmation() {
            Onboarding onboarding = buildOnboarding(UUID.randomUUID());
            when(onboardingSessionService.checkToken(TOKEN)).thenReturn(onboarding);
            when(setPasswordTokenJwtService.generateToken(any(), eq(EMAIL))).thenReturn("sp.jwt");

            verifyService.verify(TOKEN);

            verify(onboardingSessionService).checkToken(TOKEN);
            verify(onboardingSessionService).advanceToConfirmation(onboarding);
        }

        @Test
        void givenValidToken_whenVerify_thenReturnsDtoWithEmailAndSetPasswordToken() {
            Onboarding onboarding = buildOnboarding(UUID.randomUUID());
            when(onboardingSessionService.checkToken(TOKEN)).thenReturn(onboarding);
            when(setPasswordTokenJwtService.generateToken(any(), eq(EMAIL))).thenReturn("sp.jwt");

            SetPasswordTokenDto result = verifyService.verify(TOKEN);

            assertThat(result.email()).isEqualTo(EMAIL);
            assertThat(result.setPasswordToken()).isEqualTo("sp.jwt");
        }

        @Test
        void givenValidToken_whenVerify_thenPassesOnboardingIdAsClaimAndEmailAsSubject() {
            UUID onboardingId = UUID.randomUUID();
            Onboarding onboarding = buildOnboarding(onboardingId);
            when(onboardingSessionService.checkToken(TOKEN)).thenReturn(onboarding);
            when(setPasswordTokenJwtService.generateToken(any(), eq(EMAIL))).thenReturn("sp.jwt");

            verifyService.verify(TOKEN);

            @SuppressWarnings("unchecked")
            ArgumentCaptor<Map<String, Object>> claimsCaptor = ArgumentCaptor.forClass(Map.class);
            verify(setPasswordTokenJwtService).generateToken(claimsCaptor.capture(), eq(EMAIL));
            assertThat(claimsCaptor.getValue()).containsEntry("onboardingId", onboardingId.toString());
        }

        @Test
        void givenExpiredToken_whenVerify_thenPropagatesTokenExpiredExceptionWithoutFallback() {
            when(onboardingSessionService.checkToken(TOKEN)).thenThrow(new TokenExpiredException());

            assertThatExceptionOfType(TokenExpiredException.class)
                    .isThrownBy(() -> verifyService.verify(TOKEN));

            verify(actionTokenService, never()).checkToken(any());
        }

        @Test
        void givenInvalidToken_whenVerify_thenPropagatesInvalidTokenExceptionWithoutFallback() {
            when(onboardingSessionService.checkToken(TOKEN)).thenThrow(new InvalidTokenException());

            assertThatExceptionOfType(InvalidTokenException.class)
                    .isThrownBy(() -> verifyService.verify(TOKEN));

            verify(actionTokenService, never()).checkToken(any());
        }

        @Test
        void givenAlreadyUsedToken_whenVerify_thenPropagatesTokenAlreadyUsedExceptionWithoutFallback() {
            when(onboardingSessionService.checkToken(TOKEN)).thenThrow(new TokenAlreadyUsedException());

            assertThatExceptionOfType(TokenAlreadyUsedException.class)
                    .isThrownBy(() -> verifyService.verify(TOKEN));

            verify(actionTokenService, never()).checkToken(any());
        }

        private Onboarding buildOnboarding(UUID id) {
            return Onboarding.builder().id(id).email(EMAIL).build();
        }
    }

    // -------------------------------------------------------------------------
    // verify — repli sur le flux prestataire (ActionToken)
    // -------------------------------------------------------------------------

    @Nested
    class VerifyPrestataireOnboarding {

        @Test
        void givenTokenUnknownToOnboarding_whenVerify_thenFallsBackToActionToken() {
            when(onboardingSessionService.checkToken(TOKEN)).thenThrow(new EntityNotFoundException());
            ActionToken actionToken = buildActionToken(UUID.randomUUID());
            when(actionTokenService.checkToken(TOKEN)).thenReturn(actionToken);
            when(actionTokenService.readPayload(actionToken)).thenReturn(Map.of("email", EMAIL));
            when(setPasswordTokenJwtService.generateToken(any(), eq(EMAIL))).thenReturn("sp.jwt");

            SetPasswordTokenDto result = verifyService.verify(TOKEN);

            assertThat(result.email()).isEqualTo(EMAIL);
            assertThat(result.setPasswordToken()).isEqualTo("sp.jwt");
        }

        @Test
        void givenTokenUnknownToOnboarding_whenVerify_thenDoesNotAdvanceOnboardingConfirmation() {
            when(onboardingSessionService.checkToken(TOKEN)).thenThrow(new EntityNotFoundException());
            ActionToken actionToken = buildActionToken(UUID.randomUUID());
            when(actionTokenService.checkToken(TOKEN)).thenReturn(actionToken);
            when(actionTokenService.readPayload(actionToken)).thenReturn(Map.of("email", EMAIL));
            when(setPasswordTokenJwtService.generateToken(any(), eq(EMAIL))).thenReturn("sp.jwt");

            verifyService.verify(TOKEN);

            verify(onboardingSessionService, never()).advanceToConfirmation(any());
        }

        @Test
        void givenTokenUnknownToOnboarding_whenVerify_thenPassesActionTokenIdAsClaimAndEmailAsSubject() {
            UUID actionTokenId = UUID.randomUUID();
            when(onboardingSessionService.checkToken(TOKEN)).thenThrow(new EntityNotFoundException());
            ActionToken actionToken = buildActionToken(actionTokenId);
            when(actionTokenService.checkToken(TOKEN)).thenReturn(actionToken);
            when(actionTokenService.readPayload(actionToken)).thenReturn(Map.of("email", EMAIL));
            when(setPasswordTokenJwtService.generateToken(any(), eq(EMAIL))).thenReturn("sp.jwt");

            verifyService.verify(TOKEN);

            @SuppressWarnings("unchecked")
            ArgumentCaptor<Map<String, Object>> claimsCaptor = ArgumentCaptor.forClass(Map.class);
            verify(setPasswordTokenJwtService).generateToken(claimsCaptor.capture(), eq(EMAIL));
            assertThat(claimsCaptor.getValue()).containsEntry("actionTokenId", actionTokenId.toString());
        }

        @Test
        void givenTokenUnknownToBothFlows_whenVerify_thenPropagatesEntityNotFoundException() {
            when(onboardingSessionService.checkToken(TOKEN)).thenThrow(new EntityNotFoundException());
            when(actionTokenService.checkToken(TOKEN)).thenThrow(new EntityNotFoundException());

            assertThatExceptionOfType(EntityNotFoundException.class)
                    .isThrownBy(() -> verifyService.verify(TOKEN));
        }

        @Test
        void givenExpiredActionToken_whenVerify_thenPropagatesTokenExpiredException() {
            when(onboardingSessionService.checkToken(TOKEN)).thenThrow(new EntityNotFoundException());
            when(actionTokenService.checkToken(TOKEN)).thenThrow(new TokenExpiredException());

            assertThatExceptionOfType(TokenExpiredException.class)
                    .isThrownBy(() -> verifyService.verify(TOKEN));
        }

        private ActionToken buildActionToken(UUID id) {
            return ActionToken.builder().id(id).build();
        }
    }
}
