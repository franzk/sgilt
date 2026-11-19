package net.franzka.sgilt.core.admin.service;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.admin.domain.ClientInvitationStatus;
import net.franzka.sgilt.core.admin.dto.ClientInvitationDto;
import net.franzka.sgilt.core.admin.dto.CreateClientInvitationRequest;
import net.franzka.sgilt.core.admin.dto.InvitedReservation;
import net.franzka.sgilt.core.admin.exception.ClientInvitationNotSentException;
import net.franzka.sgilt.core.admin.mailer.ClientInvitationMailerService;
import net.franzka.sgilt.core.evenement.dto.EvenementSummaryDto;
import net.franzka.sgilt.core.evenement.service.EvenementService;
import net.franzka.sgilt.core.jwt.domain.ActionToken;
import net.franzka.sgilt.core.jwt.domain.ActionType;
import net.franzka.sgilt.core.jwt.service.ActionLinkService;
import net.franzka.sgilt.core.jwt.service.ActionTokenService;
import net.franzka.sgilt.core.reservation.domain.Reservation;
import net.franzka.sgilt.core.reservation.dto.ReservationSummaryDto;
import net.franzka.sgilt.core.reservation.service.ReservationService;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import net.franzka.sgilt.core.utilisateur.service.UtilisateurService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service métier des invitations client du back-office : un client dont le deal s'est conclu hors
 * Sgilt (ex. le Taennel) est créé avec son événement et sa réservation directement confirmée, en
 * statut INVITED, sans qu'aucun mail ne lui parte ; l'invitation lui est envoyée plus tard depuis
 * le BO, avec un lien d'activation porté par un jeton d'action {@link ActionType#CLIENT_INVITATION}.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AdminInvitationService {

    private final UtilisateurService utilisateurService;
    private final EvenementService evenementService;
    private final ReservationService reservationService;
    private final ActionTokenService actionTokenService;
    private final ActionLinkService actionLinkService;
    private final ClientInvitationMailerService clientInvitationMailerService;

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
        InvitedEvent invitedEvent = new InvitedEvent(
                reservation.getEvenement().getId(), request.eventType(), request.eventDate(),
                reservation.getId(), reservation.getPrestataire().getName());
        return toDto(utilisateur, invitedEvent, null);
    }

    /**
     * Liste les invitations des clients qui n'ont pas encore activé leur compte, avec leur
     * événement, le prestataire réservé et l'état de l'invitation (envoyée si un lien d'activation
     * est en attente).
     *
     * @return les invitations en cours
     */
    public List<ClientInvitationDto> listInvitations() {
        Map<String, ActionToken> pendingTokensByEmail = actionTokenService.findAllByType(ActionType.CLIENT_INVITATION).stream()
                .collect(Collectors.toMap(
                        token -> (String) actionTokenService.readPayload(token).get("email"), Function.identity()));
        return utilisateurService.listInvitedUtilisateurs().stream()
                .map(utilisateur -> toDto(
                        utilisateur, loadInvitedEvent(utilisateur), pendingTokensByEmail.get(utilisateur.getEmail())))
                .toList();
    }

    /**
     * Envoie le mail d'invitation au client, avec son lien d'activation : créé au premier envoi,
     * relancé ensuite (même lien, période de validité réinitialisée). Si le mail ne part pas,
     * l'exception levée annule la transaction : le lien n'est ni créé ni prolongé, et l'invitation
     * garde son état — son statut se déduit de l'existence du lien.
     *
     * @param utilisateurId l'identifiant du client invité
     * @throws net.franzka.sgilt.core.utilisateur.exception.UtilisateurNotFoundException si le client
     *         n'existe pas ou a déjà activé son compte
     * @throws ClientInvitationNotSentException si l'appel au mailer a échoué
     */
    public void sendInvitation(UUID utilisateurId) {
        Utilisateur utilisateur = utilisateurService.getInvitedUtilisateur(utilisateurId);
        String email = utilisateur.getEmail();

        String actionUrl = actionTokenService.findOptionalPendingByEmail(ActionType.CLIENT_INVITATION, email)
                .map(actionLinkService::rebuildLink)
                .orElseGet(() -> actionLinkService.createLink(ActionType.CLIENT_INVITATION, Map.of("email", email)));

        InvitedEvent invitedEvent = loadInvitedEvent(utilisateur);
        log.info("sendInvitation — envoi de l'invitation à {}", email);
        boolean sent = clientInvitationMailerService.sendClientInvitationEmail(
                email, utilisateur.getFirstName(), invitedEvent.prestataireName(), invitedEvent.date(), actionUrl);
        if (!sent) {
            throw new ClientInvitationNotSentException(email);
        }
    }

    /**
     * Active le compte d'un client invité, au moment où il choisit son mot de passe via le lien de
     * l'invitation : passe de INVITED à ACTIVE et enregistre son acceptation des CGU (cochée sur la
     * page d'activation).
     *
     * @param email l'email du client invité
     * @return sa réservation et son événement, ce qu'il retrouve en arrivant
     * @throws net.franzka.sgilt.core.utilisateur.exception.UtilisateurNotFoundException si le client
     *         n'existe pas ou a déjà activé son compte
     */
    public InvitedReservation activateInvitation(String email) {
        Utilisateur utilisateur = utilisateurService.activateInvitedUtilisateur(email);
        utilisateurService.acceptCgu(utilisateur);
        InvitedEvent invitedEvent = loadInvitedEvent(utilisateur);
        log.info("activateInvitation — compte de {} activé", email);
        return new InvitedReservation(invitedEvent.eventId(), invitedEvent.reservationId());
    }

    // L'événement d'un client invité et sa réservation, tels que l'invitation les présente.
    private record InvitedEvent(UUID eventId, String eventType, LocalDate date,
                                UUID reservationId, String prestataireName) {}

    // Un client invité a, par construction, un seul événement portant une seule réservation.
    private InvitedEvent loadInvitedEvent(Utilisateur utilisateur) {
        EvenementSummaryDto evenement = evenementService.getUserEvents(utilisateur.getId()).getFirst();
        ReservationSummaryDto reservation = reservationService.getReservationSummaries(evenement.id()).getFirst();
        return new InvitedEvent(evenement.id(), evenement.eventType(), evenement.date(),
                reservation.id(), reservation.prestataireName());
    }

    // L'invitation est envoyée si un lien d'activation est en attente, non envoyée sinon.
    private ClientInvitationDto toDto(Utilisateur utilisateur, InvitedEvent invitedEvent,
                                      @Nullable ActionToken pendingToken) {
        ClientInvitationStatus status = pendingToken == null ? ClientInvitationStatus.NOT_SENT : ClientInvitationStatus.SENT;
        return new ClientInvitationDto(
                utilisateur.getId(), utilisateur.getFirstName(), utilisateur.getLastName(), utilisateur.getEmail(),
                invitedEvent.eventType(), invitedEvent.date(), invitedEvent.prestataireName(), status,
                pendingToken == null ? null : pendingToken.getCreatedAt(),
                pendingToken == null ? null : pendingToken.getExpiresAt());
    }
}
