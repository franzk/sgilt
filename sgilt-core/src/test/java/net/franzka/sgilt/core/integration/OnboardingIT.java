package net.franzka.sgilt.core.integration;

import net.franzka.sgilt.core.onboarding.domain.Onboarding;
import net.franzka.sgilt.core.onboarding.domain.OnboardingState;
import net.franzka.sgilt.core.onboarding.repository.OnboardingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OnboardingIT extends BaseIntegrationTest {

    @Autowired private OnboardingRepository onboardingRepository;

    OnboardingIT(WebApplicationContext wac) {
        super(wac);
    }

    @Test
    void givenValidRequest_whenSubmitDemande_thenReturns202AndCreatesOnboardingSession() {
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

        List<Onboarding> sessions = onboardingRepository.findAll();
        assertThat(sessions).hasSize(1);

        Onboarding session = sessions.getFirst();
        assertThat(session.getEmail()).isEqualTo("franz@ka.net");
        assertThat(session.getState()).isEqualTo(OnboardingState.OPEN);
        assertThat(session.getHmacPayload()).isNotBlank();
        assertThat(session.getExpiresAt()).isAfter(LocalDateTime.now());
        assertThat(session.getPrestataire()).isNotNull();
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
    void givenSameEmailTwice_whenSubmitDemande_thenPreviousSessionCancelledAndNewSessionCreated() {
        // première soumission
        assertThat(mockMvc.post().uri("/api/v1/onboarding")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "firstName": "Franz",
                    "lastName": "Ka",
                    "email": "franz@ka.net",
                    "prestataireId": "37d43573-8cd0-4eef-a838-91d9bb4f2323"
                }
            """)).hasStatus(HttpStatus.ACCEPTED);

        // deuxième soumission
        assertThat(mockMvc.post().uri("/api/v1/onboarding")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "firstName": "Franz",
                    "lastName": "Ka",
                    "email": "franz@ka.net",
                    "prestataireId": "37d43573-8cd0-4eef-a838-91d9bb4f2323"
                }
            """)).hasStatus(HttpStatus.ACCEPTED);

        List<Onboarding> sessions = onboardingRepository.findAll();
        assertThat(sessions)
                .hasSize(2)
                .anyMatch(s -> s.getState() == OnboardingState.CANCELLED)
                .anyMatch(s -> s.getState() == OnboardingState.OPEN);
    }

    @Test
    void givenExistingClient_whenSubmitDemande_thenReturns202AndSendsSecurityAlert() {
        // TODO : nécessite la table utilisateurs
        // quand un email déjà connu est soumis :
        // - pas de nouvelle session d'onboarding créée
        // - mail de sécurité envoyé
    }
}
