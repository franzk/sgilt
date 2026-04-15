package net.franzka.sgilt.core.integration;

import net.franzka.sgilt.core.onboarding.repository.ConfirmationTokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

class OnboardingIT extends BaseIntegrationTest {

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

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
                .hasStatus(202)
                .bodyJson()
                .extractingPath("$.email")
                .isEqualTo("franz@ka.net");

        assertThat(confirmationTokenRepository.findAll()).hasSize(1);
    }
}