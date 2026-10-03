package net.franzka.sgilt.core.onboarding.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import net.franzka.sgilt.core.evenement.dto.DemandeInitieeDto;
import net.franzka.sgilt.core.evenement.dto.EvenementDto;
import net.franzka.sgilt.core.evenement.dto.RubriqueDto;
import net.franzka.sgilt.core.jwt.domain.ActionToken;
import net.franzka.sgilt.core.jwt.service.ActionTokenService;
import net.franzka.sgilt.core.jwt.service.TokenJwtService;
import net.franzka.sgilt.core.keycloak.KeycloakAdminService;
import net.franzka.sgilt.core.onboarding.domain.Onboarding;
import net.franzka.sgilt.core.onboarding.dto.ConfirmAccountRequest;
import net.franzka.sgilt.core.onboarding.dto.ConfirmAccountResponse;
import net.franzka.sgilt.core.onboarding.dto.InitOnboardingDemandeRequest;
import net.franzka.sgilt.core.onboarding.dto.InitOnboardingRequest;
import net.franzka.sgilt.core.onboarding.dto.InitOnboardingResponse;
import net.franzka.sgilt.core.onboarding.exception.InvalidTokenException;
import net.franzka.sgilt.core.onboarding.exception.TokenExpiredException;
import net.franzka.sgilt.core.onboarding.mailer.OnboardingMailerService;
import net.franzka.sgilt.core.prestataire.service.PrestataireService;
import net.franzka.sgilt.core.template.service.TemplateService;
import net.franzka.sgilt.core.utilisateur.service.UtilisateurService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Map;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OnboardingServiceTest {

    private static final String    FIRSTNAME      = "Jean";
    private static final String    LASTNAME       = "Dupont";
    private static final String    EMAIL          = "jean.dupont@example.com";
    private static final UUID      PRESTATAIRE_ID = UUID.randomUUID();
    private static final String    EVENT_TYPE     = "anniversaire";
    private static final LocalDate DATE           = LocalDate.of(2025, 6, 15);
    private static final String    TELEPHONE      = "0612345678";
    private static final String    SP_TOKEN       = "sp.header.payload.signature";
    private static final String    MESSAGE        = "Disponible le 15 juin ?";
    private static final List<RubriqueDto> RUBRIQUES = List.of(new RubriqueDto(
            "musique-animation", List.of(new DemandeInitieeDto(PRESTATAIRE_ID, MESSAGE))));

    @Mock private PrestataireService prestataireService;
    @Mock private OnboardingSessionService onboardingSessionService;
    @Mock private TokenJwtService setPasswordTokenJwtService;
    @Mock private OnboardingMailerService onboardingMailerService;
    @Mock private UtilisateurService utilisateurService;
    @Mock private KeycloakAdminService keycloakAdminService;
    @Mock private ActionTokenService actionTokenService;
    @Mock private TemplateService templateService;

    @InjectMocks
    private OnboardingService onboardingService;

    // -------------------------------------------------------------------------
    // initOnboardingDemande
    // -------------------------------------------------------------------------

    @Nested
    class InitOnboardingDemande {

        @Test
        void givenExistingUser_whenInitOnboardingDemande_thenSendsSecurityAlertWithoutSession() {
            when(utilisateurService.existsByEmail(EMAIL)).thenReturn(true);

            InitOnboardingResponse response = onboardingService.initOnboardingDemande(buildDemande());

            assertThat(response.email()).isEqualTo(EMAIL);
            verify(onboardingMailerService).sendSecurityAlertEmail(EMAIL);
            verify(onboardingSessionService, never()).initiate(any(), any());
        }

        @Test
        void givenNewUser_whenInitOnboardingDemande_thenCancelsExistingSessionsForEmail() {
            stubHappyPath();

            onboardingService.initOnboardingDemande(buildDemande());

            verify(onboardingSessionService).cancelExistingForEmail(EMAIL);
        }

        @Test
        void givenNewUser_whenInitOnboardingDemande_thenStoresTheEventBuiltFromTheTemplate() {
            stubHappyPath();

            onboardingService.initOnboardingDemande(buildDemande());

            ArgumentCaptor<InitOnboardingRequest> captor = ArgumentCaptor.forClass(InitOnboardingRequest.class);
            verify(onboardingSessionService).initiate(eq(EMAIL), captor.capture());
            InitOnboardingRequest stored = captor.getValue();
            assertThat(stored.firstName()).isEqualTo(FIRSTNAME);
            assertThat(stored.telephone()).isEqualTo(TELEPHONE);
            assertThat(stored.evenement().eventType()).isEqualTo(EVENT_TYPE);
            assertThat(stored.evenement().date()).isEqualTo(DATE);
            assertThat(stored.evenement().rubriques()).isEqualTo(RUBRIQUES);
        }

        @Test
        void givenNewUser_whenInitOnboardingDemande_thenPlacesTheDemandeInTheTemplateRubriques() {
            stubHappyPath();

            onboardingService.initOnboardingDemande(buildDemande());

            verify(templateService).rubriquesWithDemande(EVENT_TYPE, new DemandeInitieeDto(PRESTATAIRE_ID, MESSAGE));
        }

        @Test
        void givenNewUser_whenInitOnboardingDemande_thenSendsVerificationEmailWithHmacToken() {
            stubHappyPath();

            onboardingService.initOnboardingDemande(buildDemande());

            verify(onboardingMailerService).sendVerificationEmail(EMAIL, "hmac.token");
        }

        private void stubHappyPath() {
            when(utilisateurService.existsByEmail(EMAIL)).thenReturn(false);
            when(templateService.rubriquesWithDemande(EVENT_TYPE, new DemandeInitieeDto(PRESTATAIRE_ID, MESSAGE)))
                    .thenReturn(RUBRIQUES);
            when(onboardingSessionService.initiate(eq(EMAIL), any()))
                    .thenReturn(new OnboardingSessionService.InitiationResult(Onboarding.builder().email(EMAIL).build(), "hmac.token"));
        }

        private InitOnboardingDemandeRequest buildDemande() {
            return new InitOnboardingDemandeRequest(
                    FIRSTNAME, LASTNAME, EMAIL, PRESTATAIRE_ID,
                    EVENT_TYPE, null, null, null, DATE,
                    null, null, null, TELEPHONE, MESSAGE);
        }
    }

    // -------------------------------------------------------------------------
    // initOnboardingSession
    // -------------------------------------------------------------------------

    @Nested
    class InitOnboardingSession {

        @Test
        void givenExistingUser_whenInitOnboardingSession_thenSendsSecurityAlertWithoutSession() {
            when(utilisateurService.existsByEmail(EMAIL)).thenReturn(true);

            onboardingService.initOnboardingSession(fullRequest());

            verify(onboardingMailerService).sendSecurityAlertEmail(EMAIL);
            verify(onboardingSessionService, never()).initiate(any(), any());
        }

        @Test
        void givenNewUser_whenInitOnboardingSession_thenChecksEachDemandePrestataireIsPublished() {
            stubNewUser();

            onboardingService.initOnboardingSession(fullRequest());

            verify(prestataireService).ensurePublished(PRESTATAIRE_ID);
        }

        @Test
        void givenNewUser_whenInitOnboardingSession_thenStoresTheEventAsIs() {
            stubNewUser();
            InitOnboardingRequest request = fullRequest();

            onboardingService.initOnboardingSession(request);

            verify(onboardingSessionService).initiate(EMAIL, request);
            verify(onboardingMailerService).sendVerificationEmail(EMAIL, "hmac.token");
        }

        private void stubNewUser() {
            when(utilisateurService.existsByEmail(EMAIL)).thenReturn(false);
            when(onboardingSessionService.initiate(eq(EMAIL), any()))
                    .thenReturn(new OnboardingSessionService.InitiationResult(Onboarding.builder().email(EMAIL).build(), "hmac.token"));
        }
    }

    private static InitOnboardingRequest fullRequest() {
        return new InitOnboardingRequest(FIRSTNAME, LASTNAME, EMAIL, TELEPHONE,
                new EvenementDto(EVENT_TYPE, null, null, null, DATE, null, null, null, RUBRIQUES));
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
                    .isThrownBy(() -> onboardingService.confirmOnboarding(buildRequest()));
        }

        @Test
        void givenExpiredToken_whenConfirmAccount_thenDoesNotExtractClaims() {
            when(setPasswordTokenJwtService.isExpired(SP_TOKEN)).thenReturn(true);

            assertThatExceptionOfType(TokenExpiredException.class)
                    .isThrownBy(() -> onboardingService.confirmOnboarding(buildRequest()));

            verify(setPasswordTokenJwtService, never()).extractClaims(any());
        }

        @Test
        void givenJwtException_whenConfirmAccount_thenThrowsInvalidTokenException() {
            when(setPasswordTokenJwtService.isExpired(SP_TOKEN)).thenReturn(false);
            when(setPasswordTokenJwtService.extractClaims(SP_TOKEN)).thenThrow(new JwtException("bad token"));

            assertThatExceptionOfType(InvalidTokenException.class)
                    .isThrownBy(() -> onboardingService.confirmOnboarding(buildRequest()));
        }

        @Test
        void givenValidToken_whenConfirmAccount_thenCreatesKeycloakUser() {
            stubValidToken(UUID.randomUUID());

            onboardingService.confirmOnboarding(buildRequest());

            verify(keycloakAdminService).createClientUser(EMAIL, FIRSTNAME, LASTNAME, "p@ssw0rd!");
        }

        @Test
        void givenValidToken_whenConfirmAccount_thenCreatesEntities() {
            UUID onboardingId = UUID.randomUUID();
            InitOnboardingRequest formData = stubValidToken(onboardingId);

            onboardingService.confirmOnboarding(buildRequest());

            verify(onboardingSessionService).createEntities(formData, EMAIL);
        }

        @Test
        void givenValidToken_whenConfirmAccount_thenSendsWelcomeEmail() {
            stubValidToken(UUID.randomUUID());

            onboardingService.confirmOnboarding(buildRequest());

            verify(onboardingMailerService).sendWelcomeEmail(EMAIL);
        }

        private InitOnboardingRequest stubValidToken(UUID onboardingId) {
            Claims claims = mock(Claims.class);
            when(claims.get("actionTokenId", String.class)).thenReturn(null);
            when(claims.get("onboardingId", String.class)).thenReturn(onboardingId.toString());
            when(claims.getSubject()).thenReturn(EMAIL);
            when(setPasswordTokenJwtService.isExpired(SP_TOKEN)).thenReturn(false);
            when(setPasswordTokenJwtService.extractClaims(SP_TOKEN)).thenReturn(claims);

            InitOnboardingRequest formData = fullRequest();
            Onboarding onboarding = Onboarding.builder().id(onboardingId).email(EMAIL).build();
            when(onboardingSessionService.findById(onboardingId)).thenReturn(onboarding);
            when(onboardingSessionService.consume(onboarding)).thenReturn(formData);
            when(onboardingSessionService.createEntities(any(), any())).thenReturn(UUID.randomUUID());

            return formData;
        }

        private ConfirmAccountRequest buildRequest() {
            return new ConfirmAccountRequest(SP_TOKEN, "p@ssw0rd!", true);
        }
    }

    // -------------------------------------------------------------------------
    // confirmAccount — repli sur le flux prestataire (claim actionTokenId)
    // -------------------------------------------------------------------------

    @Nested
    class ConfirmPrestataireOnboarding {

        private static final String KC_USER_ID = "kc-user-id";

        @Test
        void givenActionTokenClaim_whenConfirmAccount_thenDoesNotCreateNewKeycloakUser() {
            stubValidActionToken(UUID.randomUUID());

            onboardingService.confirmOnboarding(buildRequest());

            verify(keycloakAdminService, never()).createClientUser(any(), any(), any(), any());
        }

        @Test
        void givenActionTokenClaim_whenConfirmAccount_thenDoesNotCreateOnboardingEntities() {
            stubValidActionToken(UUID.randomUUID());

            onboardingService.confirmOnboarding(buildRequest());

            verify(onboardingSessionService, never()).findById(any());
            verify(onboardingSessionService, never()).createEntities(any(), any());
        }

        @Test
        void givenActionTokenClaim_whenConfirmAccount_thenResetsPasswordOnExistingKeycloakUser() {
            stubValidActionToken(UUID.randomUUID());

            onboardingService.confirmOnboarding(buildRequest());

            verify(keycloakAdminService).getUserIdByEmail(EMAIL);
            verify(keycloakAdminService).resetPassword(KC_USER_ID, "p@ssw0rd!");
        }

        @Test
        void givenActionTokenClaim_whenConfirmAccount_thenConsumesActionTokenOnlyAfterPasswordReset() {
            ActionToken actionToken = stubValidActionToken(UUID.randomUUID());

            onboardingService.confirmOnboarding(buildRequest());

            InOrder inOrder = inOrder(keycloakAdminService, actionTokenService);
            inOrder.verify(keycloakAdminService).resetPassword(KC_USER_ID, "p@ssw0rd!");
            inOrder.verify(actionTokenService).consume(actionToken);
        }

        @Test
        void givenActionTokenClaim_whenConfirmAccount_thenReturnsMagicLoginUrlToProSpace() {
            stubValidActionToken(UUID.randomUUID());
            when(keycloakAdminService.getMagicLoginUrl(EMAIL, "/pro/page-edition")).thenReturn("https://sgilt/magic");

            ConfirmAccountResponse response = onboardingService.confirmOnboarding(buildRequest());

            assertThat(response.loginUrl()).isEqualTo("https://sgilt/magic");
        }

        private ActionToken stubValidActionToken(UUID actionTokenId) {
            Claims claims = mock(Claims.class);
            when(claims.get("actionTokenId", String.class)).thenReturn(actionTokenId.toString());
            when(setPasswordTokenJwtService.isExpired(SP_TOKEN)).thenReturn(false);
            when(setPasswordTokenJwtService.extractClaims(SP_TOKEN)).thenReturn(claims);

            ActionToken actionToken = ActionToken.builder().id(actionTokenId).build();
            when(actionTokenService.findById(actionTokenId)).thenReturn(actionToken);
            when(actionTokenService.readPayload(actionToken)).thenReturn(Map.of("email", EMAIL));
            when(keycloakAdminService.getUserIdByEmail(EMAIL)).thenReturn(KC_USER_ID);

            return actionToken;
        }

        private ConfirmAccountRequest buildRequest() {
            return new ConfirmAccountRequest(SP_TOKEN, "p@ssw0rd!", true);
        }
    }
}
