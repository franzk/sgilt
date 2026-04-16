package net.franzka.sgilt.core.integration;

import net.franzka.sgilt.core.jwt.ConfirmationTokenHmacService;
import net.franzka.sgilt.core.onboarding.repository.ConfirmationTokenRepository;
import net.franzka.sgilt.core.reservation.domain.Reservation;
import net.franzka.sgilt.core.reservation.domain.ReservationStatus;
import net.franzka.sgilt.core.reservation.repository.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.io.UnsupportedEncodingException;

import static org.assertj.core.api.Assertions.assertThat;

class ConfirmationAccountIT extends BaseIntegrationTest {

    @Autowired private ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired private ConfirmationTokenHmacService confirmationTokenHmacService;
    @Autowired private ReservationRepository reservationRepository;
    @Autowired private ObjectMapper objectMapper;

    ConfirmationAccountIT(WebApplicationContext wac) {
        super(wac);
    }

    private String getSetPasswordToken() throws UnsupportedEncodingException {
        assertThat(mockMvc.post().uri("/api/v1/onboarding")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "firstName": "Franz",
                        "lastName": "Ka",
                        "email": "franz@ka.net",
                        "prestataireId": "37d43573-8cd0-4eef-a838-91d9bb4f2323"
                    }
                """))
                .hasStatus(202);

        String payload = confirmationTokenRepository.findAll().getFirst().getPayload();
        String confirmationToken = confirmationTokenHmacService.buildToken(payload);

        var result = mockMvc.get().uri("/confirmation")
                .param("token", confirmationToken)
                .exchange();

        assertThat(result).hasStatus(200);

        String body = result.getResponse().getContentAsString();
        return objectMapper.readTree(body).get("setPasswordToken").asString();
    }

    @Test
    void givenValidToken_whenConfirmAccount_thenReturns200() throws UnsupportedEncodingException {
        String setPasswordToken = getSetPasswordToken();

        System.out.println("setPasswordToken = " + setPasswordToken);

        assertThat(mockMvc.post().uri("/api/v1/onboarding/confirm-account")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "setPasswordToken": "%s",
                        "password": "p@ssw0rd!"
                    }
                """.formatted(setPasswordToken)))
                .hasStatus(200)
                .bodyJson()
                .extractingPath("$.accessToken").asString().isNotBlank();
    }

    @Test
    void givenValidToken_whenConfirmAccount_thenReservationIsNouvelle() throws UnsupportedEncodingException {
        String setPasswordToken = getSetPasswordToken();

        assertThat(mockMvc.post().uri("/api/v1/onboarding/confirm-account")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "setPasswordToken": "%s",
                        "password": "p@ssw0rd!"
                    }
                """.formatted(setPasswordToken)))
                .hasStatus(200);

        Reservation reservation = reservationRepository.findAll().getFirst();
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.NOUVELLE);
    }

    @Test
    void givenValidToken_whenConfirmAccount_thenConfirmationTokenDeleted() throws UnsupportedEncodingException {
        String setPasswordToken = getSetPasswordToken();

        assertThat(mockMvc.post().uri("/api/v1/onboarding/confirm-account")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "setPasswordToken": "%s",
                        "password": "p@ssw0rd!"
                    }
                """.formatted(setPasswordToken)))
                .hasStatus(200);

        assertThat(confirmationTokenRepository.findAll()).isEmpty();
    }

    @Test
    void givenInvalidToken_whenConfirmAccount_thenReturns400() {
        assertThat(mockMvc.post().uri("/api/v1/onboarding/confirm-account")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "setPasswordToken": "token-invalide",
                        "password": "p@ssw0rd!"
                    }
                """))
                .hasStatus(400);
    }

    @Test
    void givenExpiredToken_whenConfirmAccount_thenReturns410() {
        // TDD - générer un setPasswordToken expiré
        // nécessite d'exposer une méthode pour générer un token expiré dans TokenJwtService
    }

    @Test
    void givenEmptyPassword_whenConfirmAccount_thenReturns400() throws UnsupportedEncodingException {
        String setPasswordToken = getSetPasswordToken();

        assertThat(mockMvc.post().uri("/api/v1/onboarding/confirm-account")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "setPasswordToken": "%s",
                        "password": ""
                    }
                """.formatted(setPasswordToken)))
                .hasStatus(400);
    }
}