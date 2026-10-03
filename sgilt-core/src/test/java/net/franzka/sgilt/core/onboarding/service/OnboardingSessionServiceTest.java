package net.franzka.sgilt.core.onboarding.service;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import net.franzka.sgilt.core.config.ConfirmationTokenProperties;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.dto.DemandeInitieeDto;
import net.franzka.sgilt.core.evenement.dto.EvenementDto;
import net.franzka.sgilt.core.evenement.dto.RubriqueDto;
import net.franzka.sgilt.core.evenement.service.EvenementService;
import net.franzka.sgilt.core.jwt.service.VerificationTokenHmacService;
import net.franzka.sgilt.core.onboarding.domain.Onboarding;
import net.franzka.sgilt.core.onboarding.domain.OnboardingState;
import net.franzka.sgilt.core.onboarding.dto.InitOnboardingRequest;
import net.franzka.sgilt.core.onboarding.exception.InvalidTokenException;
import net.franzka.sgilt.core.onboarding.exception.TokenAlreadyUsedException;
import net.franzka.sgilt.core.onboarding.exception.TokenExpiredException;
import net.franzka.sgilt.core.onboarding.repository.OnboardingRepository;
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
    private static final LocalDate DATE          = LocalDate.of(2025, 6, 15);
    private static final UUID   DJ_ID            = UUID.randomUUID();
    private static final UUID   TRAITEUR_ID      = UUID.randomUUID();

    @Mock private OnboardingRepository onboardingRepository;
    @Mock private VerificationTokenHmacService verificationTokenHmacService;
    @Mock private ConfirmationTokenProperties confirmationTokenProperties;
    @Mock private UtilisateurService utilisateurService;
    @Mock private EvenementService evenementService;
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
            when(verificationTokenHmacService.generate())
                    .thenReturn(new VerificationTokenHmacService.GeneratedToken(PAYLOAD, TOKEN));
            when(confirmationTokenProperties.confirmationExpirationHours()).thenReturn(EXPIRATION_HOURS);
            when(objectMapper.writeValueAsString(any())).thenReturn("{}");

            OnboardingSessionService.InitiationResult result =
                    onboardingSessionService.initiate(EMAIL, buildRequest());

            assertThat(result.hmacToken()).isEqualTo(TOKEN);
        }

        @Test
        void givenValidRequest_whenInitiate_thenSavesOnboardingWithPayload() throws JacksonException {
            when(verificationTokenHmacService.generate())
                    .thenReturn(new VerificationTokenHmacService.GeneratedToken(PAYLOAD, TOKEN));
            when(confirmationTokenProperties.confirmationExpirationHours()).thenReturn(EXPIRATION_HOURS);
            when(objectMapper.writeValueAsString(any())).thenReturn("{}");

            onboardingSessionService.initiate(EMAIL, buildRequest());

            ArgumentCaptor<Onboarding> captor = ArgumentCaptor.forClass(Onboarding.class);
            verify(onboardingRepository).save(captor.capture());
            assertThat(captor.getValue().getHmacPayload()).isEqualTo(PAYLOAD);
        }

        @Test
        void givenValidRequest_whenInitiate_thenSavesOnboardingWithEmail() throws JacksonException {
            when(verificationTokenHmacService.generate())
                    .thenReturn(new VerificationTokenHmacService.GeneratedToken(PAYLOAD, TOKEN));
            when(confirmationTokenProperties.confirmationExpirationHours()).thenReturn(EXPIRATION_HOURS);
            when(objectMapper.writeValueAsString(any())).thenReturn("{}");
            onboardingSessionService.initiate(EMAIL, buildRequest());

            ArgumentCaptor<Onboarding> captor = ArgumentCaptor.forClass(Onboarding.class);
            verify(onboardingRepository).save(captor.capture());
            assertThat(captor.getValue().getEmail()).isEqualTo(EMAIL);
        }

        @Test
        void givenProperties_whenInitiate_thenSavesOnboardingWithCorrectExpiry() throws JacksonException {
            when(verificationTokenHmacService.generate())
                    .thenReturn(new VerificationTokenHmacService.GeneratedToken(PAYLOAD, TOKEN));
            when(confirmationTokenProperties.confirmationExpirationHours()).thenReturn(EXPIRATION_HOURS);
            when(objectMapper.writeValueAsString(any())).thenReturn("{}");

            LocalDateTime before = LocalDateTime.now();
            onboardingSessionService.initiate(EMAIL, buildRequest());
            LocalDateTime after = LocalDateTime.now();

            ArgumentCaptor<Onboarding> captor = ArgumentCaptor.forClass(Onboarding.class);
            verify(onboardingRepository).save(captor.capture());
            assertThat(captor.getValue().getExpiresAt())
                    .isBetween(before.plusHours(EXPIRATION_HOURS), after.plusHours(EXPIRATION_HOURS));
        }

        @Test
        void givenSerializationFailure_whenInitiate_thenThrowsRuntimeException() throws JacksonException {
            when(verificationTokenHmacService.generate())
                    .thenReturn(new VerificationTokenHmacService.GeneratedToken(PAYLOAD, TOKEN));
            when(confirmationTokenProperties.confirmationExpirationHours()).thenReturn(EXPIRATION_HOURS);
            when(objectMapper.writeValueAsString(any())).thenThrow(mock(JacksonException.class));

            assertThatExceptionOfType(RuntimeException.class)
                    .isThrownBy(() -> onboardingSessionService.initiate(EMAIL, buildRequest()));
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

        private Onboarding buildPendingOnboarding(LocalDateTime expiresAt) {
            return Onboarding.builder()
                    .hmacPayload(PAYLOAD)
                    .state(OnboardingState.PENDING_CONFIRMATION)
                    .expiresAt(expiresAt)
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
    // listPending
    // -------------------------------------------------------------------------

    @Nested
    class ListPending {

        @Test
        void givenPendingSessions_whenListPending_thenReturnsThemWithTheirEventInOrder() throws JacksonException {
            Onboarding open = Onboarding.builder().state(OnboardingState.OPEN).data("{open}").build();
            Onboarding pendingConfirmation = Onboarding.builder().state(OnboardingState.PENDING_CONFIRMATION).data("{pending}").build();
            InitOnboardingRequest openContent = buildRequest();
            InitOnboardingRequest pendingContent = buildRequest();
            when(onboardingRepository.findByStateInOrderByCreatedAtDesc(
                    List.of(OnboardingState.OPEN, OnboardingState.PENDING_CONFIRMATION)))
                    .thenReturn(List.of(pendingConfirmation, open));
            when(objectMapper.readValue("{open}", InitOnboardingRequest.class)).thenReturn(openContent);
            when(objectMapper.readValue("{pending}", InitOnboardingRequest.class)).thenReturn(pendingContent);

            assertThat(onboardingSessionService.listPending()).containsExactly(
                    new OnboardingSessionService.PendingOnboarding(pendingConfirmation, pendingContent),
                    new OnboardingSessionService.PendingOnboarding(open, openContent));
        }

        @Test
        void givenNoPendingSessions_whenListPending_thenReturnsEmptyList() {
            when(onboardingRepository.findByStateInOrderByCreatedAtDesc(
                    List.of(OnboardingState.OPEN, OnboardingState.PENDING_CONFIRMATION)))
                    .thenReturn(List.of());

            assertThat(onboardingSessionService.listPending()).isEmpty();
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
            Onboarding onboarding = Onboarding.builder().data("{}").build();
            when(objectMapper.readValue("{}", InitOnboardingRequest.class)).thenReturn(buildRequest());

            onboardingSessionService.consume(onboarding);

            verify(onboardingRepository).delete(onboarding);
        }

        @Test
        void givenOnboarding_whenConsume_thenReturnsTheStoredEvent() throws JacksonException {
            InitOnboardingRequest formData = buildRequest();
            Onboarding onboarding = Onboarding.builder().data("{}").build();
            when(objectMapper.readValue("{}", InitOnboardingRequest.class)).thenReturn(formData);

            assertThat(onboardingSessionService.consume(onboarding)).isSameAs(formData);
        }

        @Test
        void givenDeserializationFailure_whenConsume_thenThrowsRuntimeException() throws JacksonException {
            Onboarding onboarding = Onboarding.builder().data("{}").build();
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
            when(utilisateurService.createUtilisateur(any(), any(), any(), any())).thenReturn(Utilisateur.builder().build());
            when(evenementService.createFromDto(any(), any())).thenReturn(Evenement.builder().id(UUID.randomUUID()).build());

            onboardingSessionService.createEntities(buildRequest(), EMAIL);

            verify(utilisateurService).createUtilisateur(FIRSTNAME, LASTNAME, EMAIL, TELEPHONE);
        }

        @Test
        void givenFormData_whenCreateEntities_thenCreatesTheEventFromItsDto() {
            InitOnboardingRequest formData = buildRequest();
            Utilisateur utilisateur = Utilisateur.builder().build();
            when(utilisateurService.createUtilisateur(any(), any(), any(), any())).thenReturn(utilisateur);
            when(evenementService.createFromDto(any(), any())).thenReturn(Evenement.builder().build());

            onboardingSessionService.createEntities(formData, EMAIL);

            verify(evenementService).createFromDto(utilisateur, formData.evenement());
        }

        @Test
        void givenFormData_whenCreateEntities_thenReturnsTheCreatedEventId() {
            UUID eventId = UUID.randomUUID();
            when(utilisateurService.createUtilisateur(any(), any(), any(), any())).thenReturn(Utilisateur.builder().build());
            when(evenementService.createFromDto(any(), any())).thenReturn(Evenement.builder().id(eventId).build());

            assertThat(onboardingSessionService.createEntities(buildRequest(), EMAIL)).isEqualTo(eventId);
        }
    }

    // -------------------------------------------------------------------------
    // helpers
    // -------------------------------------------------------------------------

    // Événement avec une demande au DJ (musique-animation) et une au traiteur (restauration).
    private InitOnboardingRequest buildRequest() {
        return new InitOnboardingRequest(FIRSTNAME, LASTNAME, EMAIL, TELEPHONE,
                new EvenementDto("mariage", null, null, null, DATE, null, null, null, List.of(
                        new RubriqueDto("restauration",
                                List.of(new DemandeInitieeDto(TRAITEUR_ID, "Pour 80 personnes ?"))),
                        new RubriqueDto("musique-animation",
                                List.of(new DemandeInitieeDto(DJ_ID, "Pour la soirée ?"))),
                        new RubriqueDto("lieu", List.of()))));
    }
}
