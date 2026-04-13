package net.franzka.sgilt.core.onboarding.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import net.franzka.sgilt.core.config.ConfirmationTokenProperties;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.jwt.TokenJwtService;
import net.franzka.sgilt.core.onboarding.domain.ConfirmationToken;
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
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmationTokenServiceTest {

    private static final String JWT = "header.payload.signature";
    private static final String JTI = "test-jti-uuid";
    private static final String EMAIL = "user@example.com";
    private static final int EXPIRATION_HOURS = 24;

    @Mock
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Mock
    private TokenJwtService confirmationTokenJwtService;

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
        void givenReservation_whenCreateForReservation_thenReturnsGeneratedJwt() {
            Reservation reservation = buildReservation();
            when(confirmationTokenJwtService.generateToken(any(), eq(EMAIL))).thenReturn(JWT);
            when(confirmationTokenJwtService.extractJti(JWT)).thenReturn(JTI);
            when(confirmationTokenProperties.confirmationExpirationHours()).thenReturn(EXPIRATION_HOURS);

            String result = confirmationTokenService.createForReservation(reservation);

            assertThat(result).isEqualTo(JWT);
        }

        @Test
        void givenReservation_whenCreateForReservation_thenPassesReservationIdAndCreatedAtAsClaims() {
            Reservation reservation = buildReservation();
            when(confirmationTokenJwtService.generateToken(any(), eq(EMAIL))).thenReturn(JWT);
            when(confirmationTokenJwtService.extractJti(JWT)).thenReturn(JTI);
            when(confirmationTokenProperties.confirmationExpirationHours()).thenReturn(EXPIRATION_HOURS);

            confirmationTokenService.createForReservation(reservation);

            @SuppressWarnings("unchecked")
            ArgumentCaptor<Map<String, Object>> claimsCaptor = ArgumentCaptor.forClass(Map.class);
            verify(confirmationTokenJwtService).generateToken(claimsCaptor.capture(), eq(EMAIL));
            assertThat(claimsCaptor.getValue())
                    .containsEntry("reservationId", reservation.getId().toString())
                    .containsEntry("reservationCreatedAt", reservation.getCreatedAt().toString());
        }

        @Test
        void givenReservation_whenCreateForReservation_thenCallsExtractJtiWithReturnedJwt() {
            Reservation reservation = buildReservation();
            when(confirmationTokenJwtService.generateToken(any(), eq(EMAIL))).thenReturn(JWT);
            when(confirmationTokenJwtService.extractJti(JWT)).thenReturn(JTI);
            when(confirmationTokenProperties.confirmationExpirationHours()).thenReturn(EXPIRATION_HOURS);

            confirmationTokenService.createForReservation(reservation);

            verify(confirmationTokenJwtService).extractJti(JWT);
        }

        @Test
        void givenReservation_whenCreateForReservation_thenSavesTokenWithCorrectFields() {
            Reservation reservation = buildReservation();
            when(confirmationTokenJwtService.generateToken(any(), eq(EMAIL))).thenReturn(JWT);
            when(confirmationTokenJwtService.extractJti(JWT)).thenReturn(JTI);
            when(confirmationTokenProperties.confirmationExpirationHours()).thenReturn(EXPIRATION_HOURS);

            confirmationTokenService.createForReservation(reservation);

            ArgumentCaptor<ConfirmationToken> tokenCaptor = ArgumentCaptor.forClass(ConfirmationToken.class);
            verify(confirmationTokenRepository).save(tokenCaptor.capture());

            ConfirmationToken saved = tokenCaptor.getValue();
            assertThat(saved.getJti()).isEqualTo(JTI);
            assertThat(saved.getEmail()).isEqualTo(EMAIL);
            assertThat(saved.getReservation()).isSameAs(reservation);
            assertThat(saved.isUsed()).isFalse();
        }

        @Test
        void givenReservationAndProperties_whenCreateForReservation_thenSavesTokenWithCorrectTimestamps() {
            Reservation reservation = buildReservation();
            when(confirmationTokenJwtService.generateToken(any(), eq(EMAIL))).thenReturn(JWT);
            when(confirmationTokenJwtService.extractJti(JWT)).thenReturn(JTI);
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
        void givenExpiredToken_whenValidate_thenThrowsTokenExpiredException() {
            when(confirmationTokenJwtService.isExpired(JWT)).thenReturn(true);

            assertThatExceptionOfType(TokenExpiredException.class)
                    .isThrownBy(() -> confirmationTokenService.validate(JWT));
        }

        @Test
        void givenExpiredToken_whenValidate_thenDoesNotExtractClaims() {
            when(confirmationTokenJwtService.isExpired(JWT)).thenReturn(true);

            assertThatExceptionOfType(TokenExpiredException.class)
                    .isThrownBy(() -> confirmationTokenService.validate(JWT));

            verify(confirmationTokenJwtService, never()).extractClaims(any());
        }

        @Test
        void givenJwtException_whenValidate_thenThrowsInvalidTokenException() {
            when(confirmationTokenJwtService.isExpired(JWT)).thenReturn(false);
            when(confirmationTokenJwtService.extractClaims(JWT)).thenThrow(new JwtException("bad token"));

            assertThatExceptionOfType(InvalidTokenException.class)
                    .isThrownBy(() -> confirmationTokenService.validate(JWT));
        }

        @Test
        void givenUnknownJti_whenValidate_thenThrowsEntityNotFoundException() {
            Claims claims = mock(Claims.class);
            when(claims.getId()).thenReturn(JTI);
            when(confirmationTokenJwtService.isExpired(JWT)).thenReturn(false);
            when(confirmationTokenJwtService.extractClaims(JWT)).thenReturn(claims);
            when(confirmationTokenRepository.findByJti(JTI)).thenReturn(Optional.empty());

            assertThatExceptionOfType(EntityNotFoundException.class)
                    .isThrownBy(() -> confirmationTokenService.validate(JWT));
        }

        @Test
        void givenAlreadyUsedToken_whenValidate_thenThrowsTokenAlreadyUsedException() {
            Claims claims = mock(Claims.class);
            when(claims.getId()).thenReturn(JTI);
            ConfirmationToken usedToken = buildConfirmationToken(true, LocalDateTime.of(2024, 6, 15, 10, 0));
            when(confirmationTokenJwtService.isExpired(JWT)).thenReturn(false);
            when(confirmationTokenJwtService.extractClaims(JWT)).thenReturn(claims);
            when(confirmationTokenRepository.findByJti(JTI)).thenReturn(Optional.of(usedToken));

            assertThatExceptionOfType(TokenAlreadyUsedException.class)
                    .isThrownBy(() -> confirmationTokenService.validate(JWT));
        }

        @Test
        void givenCreatedAtMismatch_whenValidate_thenThrowsInvalidTokenException() {
            LocalDateTime claimedCreatedAt = LocalDateTime.of(2024, 6, 15, 10, 0);
            LocalDateTime actualCreatedAt = LocalDateTime.of(2024, 1, 1, 0, 0);
            Claims claims = buildClaims(JTI, claimedCreatedAt.toString());
            ConfirmationToken token = buildConfirmationToken(false, actualCreatedAt);
            when(confirmationTokenJwtService.isExpired(JWT)).thenReturn(false);
            when(confirmationTokenJwtService.extractClaims(JWT)).thenReturn(claims);
            when(confirmationTokenRepository.findByJti(JTI)).thenReturn(Optional.of(token));

            assertThatExceptionOfType(InvalidTokenException.class)
                    .isThrownBy(() -> confirmationTokenService.validate(JWT));
        }

        @Test
        void givenValidToken_whenValidate_thenReturnsConfirmationToken() {
            LocalDateTime createdAt = LocalDateTime.of(2024, 6, 15, 10, 0);
            Claims claims = buildClaims(JTI, createdAt.toString());
            ConfirmationToken expectedToken = buildConfirmationToken(false, createdAt);
            when(confirmationTokenJwtService.isExpired(JWT)).thenReturn(false);
            when(confirmationTokenJwtService.extractClaims(JWT)).thenReturn(claims);
            when(confirmationTokenRepository.findByJti(JTI)).thenReturn(Optional.of(expectedToken));

            ConfirmationToken result = confirmationTokenService.validate(JWT);

            assertThat(result).isSameAs(expectedToken);
        }

        private Claims buildClaims(String jti, String reservationCreatedAt) {
            Claims claims = mock(Claims.class);
            when(claims.getId()).thenReturn(jti);
            when(claims.get("reservationCreatedAt", String.class)).thenReturn(reservationCreatedAt);
            return claims;
        }

        private ConfirmationToken buildConfirmationToken(boolean used, LocalDateTime reservationCreatedAt) {
            Reservation reservation = Reservation.builder().createdAt(reservationCreatedAt).build();
            return ConfirmationToken.builder()
                    .jti(JTI)
                    .used(used)
                    .reservation(reservation)
                    .build();
        }
    }

    // -------------------------------------------------------------------------
    // markAsUsed
    // -------------------------------------------------------------------------

    @Nested
    class MarkAsUsed {

        @Test
        void givenToken_whenMarkAsUsed_thenSetsUsedToTrue() {
            ConfirmationToken token = ConfirmationToken.builder().used(false).build();

            confirmationTokenService.markAsUsed(token);

            assertThat(token.isUsed()).isTrue();
        }

        @Test
        void givenToken_whenMarkAsUsed_thenSavesToken() {
            ConfirmationToken token = ConfirmationToken.builder().used(false).build();

            confirmationTokenService.markAsUsed(token);

            verify(confirmationTokenRepository).save(token);
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
