package net.franzka.sgilt.core.integration;

import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.repository.EvenementRepository;
import net.franzka.sgilt.core.onboarding.domain.ConfirmationToken;
import net.franzka.sgilt.core.onboarding.domain.ConfirmationTokenState;
import net.franzka.sgilt.core.onboarding.repository.ConfirmationTokenRepository;
import net.franzka.sgilt.core.reservation.domain.Reservation;
import net.franzka.sgilt.core.reservation.domain.ReservationStatus;
import net.franzka.sgilt.core.reservation.repository.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OnboardingIT extends BaseIntegrationTest {

    @Autowired private ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired private ReservationRepository reservationRepository;
    @Autowired private EvenementRepository evenementRepository;

    OnboardingIT(WebApplicationContext wac) {
        super(wac);
    }

    @Test
    void givenValidRequest_whenSubmitDemande_thenReturns202AndCreatesToken() {
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
                .hasStatus(HttpStatus.ACCEPTED)
                .bodyJson()
                .extractingPath("$.email")
                .isEqualTo("franz@ka.net");

        // assertions BDD
        List<ConfirmationToken> tokens = confirmationTokenRepository.findAll();
        assertThat(tokens).hasSize(1);

        ConfirmationToken token = tokens.getFirst();
        assertThat(token.getEmail()).isEqualTo("franz@ka.net");
        assertThat(token.getState()).isEqualTo(ConfirmationTokenState.OPEN);
        assertThat(token.getPayload()).isNotBlank();
        assertThat(token.getExpiresAt()).isAfter(LocalDateTime.now());
        assertThat(token.getReservation()).isNotNull();

        Reservation reservation = reservationRepository.findById(token.getReservation().getId()).orElseThrow();
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.DRAFT);

        Evenement evenement = evenementRepository.findById(reservation.getEvenement().getId()).orElseThrow();
        assertThat(evenement.getEmail()).isEqualTo("franz@ka.net");

    }

    @Test
    void givenInvalidEmail_whenSubmitDemande_thenReturns400() {
        assertThat(mockMvc.post().uri("/api/v1/onboarding")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "firstName": "Franz",
                    "lastName": "Ka",
                    "email": "pas-un-email",
                    "prestataireId": "37d43573-8cd0-4eef-a838-91d9bb4f2323"
                }
            """))
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void givenEmptyFirstName_whenSubmitDemande_thenReturns400() {
        assertThat(mockMvc.post().uri("/api/v1/onboarding")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "firstName": "",
                    "lastName": "Ka",
                    "email": "franz@ka.net",
                    "prestataireId": "37d43573-8cd0-4eef-a838-91d9bb4f2323"
                }
            """))
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void givenMissingPrestataireId_whenSubmitDemande_thenReturns400() {
        assertThat(mockMvc.post().uri("/api/v1/onboarding")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "firstName": "Franz",
                    "lastName": "Ka",
                    "email": "franz@ka.net"
                }
            """))
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void givenSameEmailTwice_whenSubmitDemande_thenPreviousTokenCancelledAndNewTokenCreated() {
        // première soumission
        mockMvc.post().uri("/api/v1/onboarding")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "firstName": "Franz",
                    "lastName": "Ka",
                    "email": "franz@ka.net",
                    "prestataireId": "37d43573-8cd0-4eef-a838-91d9bb4f2323"
                }
            """);

        // deuxième soumission
        mockMvc.post().uri("/api/v1/onboarding")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "firstName": "Franz",
                    "lastName": "Ka",
                    "email": "franz@ka.net",
                    "prestataireId": "37d43573-8cd0-4eef-a838-91d9bb4f2323"
                }
            """);

        List<ConfirmationToken> tokens = confirmationTokenRepository.findAll();
        assertThat(tokens)
                .hasSize(2)
                .anyMatch(t -> t.getState() == ConfirmationTokenState.CANCELLED)
                .anyMatch(t -> t.getState() == ConfirmationTokenState.OPEN)
        ;
    }

    @Test
    void givenExistingClient_whenSubmitDemande_thenReturns202AndSendsSecurityAlert() {
        // TODO : nécessite la table utilisateurs
        // quand un email déjà connu est soumis :
        // - pas de nouveau ConfirmationToken créé
        // - mail de sécurité envoyé
        // - réservation DRAFT créée et rattachée au compte existant
    }
}