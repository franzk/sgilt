package net.franzka.sgilt.core.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.admin.domain.ClientInvitationStatus;
import net.franzka.sgilt.core.admin.dto.ClientInvitationDto;
import net.franzka.sgilt.core.admin.dto.CreateClientInvitationRequest;
import net.franzka.sgilt.core.evenement.dto.EvenementSummaryDto;
import net.franzka.sgilt.core.evenement.service.EvenementService;
import net.franzka.sgilt.core.reservation.domain.Reservation;
import net.franzka.sgilt.core.reservation.service.ReservationService;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import net.franzka.sgilt.core.utilisateur.service.UtilisateurService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Service métier des invitations client du back-office : un client dont le deal s'est conclu hors
 * Sgilt (ex. le Taennel) est créé avec son événement et sa réservation directement confirmée, en
 * statut INVITED, sans qu'aucun mail ne lui parte ; l'invitation lui est envoyée plus tard depuis
 * le BO.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AdminInvitationService {

    private final UtilisateurService utilisateurService;
    private final EvenementService evenementService;
    private final ReservationService reservationService;

    /**
     * Crée le client invité (statut INVITED), son événement et sa réservation confirmée chez le
     * prestataire. Le prestataire est notifié de la réservation confirmée ; le client ne reçoit
     * rien. Le compte Keycloak est créé par l'appelant, avant.
     *
     * @param request le client, le type et la date de l'événement, le prestataire
     * @return l'invitation créée, non envoyée
     */
    public ClientInvitationDto createInvitation(CreateClientInvitationRequest request) {
        Utilisateur utilisateur = utilisateurService.createInvitedUtilisateur(
                request.firstName(), request.lastName(), request.email());
        Reservation reservation = evenementService.createWithConfirmedReservation(
                utilisateur, request.eventType(), request.eventDate(), request.prestataireId());
        log.info("createInvitation — client {} invité, réservation {} confirmée", request.email(), reservation.getId());
        return toDto(utilisateur, request.eventType(), request.eventDate(), reservation.getPrestataire().getName());
    }

    /**
     * Liste les invitations des clients qui n'ont pas encore activé leur compte, avec leur
     * événement et le prestataire réservé.
     *
     * @return les invitations en cours
     */
    public List<ClientInvitationDto> listInvitations() {
        return utilisateurService.listInvitedUtilisateurs().stream()
                .map(this::toDto)
                .toList();
    }

    // Un client invité a, par construction, un seul événement portant une seule réservation.
    private ClientInvitationDto toDto(Utilisateur utilisateur) {
        EvenementSummaryDto evenement = evenementService.getUserEvents(utilisateur.getId()).getFirst();
        String prestataireName = reservationService.getReservationSummaries(evenement.id()).getFirst().prestataireName();
        return toDto(utilisateur, evenement.eventType(), evenement.date(), prestataireName);
    }

    private ClientInvitationDto toDto(Utilisateur utilisateur, String eventType, LocalDate eventDate,
                                      String prestataireName) {
        return new ClientInvitationDto(
                utilisateur.getId(), utilisateur.getFirstName(), utilisateur.getLastName(), utilisateur.getEmail(),
                eventType, eventDate, prestataireName, ClientInvitationStatus.NOT_SENT, null, null);
    }
}
