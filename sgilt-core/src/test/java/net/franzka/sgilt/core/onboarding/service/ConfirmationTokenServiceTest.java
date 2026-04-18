package net.franzka.sgilt.core.onboarding.service;

import jakarta.persistence.EntityNotFoundException;
import net.franzka.sgilt.core.config.ConfirmationTokenProperties;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.jwt.ConfirmationTokenHmacService;
import net.franzka.sgilt.core.onboarding.domain.ConfirmationToken;
import net.franzka.sgilt.core.onboarding.domain.ConfirmationTokenState;
import net.franzka.sgilt.core.onboarding.exception.InvalidTokenException;
import net.franzka.sgilt.core.onboarding.exception.TokenAlreadyUsedException;
import net.franzka.sgilt.core.onboarding.exception.TokenExpiredException;
import net.franzka.sgilt.core.onboarding.repository.ConfirmationTokenRepository;
import net.franzka.sgilt.core.reservation.domain.Reservation;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmationTokenServiceTest {

    private static final String PAYLOAD = "testpayload123456789";
    private static final String TOKEN = PAYLOAD + "-" + "a".repeat(64);
    private static final String EMAIL = "user@example.com";
    private static final int EXPIRATION_HOURS = 24;

    @Mock
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Mock
    private ConfirmationTokenHmacService confirmationTokenHmacService;

    @Mock
    private ConfirmationTokenProperties confirmationTokenProperties;

    @InjectMocks
    private ConfirmationTokenService confirmationTokenService;

    // -------------------------------------------------------------------------
    // createForReservation
    // -------------------------------------------------------------------------

    @Nested
    class CreateForReservation {

        @Test
        void givenReservation_whenCreateForReservation_thenReturnsGeneratedToken() {
            Reservation reservation = buildReservation();
            when(confirmationTokenHmacService.generateToken()).thenReturn(TOKEN);
            when(confirmationTokenProperties.confirmationExpirationHours()).thenReturn(EXPIRATION_HOURS);

            String result = confirmationTokenService.createForReservation(reservation);

            assertThat(result).isEqualTo(TOKEN);
        }

        @Test
        void givenReservation_whenCreateForReservation_thenSavesTokenWithPayload() {
            Reservation reservation = buildReservation();
            when(confirmationTokenHmacService.generateToken()).thenReturn(TOKEN);
            when(confirmationTokenProperties.confirmationExpirationHours()).thenReturn(EXPIRATION_HOURS);

            confirmationTokenService.createForReservation(reservation);

            ArgumentCaptor<ConfirmationToken> tokenCaptor = ArgumentCaptor.forClass(ConfirmationToken.class);
            verify(confirmationTokenRepository).save(tokenCaptor.capture());
            assertThat(tokenCaptor.getValue().getPayload()).isEqualTo(PAYLOAD);
        }

        @Test
        void givenReservation_whenCreateForReservation_thenSavesTokenWithCorrectFields() {
            Reservation reservation = buildReservation();
            when(confirmationTokenHmacService.generateToken()).thenReturn(TOKEN);
            when(confirmationTokenProperties.confirmationExpirationHours()).thenReturn(EXPIRATION_HOURS);

            confirmationTokenService.createForReservation(reservation);

            ArgumentCaptor<ConfirmationToken> tokenCaptor = ArgumentCaptor.forClass(ConfirmationToken.class);
            verify(confirmationTokenRepository).save(tokenCaptor.capture());

            ConfirmationToken saved = tokenCaptor.getValue();
            assertThat(saved.getEmail()).isEqualTo(EMAIL);
            assertThat(saved.getReservation()).isSameAs(reservation);
            assertThat(saved.getState()).isEqualTo(ConfirmationTokenState.OPEN);
        }

        @Test
        void givenReservationAndProperties_whenCreateForReservation_thenSavesTokenWithCorrectTimestamps() {
            Reservation reservation = buildReservation();
            when(confirmationTokenHmacService.generateToken()).thenReturn(TOKEN);
            when(confirmationTokenProperties.confirmationExpirationHours()).thenReturn(EXPIRATION_HOURS);

            LocalDateTime before = LocalDateTime.now();
            confirmationTokenService.createForReservation(reservation);
            LocalDateTime after = LocalDateTime.now();

            ArgumentCaptor<ConfirmationToken> tokenCaptor = ArgumentCaptor.forClass(ConfirmationToken.class);
            verify(confirmationTokenRepository).save(tokenCaptor.capture());

            ConfirmationToken saved = tokenCaptor.getValue();
            assertThat(saved.getCreatedAt()).isBetween(before, after);
            assertThat(saved.getExpiresAt()).isBetween(
                    before.plusHours(EXPIRATION_HOURS),
                    after.plusHours(EXPIRATION_HOURS)
            );
        }

        private Reservation buildReservation() {
            Evenement evenement = Evenement.builder().email(EMAIL).build();
            return Reservation.builder()
                    .id(UUID.randomUUID())
                    .evenement(evenement)
                    .createdAt(LocalDateTime.of(2024, 6, 15, 10, 0))
                    .build();
        }
    }

    // -------------------------------------------------------------------------
    // validate
    // -------------------------------------------------------------------------

    @Nested
    class Validate {

        @Test
        void givenInvalidHmac_whenValidate_thenThrowsInvalidTokenException() {
            when(confirmationTokenHmacService.verify(TOKEN)).thenThrow(new InvalidTokenException());

            assertThatExceptionOfType(InvalidTokenException.class)
                    .isThrownBy(() -> confirmationTokenService.validate(TOKEN));
        }

        @Test
        void givenUnknownPayload_whenValidate_thenThrowsEntityNotFoundException() {
            when(confirmationTokenHmacService.verify(TOKEN)).thenReturn(PAYLOAD);
            when(confirmationTokenRepository.findByPayload(PAYLOAD)).thenReturn(Optional.empty());

            assertThatExceptionOfType(EntityNotFoundException.class)
                    .isThrownBy(() -> confirmationTokenService.validate(TOKEN));
        }

        @Test
        void givenAlreadyUsedToken_whenValidate_thenThrowsTokenAlreadyUsedException() {
            ConfirmationToken usedToken = buildConfirmationToken(ConfirmationTokenState.USED, LocalDateTime.now().plusHours(1));
            when(confirmationTokenHmacService.verify(TOKEN)).thenReturn(PAYLOAD);
            when(confirmationTokenRepository.findByPayload(PAYLOAD)).thenReturn(Optional.of(usedToken));

            assertThatExceptionOfType(TokenAlreadyUsedException.class)
                    .isThrownBy(() -> confirmationTokenService.validate(TOKEN));
        }

        @Test
        void givenCancelledToken_whenValidate_thenThrowsTokenAlreadyUsedException() {
            ConfirmationToken cancelledToken = buildConfirmationToken(ConfirmationTokenState.CANCELLED, LocalDateTime.now().plusHours(1));
            when(confirmationTokenHmacService.verify(TOKEN)).thenReturn(PAYLOAD);
            when(confirmationTokenRepository.findByPayload(PAYLOAD)).thenReturn(Optional.of(cancelledToken));

            assertThatExceptionOfType(TokenAlreadyUsedException.class)
                    .isThrownBy(() -> confirmationTokenService.validate(TOKEN));
        }

        @Test
        void givenExpiredToken_whenValidate_thenThrowsTokenExpiredException() {
            ConfirmationToken expiredToken = buildConfirmationToken(ConfirmationTokenState.OPEN, LocalDateTime.now().minusSeconds(1));
            when(confirmationTokenHmacService.verify(TOKEN)).thenReturn(PAYLOAD);
            when(confirmationTokenRepository.findByPayload(PAYLOAD)).thenReturn(Optional.of(expiredToken));

            assertThatExceptionOfType(TokenExpiredException.class)
                    .isThrownBy(() -> confirmationTokenService.validate(TOKEN));
        }

        @Test
        void givenOpenToken_whenValidate_thenReturnsConfirmationToken() {
            ConfirmationToken token = buildOpenToken(LocalDateTime.now().plusHours(1));
            when(confirmationTokenHmacService.verify(TOKEN)).thenReturn(PAYLOAD);
            when(confirmationTokenRepository.findByPayload(PAYLOAD)).thenReturn(Optional.of(token));

            ConfirmationToken result = confirmationTokenService.validate(TOKEN);

            assertThat(result).isSameAs(token);
        }

        @Test
        void givenOpenToken_whenValidate_thenSetsStateToPendingConfirmation() {
            ConfirmationToken token = buildOpenToken(LocalDateTime.now().plusHours(1));
            when(confirmationTokenHmacService.verify(TOKEN)).thenReturn(PAYLOAD);
            when(confirmationTokenRepository.findByPayload(PAYLOAD)).thenReturn(Optional.of(token));

            confirmationTokenService.validate(TOKEN);

            assertThat(token.getState()).isEqualTo(ConfirmationTokenState.PENDING_CONFIRMATION);
        }

        @Test
        void givenOpenToken_whenValidate_thenSetsConfirmationPeriodExpiresAt() {
            ConfirmationToken token = buildOpenToken(LocalDateTime.now().plusHours(1));
            when(confirmationTokenHmacService.verify(TOKEN)).thenReturn(PAYLOAD);
            when(confirmationTokenRepository.findByPayload(PAYLOAD)).thenReturn(Optional.of(token));

            LocalDateTime before = LocalDateTime.now();
            confirmationTokenService.validate(TOKEN);
            LocalDateTime after = LocalDateTime.now();

            assertThat(token.getConfirmationPeriodExpiresAt())
                    .isBetween(before.plusMinutes(5), after.plusMinutes(5));
        }

        @Test
        void givenOpenToken_whenValidate_thenSavesToken() {
            ConfirmationToken token = buildOpenToken(LocalDateTime.now().plusHours(1));
            when(confirmationTokenHmacService.verify(TOKEN)).thenReturn(PAYLOAD);
            when(confirmationTokenRepository.findByPayload(PAYLOAD)).thenReturn(Optional.of(token));

            confirmationTokenService.validate(TOKEN);

            verify(confirmationTokenRepository).save(token);
        }

        @Test
        void givenPendingConfirmationTokenWithinGracePeriod_whenValidate_thenReturnsConfirmationToken() {
            ConfirmationToken token = buildPendingConfirmationToken(LocalDateTime.now().plusMinutes(4));
            when(confirmationTokenHmacService.verify(TOKEN)).thenReturn(PAYLOAD);
            when(confirmationTokenRepository.findByPayload(PAYLOAD)).thenReturn(Optional.of(token));

            ConfirmationToken result = confirmationTokenService.validate(TOKEN);

            assertThat(result).isSameAs(token);
        }

        @Test
        void givenPendingConfirmationTokenAfterGracePeriod_whenValidate_thenThrowsTokenExpiredException() {
            ConfirmationToken token = buildPendingConfirmationToken(LocalDateTime.now().minusSeconds(1));
            when(confirmationTokenHmacService.verify(TOKEN)).thenReturn(PAYLOAD);
            when(confirmationTokenRepository.findByPayload(PAYLOAD)).thenReturn(Optional.of(token));

            assertThatExceptionOfType(TokenExpiredException.class)
                    .isThrownBy(() -> confirmationTokenService.validate(TOKEN));
        }

        private ConfirmationToken buildOpenToken(LocalDateTime expiresAt) {
            return ConfirmationToken.builder()
                    .payload(PAYLOAD)
                    .state(ConfirmationTokenState.OPEN)
                    .expiresAt(expiresAt)
                    .build();
        }

        private ConfirmationToken buildConfirmationToken(ConfirmationTokenState state, LocalDateTime expiresAt) {
            return ConfirmationToken.builder()
                    .payload(PAYLOAD)
                    .state(state)
                    .expiresAt(expiresAt)
                    .build();
        }

        private ConfirmationToken buildPendingConfirmationToken(LocalDateTime confirmationPeriodExpiresAt) {
            return ConfirmationToken.builder()
                    .payload(PAYLOAD)
                    .state(ConfirmationTokenState.PENDING_CONFIRMATION)
                    .confirmationPeriodExpiresAt(confirmationPeriodExpiresAt)
                    .build();
        }
    }

    // -------------------------------------------------------------------------
    // cancelExistingTokenForEmail
    // -------------------------------------------------------------------------

    @Nested
    class CancelExistingTokenForEmail {

        @Test
        void givenOpenToken_whenCancelExistingTokenForEmail_thenSetsStateToCancelled() {
            ConfirmationToken token = ConfirmationToken.builder().state(ConfirmationTokenState.OPEN).build();
            when(confirmationTokenRepository.findByEmailAndState(EMAIL, ConfirmationTokenState.OPEN))
                    .thenReturn(List.of(token));

            confirmationTokenService.cancelExistingTokenForEmail(EMAIL);

            assertThat(token.getState()).isEqualTo(ConfirmationTokenState.CANCELLED);
        }

        @Test
        void givenOpenToken_whenCancelExistingTokenForEmail_thenSavesToken() {
            ConfirmationToken token = ConfirmationToken.builder().state(ConfirmationTokenState.OPEN).build();
            when(confirmationTokenRepository.findByEmailAndState(EMAIL, ConfirmationTokenState.OPEN))
                    .thenReturn(List.of(token));

            confirmationTokenService.cancelExistingTokenForEmail(EMAIL);

            verify(confirmationTokenRepository).save(token);
        }

        @Test
        void givenNoOpenToken_whenCancelExistingTokenForEmail_thenDoesNotSave() {
            when(confirmationTokenRepository.findByEmailAndState(EMAIL, ConfirmationTokenState.OPEN))
                    .thenReturn(List.of());

            confirmationTokenService.cancelExistingTokenForEmail(EMAIL);

            verify(confirmationTokenRepository, never()).save(any());
        }
    }

    // -------------------------------------------------------------------------
    // deleteByReservation
    // -------------------------------------------------------------------------

    @Nested
    class DeleteByReservation {

        @Test
        void givenExistingToken_whenDeleteByReservation_thenDeletesToken() {
            UUID reservationId = UUID.randomUUID();
            ConfirmationToken token = ConfirmationToken.builder().build();
            when(confirmationTokenRepository.findByReservationId(reservationId)).thenReturn(Optional.of(token));

            confirmationTokenService.deleteByReservation(reservationId);

            verify(confirmationTokenRepository).delete(token);
        }

        @Test
        void givenNoToken_whenDeleteByReservation_thenDoesNotCallDelete() {
            UUID reservationId = UUID.randomUUID();
            when(confirmationTokenRepository.findByReservationId(reservationId)).thenReturn(Optional.empty());

            confirmationTokenService.deleteByReservation(reservationId);

            verify(confirmationTokenRepository, never()).delete(any());
        }
    }
}
