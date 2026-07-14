package net.franzka.sgilt.core.admin.service;

import net.franzka.sgilt.core.admin.mailer.AdminMailerService;
import net.franzka.sgilt.core.jwt.domain.ActionToken;
import net.franzka.sgilt.core.jwt.domain.ActionType;
import net.franzka.sgilt.core.jwt.service.ActionLinkService;
import net.franzka.sgilt.core.jwt.service.ActionTokenService;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.prestataire.dto.PrestataireOnboardingPendingDto;
import net.franzka.sgilt.core.prestataire.mapper.PrestataireMapper;
import net.franzka.sgilt.core.prestataire.service.PrestataireService;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import net.franzka.sgilt.core.utilisateur.domain.UtilisateurStatus;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminOnboardingServiceTest {

    @Mock
    private ActionTokenService actionTokenService;

    @Mock
    private ActionLinkService actionLinkService;

    @Mock
    private PrestataireService prestataireService;

    @Mock
    private PrestataireMapper prestataireMapper;

    @Mock
    private AdminMailerService adminMailerService;

    @InjectMocks
    private AdminOnboardingService adminOnboardingService;

    private static final String EMAIL = "pro@sgilt.fr";
    private static final String FIRST_NAME = "Jean";

    private final Utilisateur utilisateur = Utilisateur.builder()
            .id(UUID.randomUUID())
            .firstName(FIRST_NAME)
            .lastName("Photographe")
            .email(EMAIL)
            .status(UtilisateurStatus.ACTIVE)
            .build();

    private final Prestataire prestataire = Prestataire.builder()
            .id(UUID.randomUUID())
            .utilisateur(utilisateur)
            .name("Jean Photographe")
            .slug("jean-photographe")
            .build();

    // -------------------------------------------------------------------------
    // listPendingOnboardings
    // -------------------------------------------------------------------------

    @Nested
    class ListPendingOnboardings {

        @Test
        void givenPendingTokens_whenListPendingOnboardings_thenReturnsDtosResolvedFromPayload() {
            ActionToken token = ActionToken.builder()
                    .id(UUID.randomUUID())
                    .type(ActionType.PRESTATAIRE_ONBOARDING)
                    .createdAt(LocalDateTime.now().minusDays(1))
                    .expiresAt(LocalDateTime.now().plusDays(6))
                    .build();
            PrestataireOnboardingPendingDto dto = new PrestataireOnboardingPendingDto(
                    prestataire.getId(), prestataire.getName(), EMAIL, token.getCreatedAt(), token.getExpiresAt());
            when(actionTokenService.findAllByType(ActionType.PRESTATAIRE_ONBOARDING)).thenReturn(List.of(token));
            when(actionTokenService.readPayload(token)).thenReturn(Map.of("email", EMAIL));
            when(prestataireService.getByUtilisateurEmail(EMAIL)).thenReturn(prestataire);
            when(prestataireMapper.toOnboardingPendingDto(prestataire, token.getCreatedAt(), token.getExpiresAt()))
                    .thenReturn(dto);

            List<PrestataireOnboardingPendingDto> result = adminOnboardingService.listPendingOnboardings();

            assertThat(result).containsExactly(dto);
        }

        @Test
        void givenNoPendingTokens_whenListPendingOnboardings_thenReturnsEmptyList() {
            when(actionTokenService.findAllByType(ActionType.PRESTATAIRE_ONBOARDING)).thenReturn(List.of());

            assertThat(adminOnboardingService.listPendingOnboardings()).isEmpty();
        }
    }

    // -------------------------------------------------------------------------
    // resendOnboardingEmail
    // -------------------------------------------------------------------------

    @Nested
    class ResendOnboardingEmail {

        @Test
        void givenPendingOnboarding_whenResendOnboardingEmail_thenRenewsLinkAndSendsMail() {
            ActionToken token = ActionToken.builder().id(UUID.randomUUID()).build();
            when(prestataireService.getById(prestataire.getId())).thenReturn(prestataire);
            when(actionTokenService.findPendingByEmail(ActionType.PRESTATAIRE_ONBOARDING, EMAIL)).thenReturn(token);
            when(actionLinkService.rebuildLink(token)).thenReturn("https://sgilt.fr/onboarding/verify?token=abc");
            when(adminMailerService.sendPrestataireOnboardingEmail(
                    EMAIL, FIRST_NAME, "https://sgilt.fr/onboarding/verify?token=abc"))
                    .thenReturn(true);

            boolean result = adminOnboardingService.resendOnboardingEmail(prestataire.getId());

            assertThat(result).isTrue();
        }

        @Test
        void givenMailerFailure_whenResendOnboardingEmail_thenReturnsFalse() {
            ActionToken token = ActionToken.builder().id(UUID.randomUUID()).build();
            when(prestataireService.getById(prestataire.getId())).thenReturn(prestataire);
            when(actionTokenService.findPendingByEmail(ActionType.PRESTATAIRE_ONBOARDING, EMAIL)).thenReturn(token);
            when(actionLinkService.rebuildLink(token)).thenReturn("https://sgilt.fr/onboarding/verify?token=abc");
            when(adminMailerService.sendPrestataireOnboardingEmail(
                    EMAIL, FIRST_NAME, "https://sgilt.fr/onboarding/verify?token=abc"))
                    .thenReturn(false);

            assertThat(adminOnboardingService.resendOnboardingEmail(prestataire.getId())).isFalse();
        }
    }
}
