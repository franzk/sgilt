package net.franzka.sgilt.core.onboarding.service;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import net.franzka.sgilt.core.config.ConfirmationTokenProperties;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.service.EvenementService;
import net.franzka.sgilt.core.jwt.VerificationTokenHmacService;
import net.franzka.sgilt.core.onboarding.domain.Onboarding;
import net.franzka.sgilt.core.onboarding.domain.OnboardingState;
import net.franzka.sgilt.core.onboarding.dto.InitOnboardingRequest;
import net.franzka.sgilt.core.onboarding.exception.InvalidTokenException;
import net.franzka.sgilt.core.onboarding.exception.TokenAlreadyUsedException;
import net.franzka.sgilt.core.onboarding.exception.TokenExpiredException;
import net.franzka.sgilt.core.onboarding.repository.OnboardingRepository;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.reservation.service.ReservationService;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import net.franzka.sgilt.core.utilisateur.service.UtilisateurService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OnboardingSessionServiceTest {

    private static final String PAYLOAD          = "testpayload123456789";
    private static final String TOKEN            = PAYLOAD + "-" + "a".repeat(64);
    private static final String EMAIL            = "user@example.com";
    private static final String FIRSTNAME        = "Jean";
    private static final String LASTNAME         = "Dupont";
    private static final String TELEPHONE        = "0612345678";
    private static final int    EXPIRATION_HOURS = 24;

    @Mock private OnboardingRepository onboardingRepository;
    @Mock private VerificationTokenHmacService verificationTokenHmacService;
    @Mock private ConfirmationTokenProperties confirmationTokenProperties;
    @Mock private UtilisateurService utilisateurService;
    @Mock private EvenementService evenementService;
    @Mock private ReservationService reservationService;
    @Mock private ObjectMapper objectMapper;

    @InjectMocks
    private OnboardingSessionService onboardingSessionService;

    // -------------------------------------------------------------------------
    // initiate
    // -------------------------------------------------------------------------

    @Nested
    class Initiate {

        @Test
        void givenValidRequest_whenInitiate_thenReturnsHmacToken() throws JacksonException {
            when(verificationTokenHmacService.generateToken()).thenReturn(TOKEN);
            when(confirmationTokenProperties.confirmationExpirationHours()).thenReturn(EXPIRATION_HOURS);
            when(objectMapper.writeValueAsString(any())).thenReturn("{}");

            OnboardingSessionService.InitiationResult result =
                    onboardingSessionService.initiate(EMAIL, buildPrestataire(), buildRequest());

            assertThat(result.hmacToken()).isEqualTo(TOKEN);
        }

        @Test
        void givenValidRequest_whenInitiate_thenSavesOnboardingWithPayload() throws JacksonException {
            when(verificationTokenHmacService.generateToken()).thenReturn(TOKEN);
            when(confirmationTokenProperties.confirmationExpirationHours()).thenReturn(EXPIRATION_HOURS);
            when(objectMapper.writeValueAsString(any())).thenReturn("{}");

            onboardingSessionService.initiate(EMAIL, buildPrestataire(), buildRequest());

            ArgumentCaptor<Onboarding> captor = ArgumentCaptor.forClass(Onboarding.class);
            verify(onboardingRepository).save(captor.capture());
            assertThat(captor.getValue().getHmacPayload()).isEqualTo(PAYLOAD);
        }

        @Test
        void givenValidRequest_whenInitiate_thenSavesOnboardingWithEmailAndPrestataire() throws JacksonException {
            when(verificationTokenHmacService.generateToken()).thenReturn(TOKEN);
            when(confirmationTokenProperties.confirmationExpirationHours()).thenReturn(EXPIRATION_HOURS);
            when(objectMapper.writeValueAsString(any())).thenReturn("{}");
            Prestataire prestataire = buildPrestataire();

            onboardingSessionService.initiate(EMAIL, prestataire, buildRequest());

            ArgumentCaptor<Onboarding> captor = ArgumentCaptor.forClass(Onboarding.class);
            verify(onboardingRepository).save(captor.capture());
            assertThat(captor.getValue().getEmail()).isEqualTo(EMAIL);
            assertThat(captor.getValue().getPrestataire()).isSameAs(prestataire);
        }

        @Test
        void givenProperties_whenInitiate_thenSavesOnboardingWithCorrectExpiry() throws JacksonException {
            when(verificationTokenHmacService.generateToken()).thenReturn(TOKEN);
            when(confirmationTokenProperties.confirmationExpirationHours()).thenReturn(EXPIRATION_HOURS);
            when(objectMapper.writeValueAsString(any())).thenReturn("{}");

            LocalDateTime before = LocalDateTime.now();
            onboardingSessionService.initiate(EMAIL, buildPrestataire(), buildRequest());
            LocalDateTime after = LocalDateTime.now();

            ArgumentCaptor<Onboarding> captor = ArgumentCaptor.forClass(Onboarding.class);
            verify(onboardingRepository).save(captor.capture());
            assertThat(captor.getValue().getExpiresAt())
                    .isBetween(before.plusHours(EXPIRATION_HOURS), after.plusHours(EXPIRATION_HOURS));
        }

        @Test
        void givenSerializationFailure_whenInitiate_thenThrowsRuntimeException() throws JacksonException {
            when(verificationTokenHmacService.generateToken()).thenReturn(TOKEN);
            when(confirmationTokenProperties.confirmationExpirationHours()).thenReturn(EXPIRATION_HOURS);
            when(objectMapper.writeValueAsString(any())).thenThrow(mock(JacksonException.class));

            assertThatExceptionOfType(RuntimeException.class)
                    .isThrownBy(() -> onboardingSessionService.initiate(EMAIL, buildPrestataire(), buildRequest()));
        }
    }

    // -------------------------------------------------------------------------
    // checkToken
    // -------------------------------------------------------------------------

    @Nested
    class CheckToken {

        @Test
        void givenInvalidHmac_whenCheckToken_thenThrowsInvalidTokenException() {
            when(verificationTokenHmacService.verify(TOKEN)).thenThrow(new InvalidTokenException());

            assertThatExceptionOfType(InvalidTokenException.class)
                    .isThrownBy(() -> onboardingSessionService.checkToken(TOKEN));
        }

        @Test
        void givenUnknownPayload_whenCheckToken_thenThrowsEntityNotFoundException() {
            when(verificationTokenHmacService.verify(TOKEN)).thenReturn(PAYLOAD);
            when(onboardingRepository.findByHmacPayload(PAYLOAD)).thenReturn(Optional.empty());

            assertThatExceptionOfType(EntityNotFoundException.class)
                    .isThrownBy(() -> onboardingSessionService.checkToken(TOKEN));
        }

        @Test
        void givenUsedSession_whenCheckToken_thenThrowsTokenAlreadyUsedException() {
            Onboarding onboarding = buildOnboarding(OnboardingState.USED, LocalDateTime.now().plusHours(1));
            when(verificationTokenHmacService.verify(TOKEN)).thenReturn(PAYLOAD);
            when(onboardingRepository.findByHmacPayload(PAYLOAD)).thenReturn(Optional.of(onboarding));

            assertThatExceptionOfType(TokenAlreadyUsedException.class)
                    .isThrownBy(() -> onboardingSessionService.checkToken(TOKEN));
        }

        @Test
        void givenCancelledSession_whenCheckToken_thenThrowsTokenAlreadyUsedException() {
            Onboarding onboarding = buildOnboarding(OnboardingState.CANCELLED, LocalDateTime.now().plusHours(1));
            when(verificationTokenHmacService.verify(TOKEN)).thenReturn(PAYLOAD);
            when(onboardingRepository.findByHmacPayload(PAYLOAD)).thenReturn(Optional.of(onboarding));

            assertThatExceptionOfType(TokenAlreadyUsedException.class)
                    .isThrownBy(() -> onboardingSessionService.checkToken(TOKEN));
        }

        @Test
        void givenExpiredOpenSession_whenCheckToken_thenThrowsTokenExpiredException() {
            Onboarding onboarding = buildOnboarding(OnboardingState.OPEN, LocalDateTime.now().minusSeconds(1));
            when(verificationTokenHmacService.verify(TOKEN)).thenReturn(PAYLOAD);
            when(onboardingRepository.findByHmacPayload(PAYLOAD)).thenReturn(Optional.of(onboarding));

            assertThatExceptionOfType(TokenExpiredException.class)
                    .isThrownBy(() -> onboardingSessionService.checkToken(TOKEN));
        }

        @Test
        void givenOpenSession_whenCheckToken_thenReturnsOnboarding() {
            Onboarding onboarding = buildOnboarding(OnboardingState.OPEN, LocalDateTime.now().plusHours(1));
            when(verificationTokenHmacService.verify(TOKEN)).thenReturn(PAYLOAD);
            when(onboardingRepository.findByHmacPayload(PAYLOAD)).thenReturn(Optional.of(onboarding));

            Onboarding result = onboardingSessionService.checkToken(TOKEN);

            assertThat(result).isSameAs(onboarding);
        }

        @Test
        void givenPendingSessionWithinGracePeriod_whenCheckToken_thenReturnsOnboarding() {
            Onboarding onboarding = buildPendingOnboarding(LocalDateTime.now().plusMinutes(4));
            when(verificationTokenHmacService.verify(TOKEN)).thenReturn(PAYLOAD);
            when(onboardingRepository.findByHmacPayload(PAYLOAD)).thenReturn(Optional.of(onboarding));

            Onboarding result = onboardingSessionService.checkToken(TOKEN);

            assertThat(result).isSameAs(onboarding);
        }

        @Test
        void givenPendingSessionAfterGracePeriod_whenCheckToken_thenThrowsTokenExpiredException() {
            Onboarding onboarding = buildPendingOnboarding(LocalDateTime.now().minusSeconds(1));
            when(verificationTokenHmacService.verify(TOKEN)).thenReturn(PAYLOAD);
            when(onboardingRepository.findByHmacPayload(PAYLOAD)).thenReturn(Optional.of(onboarding));

            assertThatExceptionOfType(TokenExpiredException.class)
                    .isThrownBy(() -> onboardingSessionService.checkToken(TOKEN));
        }

        private Onboarding buildOnboarding(OnboardingState state, LocalDateTime expiresAt) {
            return Onboarding.builder()
                    .hmacPayload(PAYLOAD)
                    .state(state)
                    .expiresAt(expiresAt)
                    .build();
        }

        private Onboarding buildPendingOnboarding(LocalDateTime confirmationPeriodExpiresAt) {
            return Onboarding.builder()
                    .hmacPayload(PAYLOAD)
                    .state(OnboardingState.PENDING_CONFIRMATION)
                    .confirmationPeriodExpiresAt(confirmationPeriodExpiresAt)
                    .build();
        }
    }

    // -------------------------------------------------------------------------
    // advanceToConfirmation
    // -------------------------------------------------------------------------

    @Nested
    class AdvanceToConfirmation {

        @Test
        void givenOpenSession_whenAdvanceToConfirmation_thenSetsStateToPendingConfirmation() {
            Onboarding onboarding = buildOpenOnboarding();

            onboardingSessionService.advanceToConfirmation(onboarding);

            assertThat(onboarding.getState()).isEqualTo(OnboardingState.PENDING_CONFIRMATION);
        }

        @Test
        void givenOpenSession_whenAdvanceToConfirmation_thenSetsConfirmationPeriodExpiresAt() {
            Onboarding onboarding = buildOpenOnboarding();

            LocalDateTime before = LocalDateTime.now();
            onboardingSessionService.advanceToConfirmation(onboarding);
            LocalDateTime after = LocalDateTime.now();

            assertThat(onboarding.getConfirmationPeriodExpiresAt())
                    .isBetween(before.plusMinutes(5), after.plusMinutes(5));
        }

        @Test
        void givenOpenSession_whenAdvanceToConfirmation_thenSavesOnboarding() {
            Onboarding onboarding = buildOpenOnboarding();

            onboardingSessionService.advanceToConfirmation(onboarding);

            verify(onboardingRepository).save(onboarding);
        }

        @Test
        void givenPendingSession_whenAdvanceToConfirmation_thenDoesNotSave() {
            Onboarding onboarding = Onboarding.builder()
                    .state(OnboardingState.PENDING_CONFIRMATION)
                    .confirmationPeriodExpiresAt(LocalDateTime.now().plusMinutes(4))
                    .build();

            onboardingSessionService.advanceToConfirmation(onboarding);

            verify(onboardingRepository, never()).save(any());
        }

        private Onboarding buildOpenOnboarding() {
            return Onboarding.builder()
                    .state(OnboardingState.OPEN)
                    .expiresAt(LocalDateTime.now().plusHours(1))
                    .build();
        }
    }

    // -------------------------------------------------------------------------
    // cancelExistingForEmail
    // -------------------------------------------------------------------------

    @Nested
    class CancelExistingForEmail {

        @Test
        void givenOpenSession_whenCancelExistingForEmail_thenSetsStateToCancelled() {
            Onboarding onboarding = Onboarding.builder().state(OnboardingState.OPEN).build();
            when(onboardingRepository.findByEmailAndState(EMAIL, OnboardingState.OPEN))
                    .thenReturn(List.of(onboarding));

            onboardingSessionService.cancelExistingForEmail(EMAIL);

            assertThat(onboarding.getState()).isEqualTo(OnboardingState.CANCELLED);
        }

        @Test
        void givenOpenSession_whenCancelExistingForEmail_thenSavesOnboarding() {
            Onboarding onboarding = Onboarding.builder().state(OnboardingState.OPEN).build();
            when(onboardingRepository.findByEmailAndState(EMAIL, OnboardingState.OPEN))
                    .thenReturn(List.of(onboarding));

            onboardingSessionService.cancelExistingForEmail(EMAIL);

            verify(onboardingRepository).save(onboarding);
        }

        @Test
        void givenNoOpenSession_whenCancelExistingForEmail_thenDoesNotSave() {
            when(onboardingRepository.findByEmailAndState(EMAIL, OnboardingState.OPEN))
                    .thenReturn(List.of());

            onboardingSessionService.cancelExistingForEmail(EMAIL);

            verify(onboardingRepository, never()).save(any());
        }
    }

    // -------------------------------------------------------------------------
    // findById
    // -------------------------------------------------------------------------

    @Nested
    class FindById {

        @Test
        void givenExistingId_whenFindById_thenReturnsOnboarding() {
            UUID id = UUID.randomUUID();
            Onboarding onboarding = Onboarding.builder().id(id).build();
            when(onboardingRepository.findById(id)).thenReturn(Optional.of(onboarding));

            Onboarding result = onboardingSessionService.findById(id);

            assertThat(result).isSameAs(onboarding);
        }

        @Test
        void givenUnknownId_whenFindById_thenThrowsEntityNotFoundException() {
            UUID id = UUID.randomUUID();
            when(onboardingRepository.findById(id)).thenReturn(Optional.empty());

            assertThatExceptionOfType(EntityNotFoundException.class)
                    .isThrownBy(() -> onboardingSessionService.findById(id));
        }
    }

    // -------------------------------------------------------------------------
    // consume
    // -------------------------------------------------------------------------

    @Nested
    class Consume {

        @Test
        void givenOnboarding_whenConsume_thenDeletesOnboarding() throws JacksonException {
            Prestataire prestataire = buildPrestataire();
            InitOnboardingRequest formData = buildRequest();
            Onboarding onboarding = Onboarding.builder()
                    .data("{}")
                    .prestataire(prestataire)
                    .build();
            when(objectMapper.readValue("{}", InitOnboardingRequest.class)).thenReturn(formData);

            onboardingSessionService.consume(onboarding);

            verify(onboardingRepository).delete(onboarding);
        }

        @Test
        void givenOnboarding_whenConsume_thenReturnsFormDataAndPrestataire() throws JacksonException {
            Prestataire prestataire = buildPrestataire();
            InitOnboardingRequest formData = buildRequest();
            Onboarding onboarding = Onboarding.builder()
                    .data("{}")
                    .prestataire(prestataire)
                    .build();
            when(objectMapper.readValue("{}", InitOnboardingRequest.class)).thenReturn(formData);

            OnboardingSessionService.OnboardingContent result = onboardingSessionService.consume(onboarding);

            assertThat(result.formData()).isSameAs(formData);
            assertThat(result.prestataire()).isSameAs(prestataire);
        }

        @Test
        void givenDeserializationFailure_whenConsume_thenThrowsRuntimeException() throws JacksonException {
            Onboarding onboarding = Onboarding.builder()
                    .data("{}")
                    .prestataire(buildPrestataire())
                    .build();
            when(objectMapper.readValue("{}", InitOnboardingRequest.class)).thenThrow(mock(JacksonException.class));

            assertThatExceptionOfType(RuntimeException.class)
                    .isThrownBy(() -> onboardingSessionService.consume(onboarding));
        }
    }

    // -------------------------------------------------------------------------
    // createEntities
    // -------------------------------------------------------------------------

    @Nested
    class CreateEntities {

        @Test
        void givenFormData_whenCreateEntities_thenCreatesUtilisateur() {
            InitOnboardingRequest formData = buildRequest();
            Prestataire prestataire = buildPrestataire();
            when(utilisateurService.createUtilisateur(any(), any(), any(), any()))
                    .thenReturn(Utilisateur.builder().build());
            when(evenementService.createFromFormData(any(), any()))
                    .thenReturn(Evenement.builder().build());

            onboardingSessionService.createEntities(formData, prestataire, EMAIL);

            verify(utilisateurService).createUtilisateur(FIRSTNAME, LASTNAME, EMAIL, TELEPHONE);
        }

        @Test
        void givenFormData_whenCreateEntities_thenCreatesEvenementForUtilisateur() {
            InitOnboardingRequest formData = buildRequest();
            Prestataire prestataire = buildPrestataire();
            Utilisateur utilisateur = Utilisateur.builder().build();
            when(utilisateurService.createUtilisateur(any(), any(), any(), any())).thenReturn(utilisateur);
            when(evenementService.createFromFormData(any(), any())).thenReturn(Evenement.builder().build());

            onboardingSessionService.createEntities(formData, prestataire, EMAIL);

            verify(evenementService).createFromFormData(utilisateur, formData);
        }

        @Test
        void givenFormData_whenCreateEntities_thenCreatesReservation() {
            InitOnboardingRequest formData = buildRequest();
            Prestataire prestataire = buildPrestataire();
            Utilisateur utilisateur = Utilisateur.builder().build();
            Evenement evenement = Evenement.builder().build();
            when(utilisateurService.createUtilisateur(any(), any(), any(), any())).thenReturn(utilisateur);
            when(evenementService.createFromFormData(any(), any())).thenReturn(evenement);

            onboardingSessionService.createEntities(formData, prestataire, EMAIL);

            verify(reservationService).create(
                    eq(evenement), eq(prestataire), eq(utilisateur),
                    eq(formData.date()), eq(formData.prestataireMessage()));
        }
    }

    // -------------------------------------------------------------------------
    // helpers
    // -------------------------------------------------------------------------

    private Prestataire buildPrestataire() {
        return Prestataire.builder().id(UUID.randomUUID()).build();
    }

    private InitOnboardingRequest buildRequest() {
        return new InitOnboardingRequest(
                FIRSTNAME, LASTNAME, EMAIL, UUID.randomUUID(),
                "anniversaire", null, null, null, LocalDate.of(2025, 6, 15),
                null, null, null, TELEPHONE, null);
    }
}
