package net.franzka.sgilt.core.onboarding.service;

import jakarta.persistence.EntityNotFoundException;
import net.franzka.sgilt.core.jwt.TokenJwtService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfirmationServiceTest {

    private static final String TOKEN = "testpayload123456789-" + "a".repeat(64);
    private static final String EMAIL = "user@example.com";

    @Mock
    private OnboardingSessionService onboardingSessionService;

    @Mock
    private TokenJwtService setPasswordTokenJwtService;

    @InjectMocks
    private ConfirmationService confirmationService;

    // -------------------------------------------------------------------------
    // validateConfirmationToken
    // -------------------------------------------------------------------------

    @Nested
    class ValidateConfirmationToken {

        @Test
        void givenValidToken_whenValidateConfirmationToken_thenReturnsOnboarding() {
            Onboarding expected = Onboarding.builder().email(EMAIL).build();
            when(onboardingSessionService.validate(TOKEN)).thenReturn(expected);

            Onboarding result = confirmationService.validateConfirmationToken(TOKEN);

            assertThat(result).isSameAs(expected);
        }

        @Test
        void givenToken_whenValidateConfirmationToken_thenDelegatesToOnboardingSessionService() {
            when(onboardingSessionService.validate(TOKEN)).thenReturn(Onboarding.builder().build());

            confirmationService.validateConfirmationToken(TOKEN);

            verify(onboardingSessionService).validate(TOKEN);
        }

        @Test
        void givenExpiredToken_whenValidateConfirmationToken_thenPropagatesTokenExpiredException() {
            when(onboardingSessionService.validate(TOKEN)).thenThrow(new TokenExpiredException());

            assertThatExceptionOfType(TokenExpiredException.class)
                    .isThrownBy(() -> confirmationService.validateConfirmationToken(TOKEN));
        }

        @Test
        void givenInvalidToken_whenValidateConfirmationToken_thenPropagatesInvalidTokenException() {
            when(onboardingSessionService.validate(TOKEN)).thenThrow(new InvalidTokenException());

            assertThatExceptionOfType(InvalidTokenException.class)
                    .isThrownBy(() -> confirmationService.validateConfirmationToken(TOKEN));
        }

        @Test
        void givenUnknownToken_whenValidateConfirmationToken_thenPropagatesEntityNotFoundException() {
            when(onboardingSessionService.validate(TOKEN)).thenThrow(new EntityNotFoundException());

            assertThatExceptionOfType(EntityNotFoundException.class)
                    .isThrownBy(() -> confirmationService.validateConfirmationToken(TOKEN));
        }

        @Test
        void givenAlreadyUsedToken_whenValidateConfirmationToken_thenPropagatesTokenAlreadyUsedException() {
            when(onboardingSessionService.validate(TOKEN)).thenThrow(new TokenAlreadyUsedException());

            assertThatExceptionOfType(TokenAlreadyUsedException.class)
                    .isThrownBy(() -> confirmationService.validateConfirmationToken(TOKEN));
        }
    }

    // -------------------------------------------------------------------------
    // generateSetPasswordResponse
    // -------------------------------------------------------------------------

    @Nested
    class GenerateSetPasswordResponse {

        @Test
        void givenOnboarding_whenGenerateSetPasswordResponse_thenDtoContainsEmail() {
            Onboarding onboarding = buildOnboarding(UUID.randomUUID());
            when(setPasswordTokenJwtService.generateToken(any(), eq(EMAIL))).thenReturn("sp.jwt");

            SetPasswordTokenDto result = confirmationService.generateSetPasswordResponse(onboarding);

            assertThat(result.email()).isEqualTo(EMAIL);
        }

        @Test
        void givenOnboarding_whenGenerateSetPasswordResponse_thenDtoContainsSetPasswordToken() {
            Onboarding onboarding = buildOnboarding(UUID.randomUUID());
            when(setPasswordTokenJwtService.generateToken(any(), eq(EMAIL))).thenReturn("sp.jwt");

            SetPasswordTokenDto result = confirmationService.generateSetPasswordResponse(onboarding);

            assertThat(result.setPasswordToken()).isEqualTo("sp.jwt");
        }

        @Test
        void givenOnboarding_whenGenerateSetPasswordResponse_thenPassesOnboardingIdAsClaimAndEmailAsSubject() {
            UUID onboardingId = UUID.randomUUID();
            Onboarding onboarding = buildOnboarding(onboardingId);
            when(setPasswordTokenJwtService.generateToken(any(), eq(EMAIL))).thenReturn("sp.jwt");

            confirmationService.generateSetPasswordResponse(onboarding);

            @SuppressWarnings("unchecked")
            ArgumentCaptor<Map<String, Object>> claimsCaptor = ArgumentCaptor.forClass(Map.class);
            verify(setPasswordTokenJwtService).generateToken(claimsCaptor.capture(), eq(EMAIL));
            assertThat(claimsCaptor.getValue())
                    .containsEntry("onboardingId", onboardingId.toString());
        }

        private Onboarding buildOnboarding(UUID id) {
            return Onboarding.builder()
                    .id(id)
                    .email(EMAIL)
                    .build();
        }
    }
}
