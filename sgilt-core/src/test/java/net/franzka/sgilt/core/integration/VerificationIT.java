package net.franzka.sgilt.core.integration;

import net.franzka.sgilt.core.jwt.VerificationTokenHmacService;
import net.franzka.sgilt.core.onboarding.domain.Onboarding;
import net.franzka.sgilt.core.onboarding.domain.OnboardingState;
import net.franzka.sgilt.core.onboarding.repository.OnboardingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class VerificationIT extends BaseIntegrationTest {

    @Autowired private OnboardingRepository onboardingRepository;
    @Autowired private VerificationTokenHmacService verificationTokenHmacService;

    private static final String ONBOARDING_URL   = "/api/v1/onboarding";
    private static final String VERIFICATION_URL = "/api/v1/onboarding/verify";

    VerificationIT(WebApplicationContext wac) {
        super(wac);
    }

    private String submitDemandeAndGetToken() {
        assertThat(mockMvc.post().uri(ONBOARDING_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "firstName": "Franz",
                        "lastName": "Ka",
                        "email": "franz@ka.net",
                        "prestataireId": "37d43573-8cd0-4eef-a838-91d9bb4f2323"
                    }
                """)
        ).hasStatus(202);

        String payload = onboardingRepository.findAll().getFirst().getHmacPayload();
        return verificationTokenHmacService.buildToken(payload);
    }

    // -------------------------------------------------------------------------
    // parcours nominal
    // -------------------------------------------------------------------------

    @Test
    void givenValidToken_whenVerify_thenReturns200WithEmailAndSetPasswordToken() {
        String token = submitDemandeAndGetToken();

        var response = mockMvc.get().uri(VERIFICATION_URL)
                .param("token", token)
                .exchange();

        assertThat(response)
                .hasStatus(200)
                .bodyJson()
                .extractingPath("$.email").isEqualTo("franz@ka.net");

        assertThat(response)
                .bodyJson()
                .extractingPath("$.setPasswordToken").asString().isNotBlank();
    }

    @Test
    void givenValidToken_whenVerify_thenSessionMarkedAsPendingConfirmation() {
        String token = submitDemandeAndGetToken();

        assertThat(mockMvc.get().uri(VERIFICATION_URL)
                .param("token", token))
                .hasStatus(200);

        Onboarding session = onboardingRepository.findAll().getFirst();
        assertThat(session.getState()).isEqualTo(OnboardingState.PENDING_CONFIRMATION);
    }

    // -------------------------------------------------------------------------
    // parcours edge — idempotence dans la période de grâce
    // -------------------------------------------------------------------------

    @Test
    void givenSessionInPendingConfirmation_whenVerifyWithinGracePeriod_thenReturns200() {
        String token = submitDemandeAndGetToken();

        assertThat(mockMvc.get().uri(VERIFICATION_URL)
                .param("token", token))
                .hasStatus(200);

        assertThat(mockMvc.get().uri(VERIFICATION_URL)
                .param("token", token))
                .hasStatus(200);
    }

    @Test
    void givenSessionInPendingConfirmation_whenVerifyAfterGracePeriod_thenReturns410() {
        String token = submitDemandeAndGetToken();

        assertThat(mockMvc.get().uri(VERIFICATION_URL)
                .param("token", token))
                .hasStatus(200);

        Onboarding session = onboardingRepository.findAll().getFirst();
        session.setConfirmationPeriodExpiresAt(LocalDateTime.now().minusSeconds(1));
        onboardingRepository.save(session);

        assertThat(mockMvc.get().uri(VERIFICATION_URL)
                .param("token", token))
                .hasStatus(410);
    }

    // -------------------------------------------------------------------------
    // parcours edge — session expirée ou annulée
    // -------------------------------------------------------------------------

    @Test
    void givenExpiredToken_whenVerify_thenReturns410() {
        String token = submitDemandeAndGetToken();

        Onboarding session = onboardingRepository.findAll().getFirst();
        session.setExpiresAt(LocalDateTime.now().minusHours(1));
        onboardingRepository.save(session);

        assertThat(mockMvc.get().uri(VERIFICATION_URL)
                .param("token", token))
                .hasStatus(410);
    }

    @Test
    void givenCancelledSession_whenVerify_thenReturns403() {
        String body = """
                {
                    "firstName": "Franz",
                    "lastName": "Ka",
                    "email": "franz@ka.net",
                    "prestataireId": "37d43573-8cd0-4eef-a838-91d9bb4f2323"
                }
                """;

        assertThat(mockMvc.post().uri(ONBOARDING_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .hasStatus(202);

        assertThat(mockMvc.post().uri(ONBOARDING_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .hasStatus(202);

        Onboarding cancelledSession = onboardingRepository
                .findByEmailAndState("franz@ka.net", OnboardingState.CANCELLED)
                .getFirst();

        String token = verificationTokenHmacService.buildToken(cancelledSession.getHmacPayload());

        assertThat(mockMvc.get().uri(VERIFICATION_URL)
                .param("token", token))
                .hasStatus(403);
    }

    @Test
    void givenAlreadyUsedToken_whenVerify_thenReturns403() {
        String token = submitDemandeAndGetToken();

        assertThat(mockMvc.get().uri(VERIFICATION_URL)
                .param("token", token))
                .hasStatus(200);

        Onboarding session = onboardingRepository.findAll().getFirst();
        assertThat(session.getState()).isEqualTo(OnboardingState.PENDING_CONFIRMATION);
        session.setState(OnboardingState.USED);
        onboardingRepository.save(session);

        assertThat(mockMvc.get().uri(VERIFICATION_URL)
                .param("token", token))
                .hasStatus(403);
    }

    // -------------------------------------------------------------------------
    // parcours edge — token invalide
    // -------------------------------------------------------------------------

    @Test
    void givenInvalidToken_whenVerify_thenReturns400() {
        assertThat(mockMvc.get().uri(VERIFICATION_URL)
                .param("token", "token-invalide"))
                .hasStatus(400);
    }
}
