package net.franzka.sgilt.core.integration;

import net.franzka.sgilt.core.config.ConfirmationTokenProperties;
import net.franzka.sgilt.core.jwt.VerificationTokenHmacService;
import net.franzka.sgilt.core.jwt.JwtService;
import net.franzka.sgilt.core.jwt.TokenJwtService;
import net.franzka.sgilt.core.keycloak.KeycloakTokenResponse;
import net.franzka.sgilt.core.onboarding.repository.OnboardingRepository;
import net.franzka.sgilt.core.reservation.domain.Note;
import net.franzka.sgilt.core.reservation.domain.Reservation;
import net.franzka.sgilt.core.reservation.domain.ReservationStatus;
import net.franzka.sgilt.core.reservation.repository.NoteRepository;
import net.franzka.sgilt.core.reservation.repository.ReservationRepository;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import net.franzka.sgilt.core.utilisateur.repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ConfirmationAccountIT extends BaseIntegrationTest {

    @Autowired private OnboardingRepository onboardingRepository;
    @Autowired private VerificationTokenHmacService verificationTokenHmacService;
    @Autowired private ReservationRepository reservationRepository;
    @Autowired private NoteRepository noteRepository;
    @Autowired private UtilisateurRepository utilisateurRepository;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private JwtService jwtService;
    @Autowired private ConfirmationTokenProperties confirmationTokenProperties;

    private static final String ONBOARDING_URL      = "/api/v1/onboarding";
    private static final String VERIFICATION_URL     = "/api/v1/onboarding/verify";
    private static final String CONFIRM_ACCOUNT_URL  = "/api/v1/onboarding/confirm-account";


    ConfirmationAccountIT(WebApplicationContext wac) {
        super(wac);
    }

    @BeforeEach
    void stubKeycloak() {
        when(keycloakAdminService.getUserTokens(any(), any()))
                .thenReturn(new KeycloakTokenResponse("access-token", "refresh-token"));
    }

    // -------------------------------------------------------------------------
    // helpers
    // -------------------------------------------------------------------------

    private String getSetPasswordToken() throws UnsupportedEncodingException {
        return getSetPasswordToken(null);
    }

    private String getSetPasswordToken(String prestataireMessage) throws UnsupportedEncodingException {
        String messageField = prestataireMessage != null
                ? ", \"prestataireMessage\": \"%s\"".formatted(prestataireMessage)
                : "";

        assertThat(mockMvc.post().uri(ONBOARDING_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "firstName": "Franz",
                        "lastName": "Ka",
                        "email": "franz@ka.net",
                        "prestataireId": "37d43573-8cd0-4eef-a838-91d9bb4f2323",
                        "date": "2025-09-15"%s
                    }
                """.formatted(messageField)))
                .hasStatus(202);

        String payload = onboardingRepository.findAll().getFirst().getHmacPayload();
        String verificationToken = verificationTokenHmacService.buildToken(payload);

        var result = mockMvc.get().uri(VERIFICATION_URL)
                .param("token", verificationToken)
                .exchange();

        assertThat(result).hasStatus(200);

        String body = result.getResponse().getContentAsString();
        return objectMapper.readTree(body).get("setPasswordToken").asString();
    }

    // -------------------------------------------------------------------------
    // parcours nominal
    // -------------------------------------------------------------------------

    @Test
    void givenValidToken_whenConfirmAccount_thenReturns200() throws UnsupportedEncodingException {
        String setPasswordToken = getSetPasswordToken();

        assertThat(mockMvc.post().uri(CONFIRM_ACCOUNT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "setPasswordToken": "%s",
                        "password": "p@ssw0rd!"
                    }
                """.formatted(setPasswordToken)))
                .hasStatus(200)
                .bodyJson()
                .extractingPath("$.accessToken").asString().isEqualTo("access-token");
    }

    @Test
    void givenValidToken_whenConfirmAccount_thenReservationIsNew() throws UnsupportedEncodingException {
        String setPasswordToken = getSetPasswordToken();

        assertThat(mockMvc.post().uri(CONFIRM_ACCOUNT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "setPasswordToken": "%s",
                        "password": "p@ssw0rd!"
                    }
                """.formatted(setPasswordToken)))
                .hasStatus(200);

        Reservation reservation = reservationRepository.findAll().getFirst();
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.NEW);
    }

    @Test
    void givenValidToken_whenConfirmAccount_thenOnboardingSessionDeleted() throws UnsupportedEncodingException {
        String setPasswordToken = getSetPasswordToken();

        assertThat(mockMvc.post().uri(CONFIRM_ACCOUNT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "setPasswordToken": "%s",
                        "password": "p@ssw0rd!"
                    }
                """.formatted(setPasswordToken)))
                .hasStatus(200);

        assertThat(onboardingRepository.findAll()).isEmpty();
    }

    @Test
    void givenValidToken_whenConfirmAccount_thenCreatesUtilisateurWithCorrectFields() throws UnsupportedEncodingException {
        String setPasswordToken = getSetPasswordToken();

        assertThat(mockMvc.post().uri(CONFIRM_ACCOUNT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "setPasswordToken": "%s",
                        "password": "p@ssw0rd!"
                    }
                """.formatted(setPasswordToken)))
                .hasStatus(200);

        Utilisateur utilisateur = utilisateurRepository.findAll().stream()
                .filter(u -> "franz@ka.net".equals(u.getEmail()))
                .findFirst()
                .orElseThrow();
        assertThat(utilisateur.getFirstName()).isEqualTo("Franz");
        assertThat(utilisateur.getLastName()).isEqualTo("Ka");
        assertThat(utilisateur.getCreatedAt()).isNotNull();
    }

    // -------------------------------------------------------------------------
    // parcours message prestataire → note
    // -------------------------------------------------------------------------

    @Test
    void givenPrestataireMessageInRequest_whenFullOnboardingFlow_thenNoteCreatedWithMessage() throws UnsupportedEncodingException {
        String message = "Bonjour, j'aurais besoin de musique jazz pour 50 personnes";
        String setPasswordToken = getSetPasswordToken(message);

        assertThat(mockMvc.post().uri(CONFIRM_ACCOUNT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "setPasswordToken": "%s",
                        "password": "p@ssw0rd!"
                    }
                """.formatted(setPasswordToken)))
                .hasStatus(200);

        Note note = noteRepository.findAll().getFirst();
        assertThat(note.getTitle()).isEqualTo("Demande de réservation");
        assertThat(note.getContent()).isEqualTo(message);
        assertThat(note.getHidden()).isFalse();
    }

    @Test
    void givenNoPrestataireMessageInRequest_whenFullOnboardingFlow_thenNoteCreatedWithNullContent() throws UnsupportedEncodingException {
        String setPasswordToken = getSetPasswordToken();

        assertThat(mockMvc.post().uri(CONFIRM_ACCOUNT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "setPasswordToken": "%s",
                        "password": "p@ssw0rd!"
                    }
                """.formatted(setPasswordToken)))
                .hasStatus(200);

        Note note = noteRepository.findAll().getFirst();
        assertThat(note.getTitle()).isEqualTo("Demande de réservation");
        assertThat(note.getContent()).isNull();
    }

    // -------------------------------------------------------------------------
    // parcours edge — session déjà consommée
    // -------------------------------------------------------------------------

    @Test
    void givenAlreadyConsumedSession_whenConfirmAccountAgain_thenReturns404() throws UnsupportedEncodingException {
        String setPasswordToken = getSetPasswordToken();
        String body = """
                {
                    "setPasswordToken": "%s",
                    "password": "p@ssw0rd!"
                }
                """.formatted(setPasswordToken);

        assertThat(mockMvc.post().uri(CONFIRM_ACCOUNT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .hasStatus(200);

        assertThat(mockMvc.post().uri(CONFIRM_ACCOUNT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .hasStatus(404);
    }

    // -------------------------------------------------------------------------
    // parcours edge — JWT valide mais session inconnue
    // -------------------------------------------------------------------------

    @Test
    void givenJwtWithUnknownOnboardingId_whenConfirmAccount_thenReturns404() {
        TokenJwtService tokenService = new TokenJwtService(
                jwtService,
                confirmationTokenProperties.confirmationSecret(),
                "set-password",
                Duration.ofHours(24)
        );
        String token = tokenService.generateToken(
                Map.of("onboardingId", UUID.randomUUID().toString()),
                "franz@ka.net"
        );

        assertThat(mockMvc.post().uri(CONFIRM_ACCOUNT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "setPasswordToken": "%s",
                        "password": "p@ssw0rd!"
                    }
                """.formatted(token)))
                .hasStatus(404);
    }

    // -------------------------------------------------------------------------
    // parcours edge — token invalide / expiré / mdp vide
    // -------------------------------------------------------------------------

    @Test
    void givenInvalidToken_whenConfirmAccount_thenReturns400() {
        assertThat(mockMvc.post().uri(CONFIRM_ACCOUNT_URL)
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
        TokenJwtService expiredTokenService = new TokenJwtService(
                jwtService,
                confirmationTokenProperties.confirmationSecret(),
                "set-password",
                Duration.ZERO
        );
        String expiredToken = expiredTokenService.generateToken(
                Map.of("onboardingId", UUID.randomUUID().toString()),
                "franz@ka.net"
        );

        assertThat(mockMvc.post().uri(CONFIRM_ACCOUNT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "setPasswordToken": "%s",
                        "password": "p@ssw0rd!"
                    }
                """.formatted(expiredToken)))
                .hasStatus(410);
    }

    @Test
    void givenEmptyPassword_whenConfirmAccount_thenReturns400() throws UnsupportedEncodingException {
        String setPasswordToken = getSetPasswordToken();

        assertThat(mockMvc.post().uri(CONFIRM_ACCOUNT_URL)
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
