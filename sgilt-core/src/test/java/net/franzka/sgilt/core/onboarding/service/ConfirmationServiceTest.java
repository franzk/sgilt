package net.franzka.sgilt.core.onboarding.service;

import jakarta.persistence.EntityNotFoundException;
import net.franzka.sgilt.core.jwt.TokenJwtService;
import net.franzka.sgilt.core.onboarding.domain.ConfirmationToken;
import net.franzka.sgilt.core.onboarding.dto.SetPasswordTokenDto;
import net.franzka.sgilt.core.onboarding.exception.InvalidTokenException;
import net.franzka.sgilt.core.onboarding.exception.TokenAlreadyUsedException;
import net.franzka.sgilt.core.onboarding.exception.TokenExpiredException;
import net.franzka.sgilt.core.reservation.domain.Reservation;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmationServiceTest {

    private static final String TOKEN = "testpayload123456789-" + "a".repeat(64);
    private static final String EMAIL = "user@example.com";

    @Mock
    private ConfirmationTokenService confirmationTokenService;

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
        void givenValidJwt_whenValidateConfirmationToken_thenReturnsConfirmationToken() {
            ConfirmationToken expected = ConfirmationToken.builder().email(EMAIL).build();
            when(confirmationTokenService.validate(TOKEN)).thenReturn(expected);

            ConfirmationToken result = confirmationService.validateConfirmationToken(TOKEN);

            assertThat(result).isSameAs(expected);
        }

        @Test
        void givenJwt_whenValidateConfirmationToken_thenDelegatesToConfirmationTokenService() {
            when(confirmationTokenService.validate(TOKEN)).thenReturn(ConfirmationToken.builder().build());

            confirmationService.validateConfirmationToken(TOKEN);

            verify(confirmationTokenService).validate(TOKEN);
        }

        @Test
        void givenExpiredToken_whenValidateConfirmationToken_thenPropagatesTokenExpiredException() {
            when(confirmationTokenService.validate(TOKEN)).thenThrow(new TokenExpiredException());

            assertThatExceptionOfType(TokenExpiredException.class)
                    .isThrownBy(() -> confirmationService.validateConfirmationToken(TOKEN));
        }

        @Test
        void givenInvalidToken_whenValidateConfirmationToken_thenPropagatesInvalidTokenException() {
            when(confirmationTokenService.validate(TOKEN)).thenThrow(new InvalidTokenException());

            assertThatExceptionOfType(InvalidTokenException.class)
                    .isThrownBy(() -> confirmationService.validateConfirmationToken(TOKEN));
        }

        @Test
        void givenUnknownToken_whenValidateConfirmationToken_thenPropagatesEntityNotFoundException() {
            when(confirmationTokenService.validate(TOKEN)).thenThrow(new EntityNotFoundException());

            assertThatExceptionOfType(EntityNotFoundException.class)
                    .isThrownBy(() -> confirmationService.validateConfirmationToken(TOKEN));
        }

        @Test
        void givenAlreadyUsedToken_whenValidateConfirmationToken_thenPropagatesTokenAlreadyUsedException() {
            when(confirmationTokenService.validate(TOKEN)).thenThrow(new TokenAlreadyUsedException());

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
        void givenConfirmationToken_whenGenerateSetPasswordResponse_thenDtoContainsEmail() {
            ConfirmationToken token = buildToken();
            when(setPasswordTokenJwtService.generateToken(any(), eq(EMAIL))).thenReturn("sp.jwt");

            SetPasswordTokenDto result = confirmationService.generateSetPasswordResponse(token);

            assertThat(result.email()).isEqualTo(EMAIL);
        }

        @Test
        void givenConfirmationToken_whenGenerateSetPasswordResponse_thenDtoContainsSetPasswordToken() {
            ConfirmationToken token = buildToken();
            when(setPasswordTokenJwtService.generateToken(any(), eq(EMAIL))).thenReturn("sp.jwt");

            SetPasswordTokenDto result = confirmationService.generateSetPasswordResponse(token);

            assertThat(result.setPasswordToken()).isEqualTo("sp.jwt");
        }

        @Test
        void givenConfirmationToken_whenGenerateSetPasswordResponse_thenPassesReservationIdAsClaimAndEmailAsSubject() {
            UUID reservationId = UUID.randomUUID();
            ConfirmationToken token = buildToken(reservationId);
            when(setPasswordTokenJwtService.generateToken(any(), eq(EMAIL))).thenReturn("sp.jwt");

            confirmationService.generateSetPasswordResponse(token);

            @SuppressWarnings("unchecked")
            ArgumentCaptor<Map<String, Object>> claimsCaptor = ArgumentCaptor.forClass(Map.class);
            verify(setPasswordTokenJwtService).generateToken(claimsCaptor.capture(), eq(EMAIL));
            assertThat(claimsCaptor.getValue())
                    .containsEntry("reservationId", reservationId.toString());
        }

        private ConfirmationToken buildToken() {
            return buildToken(UUID.randomUUID());
        }

        private ConfirmationToken buildToken(UUID reservationId) {
            Reservation reservation = Reservation.builder().id(reservationId).build();
            return ConfirmationToken.builder()
                    .email(EMAIL)
                    .reservation(reservation)
                    .build();
        }
    }
}
