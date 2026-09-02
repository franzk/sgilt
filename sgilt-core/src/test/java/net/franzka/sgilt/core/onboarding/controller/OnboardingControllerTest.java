package net.franzka.sgilt.core.onboarding.controller;

import net.franzka.sgilt.core.onboarding.dto.ConfirmAccountRequest;
import net.franzka.sgilt.core.onboarding.dto.ConfirmAccountResponse;
import net.franzka.sgilt.core.onboarding.dto.InitOnboardingRequest;
import net.franzka.sgilt.core.onboarding.dto.InitOnboardingResponse;
import net.franzka.sgilt.core.onboarding.dto.SetPasswordTokenDto;
import net.franzka.sgilt.core.onboarding.service.OnboardingService;
import net.franzka.sgilt.core.onboarding.service.VerifyService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OnboardingControllerTest {

    @Mock
    private OnboardingService onboardingService;

    @Mock
    private VerifyService verifyService;

    @InjectMocks
    private OnboardingController controller;

    // -------------------------------------------------------------------------
    // initOnboarding
    // -------------------------------------------------------------------------

    @Nested
    class InitOnboarding {

        @Test
        void givenValidRequest_whenInitOnboarding_thenReturns202WithEmail() {
            InitOnboardingRequest request = new InitOnboardingRequest(
                    "Jean", "Dupont", "jean@sgilt.fr", UUID.randomUUID(), "Mariage", "Champetre",
                    "Vin d'honneur", "Description", LocalDate.of(2027, 6, 15), "Lyon", "80",
                    "Domaine des fleurs", "0102030405", "Bonjour");
            InitOnboardingResponse response = new InitOnboardingResponse("jean@sgilt.fr");
            when(onboardingService.initOnboardingSession(request)).thenReturn(response);

            ResponseEntity<InitOnboardingResponse> result = controller.initOnboarding(request);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
            assertThat(result.getBody()).isEqualTo(response);
        }
    }

    // -------------------------------------------------------------------------
    // verifyToken
    // -------------------------------------------------------------------------

    @Nested
    class VerifyToken {

        @Test
        void givenToken_whenVerifyToken_thenReturnsSetPasswordToken() {
            SetPasswordTokenDto dto = new SetPasswordTokenDto("jean@sgilt.fr", "set-password-jwt");
            when(verifyService.verify("confirmation-token")).thenReturn(dto);

            ResponseEntity<SetPasswordTokenDto> result = controller.verifyToken("confirmation-token");

            assertThat(result.getBody()).isEqualTo(dto);
        }
    }

    // -------------------------------------------------------------------------
    // confirmAccount
    // -------------------------------------------------------------------------

    @Nested
    class ConfirmAccount {

        @Test
        void givenRequest_whenConfirmAccount_thenReturnsLoginUrl() {
            ConfirmAccountRequest request = new ConfirmAccountRequest("set-password-jwt", "n3wP4ss!", true);
            ConfirmAccountResponse response = new ConfirmAccountResponse("https://kc.sgilt.fr/auth?...");
            when(onboardingService.confirmOnboarding(request)).thenReturn(response);

            ResponseEntity<ConfirmAccountResponse> result = controller.confirmAccount(request);

            assertThat(result.getBody()).isEqualTo(response);
        }
    }
}
