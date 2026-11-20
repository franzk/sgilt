package net.franzka.sgilt.core.reservation.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.reservation.domain.FeedCaller;
import net.franzka.sgilt.core.reservation.domain.Reservation;
import net.franzka.sgilt.core.reservation.event.mapper.ReservationEventMapper;
import net.franzka.sgilt.core.utilisateur.domain.UtilisateurStatus;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * Point unique de publication des évènements de domaine du sous-domaine réservation : construit
 * l'évènement via {@link ReservationEventMapper} et le publie (republié sur RabbitMQ après commit
 * par le listener dédié). Porte la règle commune aux évènements destinés au client : rien n'est
 * publié tant que son compte n'est pas {@link UtilisateurStatus#ACTIVE} — un client invité depuis
 * le BO ne reçoit rien avant l'envoi de son invitation.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationEventPublisher {

    private final ReservationEventMapper reservationEventMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Publie l'évènement « réservation créée » à destination du prestataire.
     *
     * @param reservation la réservation qui vient d'être créée
     */
    public void publishCreated(Reservation reservation) {
        applicationEventPublisher.publishEvent(reservationEventMapper.toReservationCreatedEvent(reservation));
    }

    /**
     * Publie l'évènement « réservation ajoutée par Sgilt » à destination du prestataire.
     *
     * @param reservation la réservation confirmée qui vient d'être créée depuis le BO
     */
    public void publishAddedBySgilt(Reservation reservation) {
        applicationEventPublisher.publishEvent(reservationEventMapper.toReservationAddedBySgiltEvent(reservation));
    }

    /**
     * Publie le changement de statut, à l'initiative du client, à destination du prestataire.
     *
     * @param reservation la réservation dont le statut vient de changer (nouveau statut déjà affecté)
     */
    public void publishStatusChangedToPro(Reservation reservation) {
        applicationEventPublisher.publishEvent(
                reservationEventMapper.toStatusChangedEventForPro(reservation, reservation.getStatus()));
    }

    /**
     * Publie le changement de statut, à l'initiative du prestataire, à destination du client — si
     * son compte est actif.
     *
     * @param reservation la réservation dont le statut vient de changer (nouveau statut déjà affecté)
     */
    public void publishStatusChangedToClient(Reservation reservation) {
        publishToClient(reservation,
                () -> reservationEventMapper.toStatusChangedEventForClient(reservation, reservation.getStatus()));
    }

    /**
     * Publie l'ajout d'un élément au feed à destination de l'autre partie — jamais pour un élément
     * personnel, visible par son seul auteur. Vers le client, seulement si son compte est actif.
     *
     * @param reservation la réservation dont le feed vient de recevoir un élément
     * @param caller      le rôle de l'auteur (l'autre partie est notifiée)
     * @param itemType    le type d'élément ajouté
     * @param isPersonal  si l'élément est personnel
     */
    public void publishFeedItemAdded(Reservation reservation, FeedCaller caller, FeedItemType itemType, boolean isPersonal) {
        if (isPersonal) {
            return;
        }
        if (caller == FeedCaller.PRESTATAIRE) {
            publishToClient(reservation, () -> reservationEventMapper.toFeedItemAddedEventForClient(reservation, itemType));
        } else {
            applicationEventPublisher.publishEvent(reservationEventMapper.toFeedItemAddedEventForPro(reservation, itemType));
        }
    }

    /**
     * Publie le rappel au client d'une réservation restée en attente de contact — si son compte est
     * actif.
     *
     * @param reservation la réservation à rappeler
     */
    public void publishContactReminderDue(Reservation reservation) {
        publishToClient(reservation, () -> reservationEventMapper.toContactReminderDueEvent(reservation));
    }

    // règle unique des évènements destinés au client : construits et publiés seulement si son compte est actif
    private void publishToClient(Reservation reservation, Supplier<?> event) {
        if (reservation.getUtilisateur().getStatus() != UtilisateurStatus.ACTIVE) {
            log.info("Évènement client non publié pour la réservation {} : client en statut {}",
                    reservation.getId(), reservation.getUtilisateur().getStatus());
            return;
        }
        applicationEventPublisher.publishEvent(event.get());
    }
}
