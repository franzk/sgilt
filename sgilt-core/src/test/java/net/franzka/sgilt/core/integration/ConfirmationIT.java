package net.franzka.sgilt.core.integration;

import net.franzka.sgilt.core.jwt.ConfirmationTokenHmacService;
import net.franzka.sgilt.core.onboarding.domain.ConfirmationToken;
import net.franzka.sgilt.core.onboarding.domain.ConfirmationTokenState;
import net.franzka.sgilt.core.onboarding.repository.ConfirmationTokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ConfirmationIT extends BaseIntegrationTest {

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired private ConfirmationTokenHmacService confirmationTokenHmacService;

    ConfirmationIT(WebApplicationContext wac) {
        super(wac);
    }

    private String submitDemandeAndGetToken() {
        assertThat(mockMvc.post().uri("/api/v1/onboarding")
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

        var confirmationTokens = confirmationTokenRepository.findAll();
        String payload = confirmationTokens.getFirst().getPayload();
        return confirmationTokenHmacService.buildToken(payload);
    }

    @Test
    void givenValidToken_whenVerify_thenReturns200WithEmailAndSetPasswordToken() {
        String token = submitDemandeAndGetToken();

        var response = mockMvc.get().uri("/confirmation")
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
    void givenValidToken_whenVerify_thenTokenMarkedAsPendingConfirmation() {
        String token = submitDemandeAndGetToken();

        assertThat(mockMvc.get().uri("/confirmation")
                .param("token", token))
                .hasStatus(200);

        ConfirmationToken confirmationToken = confirmationTokenRepository.findAll().getFirst();
        assertThat(confirmationToken.getState()).isEqualTo(ConfirmationTokenState.PENDING_CONFIRMATION);
    }

    @Test
    void givenInvalidToken_whenVerify_thenReturns400() {
        assertThat(mockMvc.get().uri("/confirmation")
                .param("token", "token-invalide"))
                .hasStatus(400);
    }

    @Test
    void givenExpiredToken_whenVerify_thenReturns410() {
        String token = submitDemandeAndGetToken();

        ConfirmationToken confirmationToken = confirmationTokenRepository.findAll().getFirst();
        confirmationToken.setExpiresAt(LocalDateTime.now().minusHours(1));
        confirmationTokenRepository.save(confirmationToken);

        assertThat(mockMvc.get().uri("/confirmation")
                .param("token", token))
                .hasStatus(410);
    }

    @Test
    void givenAlreadyUsedToken_whenVerify_thenReturns403() {
        String token = submitDemandeAndGetToken();

        assertThat(mockMvc.get().uri("/confirmation")
                .param("token", token))
                .hasStatus(200);

        ConfirmationToken confirmationToken = confirmationTokenRepository.findAll().getFirst();
        assertThat(confirmationToken.getState()).isEqualTo(ConfirmationTokenState.PENDING_CONFIRMATION);
        confirmationToken.setState(ConfirmationTokenState.USED);
        confirmationTokenRepository.save(confirmationToken);

        assertThat(mockMvc.get().uri("/confirmation")
                .param("token", token))
                .hasStatus(403);
    }

    @Test
    void givenTokenInPendingConfirmation_whenVerifyWithinGracePeriod_thenReturns200() {
        String token = submitDemandeAndGetToken();

        assertThat(mockMvc.get().uri("/confirmation")
                .param("token", token))
                .hasStatus(200);

        assertThat(mockMvc.get().uri("/confirmation")
                .param("token", token))
                .hasStatus(200);
    }

    @Test
    void givenTokenInPendingConfirmation_whenVerifyAfterGracePeriod_thenReturns410() {
        String token = submitDemandeAndGetToken();

        assertThat(mockMvc.get().uri("/confirmation")
                .param("token", token))
                .hasStatus(200);

        ConfirmationToken confirmationToken = confirmationTokenRepository.findAll().getFirst();
        confirmationToken.setConfirmationPeriodExpiresAt(LocalDateTime.now().minusSeconds(1));
        confirmationTokenRepository.save(confirmationToken);

        assertThat(mockMvc.get().uri("/confirmation")
                .param("token", token))
                .hasStatus(410);
    }

    @Test
    void givenCancelledToken_whenVerify_thenReturns403() {
        String body = """
                {
                    "firstName": "Franz",
                    "lastName": "Ka",
                    "email": "franz@ka.net",
                    "prestataireId": "37d43573-8cd0-4eef-a838-91d9bb4f2323"
                }
                """;

        assertThat(mockMvc.post().uri("/api/v1/onboarding")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .hasStatus(202);

        assertThat(mockMvc.post().uri("/api/v1/onboarding")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .hasStatus(202);

        ConfirmationToken cancelledToken = confirmationTokenRepository
                .findByEmailAndState("franz@ka.net", ConfirmationTokenState.CANCELLED)
                .getFirst();

        String token = confirmationTokenHmacService.buildToken(cancelledToken.getPayload());

        assertThat(mockMvc.get().uri("/confirmation")
                .param("token", token))
                .hasStatus(403);
    }
}