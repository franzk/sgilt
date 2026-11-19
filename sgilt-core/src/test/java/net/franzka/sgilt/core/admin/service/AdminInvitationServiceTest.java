package net.franzka.sgilt.core.admin.service;

import net.franzka.sgilt.core.admin.domain.ClientInvitationStatus;
import net.franzka.sgilt.core.admin.dto.ClientInvitationDto;
import net.franzka.sgilt.core.admin.dto.CreateClientInvitationRequest;
import net.franzka.sgilt.core.admin.exception.ClientInvitationNotSentException;
import net.franzka.sgilt.core.admin.mailer.ClientInvitationMailerService;
import net.franzka.sgilt.core.evenement.dto.EvenementSummaryDto;
import net.franzka.sgilt.core.evenement.service.EvenementService;
import net.franzka.sgilt.core.jwt.domain.ActionToken;
import net.franzka.sgilt.core.jwt.domain.ActionType;
import net.franzka.sgilt.core.jwt.service.ActionLinkService;
import net.franzka.sgilt.core.jwt.service.ActionTokenService;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.reservation.domain.Reservation;
import net.franzka.sgilt.core.reservation.dto.ReservationSummaryDto;
import net.franzka.sgilt.core.reservation.service.ReservationService;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import net.franzka.sgilt.core.utilisateur.service.UtilisateurService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminInvitationServiceTest {

    private static final LocalDate EVENT_DATE = LocalDate.of(2027, 6, 12);
    private static final String ACTION_URL = "https://sgilt.fr/onboarding/verify?token=abc";

    @Mock
    private UtilisateurService utilisateurService;

    @Mock
    private EvenementService evenementService;

    @Mock
    private ReservationService reservationService;

    @Mock
    private ActionTokenService actionTokenService;

    @Mock
    private ActionLinkService actionLinkService;

    @Mock
    private ClientInvitationMailerService clientInvitationMailerService;

    @InjectMocks
    private AdminInvitationService adminInvitationService;

    private final Utilisateur utilisateur = Utilisateur.builder()
            .id(UUID.randomUUID()).firstName("Julie").lastName("Martin").email("julie@example.com").build();

    // l'événement du client invité, avec sa réservation chez « Le Taennel »
    private void stubInvitedEvent() {
        UUID eventId = UUID.randomUUID();
        when(evenementService.getUserEvents(utilisateur.getId())).thenReturn(List.of(
                new EvenementSummaryDto(eventId, "Mon événement", EVENT_DATE, null, null, "mariage", 1, 0)));
        when(reservationService.getReservationSummaries(eventId)).thenReturn(List.of(
                new ReservationSummaryDto(UUID.randomUUID(), UUID.randomUUID(), "Le Taennel", null,
                        "lieu", "salle", "lieu", "CONFIRMED", 0)));
    }

    // -------------------------------------------------------------------------
    // createInvitation
    // -------------------------------------------------------------------------

    @Nested
    class CreateInvitation {

        @Test
        void givenRequest_whenCreateInvitation_thenCreatesInvitedClientWithConfirmedReservationAndReturnsNotSentInvitation() {
            UUID prestataireId = UUID.randomUUID();
            CreateClientInvitationRequest request = new CreateClientInvitationRequest(
                    "julie@example.com", "Julie", "Martin", "mariage", EVENT_DATE, prestataireId);
            when(utilisateurService.createInvitedUtilisateur("Julie", "Martin", "julie@example.com")).thenReturn(utilisateur);
            Reservation reservation = Reservation.builder()
                    .id(UUID.randomUUID()).prestataire(Prestataire.builder().name("Le Taennel").build()).build();
            when(evenementService.createWithConfirmedReservation(utilisateur, "mariage", EVENT_DATE, prestataireId))
                    .thenReturn(reservation);

            ClientInvitationDto result = adminInvitationService.createInvitation(request);

            assertThat(result).isEqualTo(new ClientInvitationDto(
                    utilisateur.getId(), "Julie", "Martin", "julie@example.com", "mariage", EVENT_DATE,
                    "Le Taennel", ClientInvitationStatus.NOT_SENT, null, null));
        }
    }

    // -------------------------------------------------------------------------
    // listInvitations
    // -------------------------------------------------------------------------

    @Nested
    class ListInvitations {

        @Test
        void givenInvitedClientWithoutPendingLink_whenListInvitations_thenReturnsNotSentInvitation() {
            when(actionTokenService.findAllByType(ActionType.CLIENT_INVITATION)).thenReturn(List.of());
            when(utilisateurService.listInvitedUtilisateurs()).thenReturn(List.of(utilisateur));
            stubInvitedEvent();

            List<ClientInvitationDto> result = adminInvitationService.listInvitations();

            assertThat(result).containsExactly(new ClientInvitationDto(
                    utilisateur.getId(), "Julie", "Martin", "julie@example.com", "mariage", EVENT_DATE,
                    "Le Taennel", ClientInvitationStatus.NOT_SENT, null, null));
        }

        @Test
        void givenInvitedClientWithValidLink_whenListInvitations_thenReturnsSentInvitationWithLinkDates() {
            LocalDateTime sentAt = LocalDateTime.now().minusDays(1);
            LocalDateTime expiresAt = sentAt.plusDays(7);
            ActionToken token = ActionToken.builder().type(ActionType.CLIENT_INVITATION).expiresAt(expiresAt).build();
            token.setCreatedAt(sentAt);
            when(actionTokenService.findAllByType(ActionType.CLIENT_INVITATION)).thenReturn(List.of(token));
            when(actionTokenService.readPayload(token)).thenReturn(Map.of("email", "julie@example.com"));
            when(utilisateurService.listInvitedUtilisateurs()).thenReturn(List.of(utilisateur));
            stubInvitedEvent();

            List<ClientInvitationDto> result = adminInvitationService.listInvitations();

            assertThat(result).containsExactly(new ClientInvitationDto(
                    utilisateur.getId(), "Julie", "Martin", "julie@example.com", "mariage", EVENT_DATE,
                    "Le Taennel", ClientInvitationStatus.SENT, sentAt, expiresAt));
        }

        @Test
        void givenInvitedClientWithExpiredLink_whenListInvitations_thenReturnsExpiredInvitationWithLinkDates() {
            LocalDateTime sentAt = LocalDateTime.now().minusDays(8);
            LocalDateTime expiresAt = sentAt.plusDays(7);
            ActionToken token = ActionToken.builder().type(ActionType.CLIENT_INVITATION).expiresAt(expiresAt).build();
            token.setCreatedAt(sentAt);
            when(actionTokenService.findAllByType(ActionType.CLIENT_INVITATION)).thenReturn(List.of(token));
            when(actionTokenService.readPayload(token)).thenReturn(Map.of("email", "julie@example.com"));
            when(utilisateurService.listInvitedUtilisateurs()).thenReturn(List.of(utilisateur));
            stubInvitedEvent();

            List<ClientInvitationDto> result = adminInvitationService.listInvitations();

            assertThat(result).containsExactly(new ClientInvitationDto(
                    utilisateur.getId(), "Julie", "Martin", "julie@example.com", "mariage", EVENT_DATE,
                    "Le Taennel", ClientInvitationStatus.EXPIRED, sentAt, expiresAt));
        }
    }

    // -------------------------------------------------------------------------
    // sendInvitation
    // -------------------------------------------------------------------------

    @Nested
    class SendInvitation {

        @Test
        void givenNoPendingLink_whenSendInvitation_thenCreatesLinkAndSendsMail() {
            when(utilisateurService.getInvitedUtilisateur(utilisateur.getId())).thenReturn(utilisateur);
            when(actionTokenService.findOptionalPendingByEmail(ActionType.CLIENT_INVITATION, "julie@example.com"))
                    .thenReturn(Optional.empty());
            when(actionLinkService.createLink(ActionType.CLIENT_INVITATION, Map.of("email", "julie@example.com")))
                    .thenReturn(ACTION_URL);
            stubInvitedEvent();
            when(clientInvitationMailerService.sendClientInvitationEmail(
                    "julie@example.com", "Julie", "Le Taennel", EVENT_DATE, ACTION_URL)).thenReturn(true);

            adminInvitationService.sendInvitation(utilisateur.getId());

            verify(clientInvitationMailerService).sendClientInvitationEmail(
                    "julie@example.com", "Julie", "Le Taennel", EVENT_DATE, ACTION_URL);
            verify(actionLinkService, never()).rebuildLink(any());
        }

        @Test
        void givenPendingLink_whenSendInvitation_thenRebuildsLinkAndResendsMail() {
            ActionToken token = ActionToken.builder().type(ActionType.CLIENT_INVITATION).build();
            when(utilisateurService.getInvitedUtilisateur(utilisateur.getId())).thenReturn(utilisateur);
            when(actionTokenService.findOptionalPendingByEmail(ActionType.CLIENT_INVITATION, "julie@example.com"))
                    .thenReturn(Optional.of(token));
            when(actionLinkService.rebuildLink(token)).thenReturn(ACTION_URL);
            stubInvitedEvent();
            when(clientInvitationMailerService.sendClientInvitationEmail(
                    "julie@example.com", "Julie", "Le Taennel", EVENT_DATE, ACTION_URL)).thenReturn(true);

            adminInvitationService.sendInvitation(utilisateur.getId());

            verify(clientInvitationMailerService).sendClientInvitationEmail(
                    "julie@example.com", "Julie", "Le Taennel", EVENT_DATE, ACTION_URL);
            verify(actionLinkService, never()).createLink(any(), any());
        }

        @Test
        void givenMailerFailure_whenSendInvitation_thenThrowsNotSentToRollBackTheLink() {
            when(utilisateurService.getInvitedUtilisateur(utilisateur.getId())).thenReturn(utilisateur);
            when(actionTokenService.findOptionalPendingByEmail(ActionType.CLIENT_INVITATION, "julie@example.com"))
                    .thenReturn(Optional.empty());
            when(actionLinkService.createLink(ActionType.CLIENT_INVITATION, Map.of("email", "julie@example.com")))
                    .thenReturn(ACTION_URL);
            stubInvitedEvent();
            when(clientInvitationMailerService.sendClientInvitationEmail(
                    "julie@example.com", "Julie", "Le Taennel", EVENT_DATE, ACTION_URL)).thenReturn(false);

            assertThatThrownBy(() -> adminInvitationService.sendInvitation(utilisateur.getId()))
                    .isInstanceOf(ClientInvitationNotSentException.class);
        }
    }
}
