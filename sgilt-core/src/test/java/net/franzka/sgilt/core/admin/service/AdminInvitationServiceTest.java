package net.franzka.sgilt.core.admin.service;

import net.franzka.sgilt.core.admin.domain.ClientInvitationStatus;
import net.franzka.sgilt.core.admin.dto.ClientInvitationDto;
import net.franzka.sgilt.core.admin.dto.CreateClientInvitationRequest;
import net.franzka.sgilt.core.evenement.dto.EvenementSummaryDto;
import net.franzka.sgilt.core.evenement.service.EvenementService;
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
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminInvitationServiceTest {

    private static final LocalDate EVENT_DATE = LocalDate.of(2027, 6, 12);

    @Mock
    private UtilisateurService utilisateurService;

    @Mock
    private EvenementService evenementService;

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private AdminInvitationService adminInvitationService;

    private final Utilisateur utilisateur = Utilisateur.builder()
            .id(UUID.randomUUID()).firstName("Julie").lastName("Martin").email("julie@example.com").build();

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
        void givenInvitedClient_whenListInvitations_thenReturnsItsEventAndPrestataire() {
            UUID eventId = UUID.randomUUID();
            when(utilisateurService.listInvitedUtilisateurs()).thenReturn(List.of(utilisateur));
            when(evenementService.getUserEvents(utilisateur.getId())).thenReturn(List.of(
                    new EvenementSummaryDto(eventId, "Mon événement", EVENT_DATE, null, null, "mariage", 1, 0)));
            when(reservationService.getReservationSummaries(eventId)).thenReturn(List.of(
                    new ReservationSummaryDto(UUID.randomUUID(), UUID.randomUUID(), "Le Taennel", null,
                            "lieu", "salle", "lieu", "CONFIRMED", 0)));

            List<ClientInvitationDto> result = adminInvitationService.listInvitations();

            assertThat(result).containsExactly(new ClientInvitationDto(
                    utilisateur.getId(), "Julie", "Martin", "julie@example.com", "mariage", EVENT_DATE,
                    "Le Taennel", ClientInvitationStatus.NOT_SENT, null, null));
        }
    }
}
