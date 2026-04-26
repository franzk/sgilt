package net.franzka.sgilt.core.onboarding.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import net.franzka.sgilt.core.jwt.TokenJwtService;
import net.franzka.sgilt.core.keycloak.KeycloakAdminService;
import net.franzka.sgilt.core.keycloak.KeycloakTokenResponse;
import net.franzka.sgilt.core.onboarding.domain.Onboarding;
import net.franzka.sgilt.core.onboarding.dto.ConfirmAccountRequest;
import net.franzka.sgilt.core.onboarding.dto.ConfirmAccountResponse;
import net.franzka.sgilt.core.onboarding.dto.InitOnboardingRequest;
import net.franzka.sgilt.core.onboarding.dto.InitOnboardingResponse;
import net.franzka.sgilt.core.onboarding.exception.InvalidTokenException;
import net.franzka.sgilt.core.onboarding.exception.TokenExpiredException;
import net.franzka.sgilt.core.onboarding.mailer.OnboardingMailerService;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.prestataire.service.PrestataireService;
import net.franzka.sgilt.core.utilisateur.service.UtilisateurService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
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

    @Mock private PrestataireService prestataireService;
    @Mock private OnboardingSessionService onboardingSessionService;
    @Mock private TokenJwtService setPasswordTokenJwtService;
    @Mock private OnboardingMailerService onboardingMailerService;
    @Mock private UtilisateurService utilisateurService;
    @Mock private KeycloakAdminService keycloakAdminService;

    @InjectMocks
    private OnboardingService onboardingService;

    // -------------------------------------------------------------------------
    // initOnboardingSession
    // -------------------------------------------------------------------------

    @Nested
    class InitOnboardingSession {

        @Test
        void givenExistingUser_whenInitOnboardingSession_thenSendsSecurityAlertEmail() {
            when(utilisateurService.existsByEmail(EMAIL)).thenReturn(true);

            onboardingService.initOnboardingSession(buildRequest());

            verify(onboardingMailerService).sendSecurityAlertEmail(EMAIL, PRESTATAIRE_ID);
        }

        @Test
        void givenExistingUser_whenInitOnboardingSession_thenDoesNotInitiateOnboarding() {
            when(utilisateurService.existsByEmail(EMAIL)).thenReturn(true);

            onboardingService.initOnboardingSession(buildRequest());

            verify(onboardingSessionService, never()).initiate(any(), any(), any());
        }

        @Test
        void givenExistingUser_whenInitOnboardingSession_thenReturnsResponseWithEmail() {
            when(utilisateurService.existsByEmail(EMAIL)).thenReturn(true);

            InitOnboardingResponse response = onboardingService.initOnboardingSession(buildRequest());

            assertThat(response.email()).isEqualTo(EMAIL);
        }

        @Test
        void givenNewUser_whenInitOnboardingSession_thenCancelsExistingSessionsForEmail() {
            stubHappyPath();

            onboardingService.initOnboardingSession(buildRequest());

            verify(onboardingSessionService).cancelExistingForEmail(EMAIL);
        }

        @Test
        void givenNewUser_whenInitOnboardingSession_thenLoadsPrestataire() {
            stubHappyPath();

            onboardingService.initOnboardingSession(buildRequest());

            verify(prestataireService).getById(PRESTATAIRE_ID);
        }

        @Test
        void givenNewUser_whenInitOnboardingSession_thenInitiatesOnboardingWithEmailPrestataireAndRequest() {
            Prestataire prestataire = stubHappyPath();

            onboardingService.initOnboardingSession(buildRequest());

            verify(onboardingSessionService).initiate(eq(EMAIL), eq(prestataire), any(InitOnboardingRequest.class));
        }

        @Test
        void givenNewUser_whenInitOnboardingSession_thenSendsVerificationEmailWithHmacToken() {
            stubHappyPath();

            onboardingService.initOnboardingSession(buildRequest());

            verify(onboardingMailerService).sendVerificationEmail(EMAIL, "hmac.token");
        }

        @Test
        void givenNewUser_whenInitOnboardingSession_thenReturnsResponseWithEmail() {
            stubHappyPath();

            InitOnboardingResponse response = onboardingService.initOnboardingSession(buildRequest());

            assertThat(response.email()).isEqualTo(EMAIL);
        }

        private Prestataire stubHappyPath() {
            when(utilisateurService.existsByEmail(EMAIL)).thenReturn(false);
            Prestataire prestataire = Prestataire.builder().id(PRESTATAIRE_ID).build();
            when(prestataireService.getById(PRESTATAIRE_ID)).thenReturn(prestataire);
            Onboarding onboarding = Onboarding.builder().email(EMAIL).build();
            OnboardingSessionService.InitiationResult result =
                    new OnboardingSessionService.InitiationResult(onboarding, "hmac.token");
            when(onboardingSessionService.initiate(eq(EMAIL), eq(prestataire), any())).thenReturn(result);
            return prestataire;
        }

        private InitOnboardingRequest buildRequest() {
            return new InitOnboardingRequest(
                    FIRSTNAME, LASTNAME, EMAIL, PRESTATAIRE_ID,
                    EVENT_TYPE, null, null, null, DATE,
                    null, null, null, TELEPHONE, null);
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
        void givenValidToken_whenConfirmAccount_thenCreatesKeycloakUser() {
            stubValidToken(UUID.randomUUID());

            onboardingService.confirmAccount(buildRequest());

            verify(keycloakAdminService).createUser(EMAIL, FIRSTNAME, LASTNAME, "p@ssw0rd!");
        }

        @Test
        void givenValidToken_whenConfirmAccount_thenCreatesEntities() {
            UUID onboardingId = UUID.randomUUID();
            InitOnboardingRequest formData = stubValidToken(onboardingId);

            onboardingService.confirmAccount(buildRequest());

            verify(onboardingSessionService).createEntities(eq(formData), any(Prestataire.class), eq(EMAIL));
        }

        @Test
        void givenValidToken_whenConfirmAccount_thenSendsWelcomeEmail() {
            stubValidToken(UUID.randomUUID());

            onboardingService.confirmAccount(buildRequest());

            verify(onboardingMailerService).sendWelcomeEmail(EMAIL);
        }

        @Test
        void givenValidToken_whenConfirmAccount_thenReturnsKeycloakTokens() {
            stubValidToken(UUID.randomUUID());

            ConfirmAccountResponse response = onboardingService.confirmAccount(buildRequest());

            assertThat(response.accessToken()).isEqualTo("access-token");
            assertThat(response.refreshToken()).isEqualTo("refresh-token");
        }

        private InitOnboardingRequest stubValidToken(UUID onboardingId) {
            Claims claims = mock(Claims.class);
            when(claims.get("onboardingId", String.class)).thenReturn(onboardingId.toString());
            when(claims.getSubject()).thenReturn(EMAIL);
            when(setPasswordTokenJwtService.isExpired(SP_TOKEN)).thenReturn(false);
            when(setPasswordTokenJwtService.extractClaims(SP_TOKEN)).thenReturn(claims);

            Prestataire prestataire = Prestataire.builder().id(PRESTATAIRE_ID).build();
            InitOnboardingRequest formData = new InitOnboardingRequest(
                    FIRSTNAME, LASTNAME, EMAIL, PRESTATAIRE_ID,
                    EVENT_TYPE, null, null, null, DATE,
                    null, null, null, TELEPHONE, null);
            Onboarding onboarding = Onboarding.builder().id(onboardingId).email(EMAIL).build();
            when(onboardingSessionService.findById(onboardingId)).thenReturn(onboarding);
            when(onboardingSessionService.consume(onboarding))
                    .thenReturn(new OnboardingSessionService.OnboardingContent(formData, prestataire));
            when(keycloakAdminService.getUserTokens(EMAIL, "p@ssw0rd!"))
                    .thenReturn(new KeycloakTokenResponse("access-token", "refresh-token"));

            return formData;
        }

        private ConfirmAccountRequest buildRequest() {
            return new ConfirmAccountRequest(SP_TOKEN, "p@ssw0rd!");
        }
    }
}
