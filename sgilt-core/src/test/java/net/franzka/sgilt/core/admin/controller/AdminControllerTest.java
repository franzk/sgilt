package net.franzka.sgilt.core.admin.controller;

import net.franzka.sgilt.core.admin.dto.ProvisionPrestataireRequest;
import net.franzka.sgilt.core.admin.dto.ProvisionPrestataireResponse;
import net.franzka.sgilt.core.admin.exception.SlugAlreadyExistsException;
import net.franzka.sgilt.core.admin.service.AdminOnboardingService;
import net.franzka.sgilt.core.keycloak.KeycloakAdminService;
import net.franzka.sgilt.core.onboarding.dto.OnboardingPendingDto;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.prestataire.dto.PrestataireAdminListItemDto;
import net.franzka.sgilt.core.prestataire.dto.PrestataireOnboardingPendingDto;
import net.franzka.sgilt.core.prestataire.service.PrestataireService;
import net.franzka.sgilt.core.reservation.domain.ReservationStatus;
import net.franzka.sgilt.core.reservation.dto.AdminReservationListItemDto;
import net.franzka.sgilt.core.reservation.service.ReservationService;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import net.franzka.sgilt.core.utilisateur.service.UtilisateurService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private PrestataireService prestataireService;

    @Mock
    private UtilisateurService utilisateurService;

    @Mock
    private KeycloakAdminService keycloakAdminService;

    @Mock
    private AdminOnboardingService adminOnboardingService;

    @Mock
    private ReservationService reservationService;

    @Mock
    private PlatformTransactionManager transactionManager;

    @InjectMocks
    private AdminController controller;

    // -------------------------------------------------------------------------
    // provisionPrestataire
    // -------------------------------------------------------------------------

    @Nested
    class ProvisionPrestataire {

        private final ProvisionPrestataireRequest request = new ProvisionPrestataireRequest(
                "pro@sgilt.fr", "Jean", "Dupont", "studio-fleur", "Studio Fleur", "photo", "mariage,portrait", true);

        @Test
        void givenSlugAlreadyExists_whenProvisionPrestataire_thenThrowsWithoutCreatingKeycloakUser() {
            when(prestataireService.existsBySlug("studio-fleur")).thenReturn(true);

            assertThatThrownBy(() -> controller.provisionPrestataire(request))
                    .isInstanceOf(SlugAlreadyExistsException.class);
            verify(keycloakAdminService, never()).createProUserWithoutPassword(any(), any(), any());
        }

        @Test
        void givenCleEnMainFlow_whenProvisionPrestataire_thenCreatesViaCleEnMainAndReturns201() {
            stubTransactionExecution();
            when(prestataireService.existsBySlug("studio-fleur")).thenReturn(false);
            when(keycloakAdminService.createProUserWithoutPassword("pro@sgilt.fr", "Jean", "Dupont"))
                    .thenReturn("kc-user-id");
            Utilisateur utilisateur = Utilisateur.builder().id(UUID.randomUUID()).build();
            when(utilisateurService.createUtilisateur("Jean", "Dupont", "pro@sgilt.fr", null)).thenReturn(utilisateur);
            Prestataire prestataire = Prestataire.builder()
                    .id(UUID.randomUUID()).utilisateur(utilisateur).slug("studio-fleur").build();
            when(prestataireService.createPrestataireCleEnMain(
                    utilisateur, "studio-fleur", "Studio Fleur", "photo", List.of("mariage", "portrait")))
                    .thenReturn(new PrestataireService.CreationResult(prestataire, true));

            ResponseEntity<ProvisionPrestataireResponse> response = controller.provisionPrestataire(request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isEqualTo(new ProvisionPrestataireResponse(
                    prestataire.getId(), utilisateur.getId(), "studio-fleur"));
            verify(prestataireService, never()).createPrestataireAutonome(any(), any(), any(), any(), any());
        }

        @Test
        void givenAutonomeFlow_whenProvisionPrestataire_thenCreatesViaAutonome() {
            stubTransactionExecution();
            ProvisionPrestataireRequest autonomeRequest = new ProvisionPrestataireRequest(
                    "pro@sgilt.fr", "Jean", "Dupont", "studio-fleur", "Studio Fleur", "photo", "mariage", false);
            when(keycloakAdminService.createProUserWithoutPassword(any(), any(), any())).thenReturn("kc-user-id");
            Utilisateur utilisateur = Utilisateur.builder().id(UUID.randomUUID()).build();
            when(utilisateurService.createUtilisateur(any(), any(), any(), any())).thenReturn(utilisateur);
            Prestataire prestataire = Prestataire.builder()
                    .id(UUID.randomUUID()).utilisateur(utilisateur).slug("studio-fleur").build();
            when(prestataireService.createPrestataireAutonome(
                    utilisateur, "studio-fleur", "Studio Fleur", "photo", List.of("mariage")))
                    .thenReturn(new PrestataireService.CreationResult(prestataire, true));

            controller.provisionPrestataire(autonomeRequest);

            verify(prestataireService, never()).createPrestataireCleEnMain(any(), any(), any(), any(), any());
        }

        @Test
        void givenNotificationNotDelivered_whenProvisionPrestataire_thenReturns500() {
            stubTransactionExecution();
            when(keycloakAdminService.createProUserWithoutPassword(any(), any(), any())).thenReturn("kc-user-id");
            Utilisateur utilisateur = Utilisateur.builder().id(UUID.randomUUID()).build();
            when(utilisateurService.createUtilisateur(any(), any(), any(), any())).thenReturn(utilisateur);
            Prestataire prestataire = Prestataire.builder().id(UUID.randomUUID()).utilisateur(utilisateur).build();
            when(prestataireService.createPrestataireCleEnMain(any(), any(), any(), any(), any()))
                    .thenReturn(new PrestataireService.CreationResult(prestataire, false));

            ResponseEntity<ProvisionPrestataireResponse> response = controller.provisionPrestataire(request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @Test
        void givenDbCreationFails_whenProvisionPrestataire_thenCompensatesByDeletingKeycloakUserAndRethrows() {
            stubTransactionExecution();
            when(keycloakAdminService.createProUserWithoutPassword(any(), any(), any())).thenReturn("kc-user-id");
            when(utilisateurService.createUtilisateur(any(), any(), any(), any()))
                    .thenThrow(new RuntimeException("erreur DB"));

            assertThatThrownBy(() -> controller.provisionPrestataire(request))
                    .isInstanceOf(RuntimeException.class);
            verify(keycloakAdminService).deleteUser("kc-user-id");
        }

        private void stubTransactionExecution() {
            when(transactionManager.getTransaction(any())).thenReturn(mock(TransactionStatus.class));
        }
    }

    // -------------------------------------------------------------------------
    // listPrestataires
    // -------------------------------------------------------------------------

    @Nested
    class ListPrestataires {

        @Test
        void givenConfirmedPrestataires_whenListPrestataires_thenReturnsThem() {
            List<PrestataireAdminListItemDto> items = List.of(mock(PrestataireAdminListItemDto.class));
            when(prestataireService.getConfirmedPrestataires()).thenReturn(items);

            assertThat(controller.listPrestataires().getBody()).isEqualTo(items);
        }
    }

    // -------------------------------------------------------------------------
    // publishPrestataire
    // -------------------------------------------------------------------------

    @Nested
    class PublishPrestataire {

        @Test
        void givenDelivered_whenPublishPrestataire_thenReturnsNoContent() {
            UUID id = UUID.randomUUID();
            when(prestataireService.publish(id)).thenReturn(true);

            assertThat(controller.publishPrestataire(id).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }

        @Test
        void givenNotDelivered_whenPublishPrestataire_thenReturns500() {
            UUID id = UUID.randomUUID();
            when(prestataireService.publish(id)).thenReturn(false);

            assertThat(controller.publishPrestataire(id).getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // -------------------------------------------------------------------------
    // sendPrestataireBackToReview
    // -------------------------------------------------------------------------

    @Nested
    class SendPrestataireBackToReview {

        @Test
        void givenId_whenSendPrestataireBackToReview_thenDelegatesAndReturnsNoContent() {
            UUID id = UUID.randomUUID();

            ResponseEntity<Void> response = controller.sendPrestataireBackToReview(id);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(prestataireService).sendBackToReview(id);
        }
    }

    // -------------------------------------------------------------------------
    // listPendingOnboardings
    // -------------------------------------------------------------------------

    @Nested
    class ListPendingOnboardings {

        @Test
        void givenPendingOnboardings_whenListPendingOnboardings_thenReturnsThem() {
            List<PrestataireOnboardingPendingDto> items = List.of(mock(PrestataireOnboardingPendingDto.class));
            when(adminOnboardingService.listPendingOnboardings()).thenReturn(items);

            assertThat(controller.listPendingOnboardings().getBody()).isEqualTo(items);
        }
    }

    // -------------------------------------------------------------------------
    // resendOnboardingEmail
    // -------------------------------------------------------------------------

    @Nested
    class ResendOnboardingEmail {

        @Test
        void givenMailSent_whenResendOnboardingEmail_thenReturnsNoContent() {
            UUID id = UUID.randomUUID();
            when(adminOnboardingService.resendOnboardingEmail(id)).thenReturn(true);

            assertThat(controller.resendOnboardingEmail(id).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }

        @Test
        void givenMailNotSent_whenResendOnboardingEmail_thenReturns500() {
            UUID id = UUID.randomUUID();
            when(adminOnboardingService.resendOnboardingEmail(id)).thenReturn(false);

            assertThat(controller.resendOnboardingEmail(id).getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // -------------------------------------------------------------------------
    // listReservations
    // -------------------------------------------------------------------------

    @Nested
    class ListReservations {

        @Test
        void givenStatusFilter_whenListReservations_thenDelegatesWithStatus() {
            List<AdminReservationListItemDto> items = List.of(mock(AdminReservationListItemDto.class));
            when(reservationService.getAdminReservations(ReservationStatus.CONFIRMED)).thenReturn(items);

            assertThat(controller.listReservations(ReservationStatus.CONFIRMED).getBody()).isEqualTo(items);
        }

        @Test
        void givenNoStatusFilter_whenListReservations_thenDelegatesWithNull() {
            List<AdminReservationListItemDto> items = List.of(mock(AdminReservationListItemDto.class));
            when(reservationService.getAdminReservations(null)).thenReturn(items);

            assertThat(controller.listReservations(null).getBody()).isEqualTo(items);
        }
    }

    // -------------------------------------------------------------------------
    // listPendingUserOnboardings
    // -------------------------------------------------------------------------

    @Nested
    class ListPendingUserOnboardings {

        @Test
        void givenPendingUserOnboardings_whenListPendingUserOnboardings_thenReturnsThem() {
            List<OnboardingPendingDto> items = List.of(mock(OnboardingPendingDto.class));
            when(adminOnboardingService.listPendingUserOnboardings()).thenReturn(items);

            assertThat(controller.listPendingUserOnboardings().getBody()).isEqualTo(items);
        }
    }
}
