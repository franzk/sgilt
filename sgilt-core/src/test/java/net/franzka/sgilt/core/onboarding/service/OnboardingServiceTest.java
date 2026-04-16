package net.franzka.sgilt.core.onboarding.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.service.EvenementService;
import net.franzka.sgilt.core.jwt.TokenJwtService;
import net.franzka.sgilt.core.onboarding.dto.ConfirmAccountRequest;
import net.franzka.sgilt.core.onboarding.dto.ConfirmAccountResponse;
import net.franzka.sgilt.core.onboarding.dto.DemandeInitialeRequest;
import net.franzka.sgilt.core.onboarding.dto.DemandeInitialeResponse;
import net.franzka.sgilt.core.onboarding.exception.InvalidTokenException;
import net.franzka.sgilt.core.onboarding.exception.TokenExpiredException;
import net.franzka.sgilt.core.onboarding.mailer.OnboardingMailerService;
import net.franzka.sgilt.core.reservation.domain.Reservation;
import net.franzka.sgilt.core.reservation.service.ReservationService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import org.mockito.InOrder;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OnboardingServiceTest {

    private static final String FIRSTNAME      = "Jean";
    private static final String LASTNAME       = "Dupont";
    private static final String EMAIL          = "jean.dupont@example.com";
    private static final UUID   PRESTATAIRE_ID = UUID.randomUUID();
    private static final String SP_TOKEN       = "sp.header.payload.signature";

    @Mock
    private EvenementService evenementService;

    @Mock
    private ReservationService reservationService;

    @Mock
    private ConfirmationTokenService confirmationTokenService;

    @Mock
    private TokenJwtService setPasswordTokenJwtService;

    @Mock
    private OnboardingMailerService onboardingMailerService;

    @InjectMocks
    private OnboardingService onboardingService;

    // -------------------------------------------------------------------------
    // createDemandeReservation
    // -------------------------------------------------------------------------

    @Nested
    class CreateDemandeReservation {

        @Test
        void givenRequest_whenCreateDemandeReservation_thenCancelsExistingTokenForEmail() {
            DemandeInitialeRequest request = buildRequest();
            Evenement evenement = Evenement.builder().email(EMAIL).build();
            Reservation reservation = Reservation.builder().build();
            when(evenementService.createDraft(FIRSTNAME, LASTNAME, EMAIL)).thenReturn(evenement);
            when(reservationService.createDraft(evenement, PRESTATAIRE_ID)).thenReturn(reservation);
            when(confirmationTokenService.createForReservation(reservation)).thenReturn("jwt");

            onboardingService.createDemandeReservation(request);

            verify(confirmationTokenService).cancelExistingTokenForEmail(EMAIL);
        }

        @Test
        void givenRequest_whenCreateDemandeReservation_thenCancelsTokenBeforeCreatingEvenement() {
            DemandeInitialeRequest request = buildRequest();
            Evenement evenement = Evenement.builder().email(EMAIL).build();
            Reservation reservation = Reservation.builder().build();
            when(evenementService.createDraft(FIRSTNAME, LASTNAME, EMAIL)).thenReturn(evenement);
            when(reservationService.createDraft(evenement, PRESTATAIRE_ID)).thenReturn(reservation);
            when(confirmationTokenService.createForReservation(reservation)).thenReturn("jwt");

            InOrder inOrder = inOrder(confirmationTokenService, evenementService);
            onboardingService.createDemandeReservation(request);

            inOrder.verify(confirmationTokenService).cancelExistingTokenForEmail(EMAIL);
            inOrder.verify(evenementService).createDraft(FIRSTNAME, LASTNAME, EMAIL);
        }

        @Test
        void givenRequest_whenCreateDemandeReservation_thenCreatesEvenementDraftWithRequestFields() {
            DemandeInitialeRequest request = buildRequest();
            Evenement evenement = Evenement.builder().email(EMAIL).build();
            Reservation reservation = Reservation.builder().build();
            when(evenementService.createDraft(FIRSTNAME, LASTNAME, EMAIL)).thenReturn(evenement);
            when(reservationService.createDraft(evenement, PRESTATAIRE_ID)).thenReturn(reservation);
            when(confirmationTokenService.createForReservation(reservation)).thenReturn("jwt");

            onboardingService.createDemandeReservation(request);

            verify(evenementService).createDraft(FIRSTNAME, LASTNAME, EMAIL);
        }

        @Test
        void givenRequest_whenCreateDemandeReservation_thenCreatesReservationDraftWithCreatedEvenementAndPrestataireId() {
            DemandeInitialeRequest request = buildRequest();
            Evenement evenement = Evenement.builder().email(EMAIL).build();
            Reservation reservation = Reservation.builder().build();
            when(evenementService.createDraft(FIRSTNAME, LASTNAME, EMAIL)).thenReturn(evenement);
            when(reservationService.createDraft(evenement, PRESTATAIRE_ID)).thenReturn(reservation);
            when(confirmationTokenService.createForReservation(reservation)).thenReturn("jwt");

            onboardingService.createDemandeReservation(request);

            verify(reservationService).createDraft(evenement, PRESTATAIRE_ID);
        }

        @Test
        void givenRequest_whenCreateDemandeReservation_thenCreatesConfirmationTokenForReservation() {
            DemandeInitialeRequest request = buildRequest();
            Evenement evenement = Evenement.builder().email(EMAIL).build();
            Reservation reservation = Reservation.builder().build();
            when(evenementService.createDraft(FIRSTNAME, LASTNAME, EMAIL)).thenReturn(evenement);
            when(reservationService.createDraft(evenement, PRESTATAIRE_ID)).thenReturn(reservation);
            when(confirmationTokenService.createForReservation(reservation)).thenReturn("jwt");

            onboardingService.createDemandeReservation(request);

            verify(confirmationTokenService).createForReservation(reservation);
        }

        @Test
        void givenRequest_whenCreateDemandeReservation_thenSendsConfirmationEmailWithEmailAndJwt() {
            DemandeInitialeRequest request = buildRequest();
            Evenement evenement = Evenement.builder().email(EMAIL).build();
            Reservation reservation = Reservation.builder().build();
            when(evenementService.createDraft(FIRSTNAME, LASTNAME, EMAIL)).thenReturn(evenement);
            when(reservationService.createDraft(evenement, PRESTATAIRE_ID)).thenReturn(reservation);
            when(confirmationTokenService.createForReservation(reservation)).thenReturn("confirmation.jwt");

            onboardingService.createDemandeReservation(request);

            verify(onboardingMailerService).sendConfirmationEmail(EMAIL, "confirmation.jwt");
        }

        @Test
        void givenRequest_whenCreateDemandeReservation_thenReturnsResponseWithEmail() {
            DemandeInitialeRequest request = buildRequest();
            Evenement evenement = Evenement.builder().email(EMAIL).build();
            Reservation reservation = Reservation.builder().build();
            when(evenementService.createDraft(FIRSTNAME, LASTNAME, EMAIL)).thenReturn(evenement);
            when(reservationService.createDraft(evenement, PRESTATAIRE_ID)).thenReturn(reservation);
            when(confirmationTokenService.createForReservation(reservation)).thenReturn("jwt");

            DemandeInitialeResponse response = onboardingService.createDemandeReservation(request);

            assertThat(response.email()).isEqualTo(EMAIL);
        }

        private DemandeInitialeRequest buildRequest() {
            return new DemandeInitialeRequest(FIRSTNAME, LASTNAME, EMAIL, PRESTATAIRE_ID);
        }
    }

    // -------------------------------------------------------------------------
    // confirmAccount
    // -------------------------------------------------------------------------

    @Nested
    class ConfirmAccount {

        @Test
        void givenExpiredToken_whenConfirmAccount_thenThrowsTokenExpiredException() {
            when(setPasswordTokenJwtService.isExpired(SP_TOKEN)).thenReturn(true);

            assertThatExceptionOfType(TokenExpiredException.class)
                    .isThrownBy(() -> onboardingService.confirmAccount(buildRequest()));
        }

        @Test
        void givenExpiredToken_whenConfirmAccount_thenDoesNotExtractClaims() {
            when(setPasswordTokenJwtService.isExpired(SP_TOKEN)).thenReturn(true);

            assertThatExceptionOfType(TokenExpiredException.class)
                    .isThrownBy(() -> onboardingService.confirmAccount(buildRequest()));

            verify(setPasswordTokenJwtService, never()).extractClaims(any());
        }

        @Test
        void givenJwtException_whenConfirmAccount_thenThrowsInvalidTokenException() {
            when(setPasswordTokenJwtService.isExpired(SP_TOKEN)).thenReturn(false);
            when(setPasswordTokenJwtService.extractClaims(SP_TOKEN)).thenThrow(new JwtException("bad token"));

            assertThatExceptionOfType(InvalidTokenException.class)
                    .isThrownBy(() -> onboardingService.confirmAccount(buildRequest()));
        }

        @Test
        void givenValidToken_whenConfirmAccount_thenActivatesReservationWithIdFromClaims() {
            UUID reservationId = UUID.randomUUID();
            Claims claims = buildClaims(reservationId, EMAIL);
            when(setPasswordTokenJwtService.isExpired(SP_TOKEN)).thenReturn(false);
            when(setPasswordTokenJwtService.extractClaims(SP_TOKEN)).thenReturn(claims);

            onboardingService.confirmAccount(buildRequest());

            verify(reservationService).activateDemande(reservationId);
        }

        @Test
        void givenValidToken_whenConfirmAccount_thenDeletesConfirmationTokenForReservation() {
            UUID reservationId = UUID.randomUUID();
            Claims claims = buildClaims(reservationId, EMAIL);
            when(setPasswordTokenJwtService.isExpired(SP_TOKEN)).thenReturn(false);
            when(setPasswordTokenJwtService.extractClaims(SP_TOKEN)).thenReturn(claims);

            onboardingService.confirmAccount(buildRequest());

            verify(confirmationTokenService).deleteByReservation(reservationId);
        }

        @Test
        void givenValidToken_whenConfirmAccount_thenSendsWelcomeEmailToSubject() {
            UUID reservationId = UUID.randomUUID();
            Claims claims = buildClaims(reservationId, EMAIL);
            when(setPasswordTokenJwtService.isExpired(SP_TOKEN)).thenReturn(false);
            when(setPasswordTokenJwtService.extractClaims(SP_TOKEN)).thenReturn(claims);

            onboardingService.confirmAccount(buildRequest());

            verify(onboardingMailerService).sendWelcomeEmail(EMAIL);
        }

        @Test
        void givenValidToken_whenConfirmAccount_thenReturnsResponse() {
            UUID reservationId = UUID.randomUUID();
            Claims claims = buildClaims(reservationId, EMAIL);
            when(setPasswordTokenJwtService.isExpired(SP_TOKEN)).thenReturn(false);
            when(setPasswordTokenJwtService.extractClaims(SP_TOKEN)).thenReturn(claims);

            ConfirmAccountResponse response = onboardingService.confirmAccount(buildRequest());

            assertThat(response).isNotNull();
            assertThat(response.accessToken()).isNotBlank();
            assertThat(response.refreshToken()).isNotBlank();
        }

        private ConfirmAccountRequest buildRequest() {
            return new ConfirmAccountRequest(SP_TOKEN, "p@ssw0rd!");
        }

        private Claims buildClaims(UUID reservationId, String subject) {
            Claims claims = mock(Claims.class);
            when(claims.get("reservationId", String.class)).thenReturn(reservationId.toString());
            when(claims.getSubject()).thenReturn(subject);
            return claims;
        }
    }
}
