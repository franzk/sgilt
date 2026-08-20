package net.franzka.sgilt.core.reservation.event.mapper;

import net.franzka.sgilt.core.reservation.domain.Reservation;
import net.franzka.sgilt.core.reservation.domain.ReservationStatus;
import net.franzka.sgilt.core.reservation.event.FeedItemType;
import net.franzka.sgilt.core.reservation.event.reservationcreated.ReservationCreatedEvent;
import net.franzka.sgilt.core.reservation.event.reservationfeeditemadded.ReservationFeedItemAddedEvent;
import net.franzka.sgilt.core.reservation.event.reservationstatuschanged.ReservationStatusChangedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Construit les évènements de domaine du sous-domaine réservation, à destination de RabbitMQ —
 * distinct de {@link net.franzka.sgilt.core.reservation.mapper.ReservationMapper}, qui ne mappe
 * que vers des DTOs REST. Un nouvel évènement ajoute une méthode ici, pas un nouveau fichier.
 */
@Mapper(componentModel = "spring")
public interface ReservationEventMapper {

    @Mapping(source = "id",                            target = "reservationId")
    @Mapping(source = "prestataire.utilisateur.id",    target = "recipientUserId")
    @Mapping(source = "prestataire.utilisateur.email", target = "recipientEmail")
    @Mapping(source = "utilisateur.firstName",          target = "clientFirstName")
    @Mapping(source = "utilisateur.lastName",           target = "clientLastName")
    @Mapping(source = "evenement.title",                target = "eventTitle")
    @Mapping(source = "date",                           target = "eventDate")
    ReservationCreatedEvent toReservationCreatedEvent(Reservation reservation);

    /**
     * Construit l'évènement générique de changement de statut à destination du client, pour toute
     * transition déclenchée par le prestataire (refus, annulation post-confirmation — le prestataire
     * ne pilote plus la prise de contact ni la confirmation, désormais déclarées par le client).
     * {@code status} est passé séparément — c'est le nouveau statut, déjà affecté à {@code reservation}
     * par l'appelant au moment de l'appel. {@code Prestataire.name} est déjà un champ unique, mappable
     * directement — contrairement à {@link #toStatusChangedEventForPro}.
     *
     * @param reservation la réservation dont le statut vient de changer
     * @param status      le nouveau statut
     * @return l'évènement à publier
     */
    @Mapping(source = "reservation.id",                target = "reservationId")
    @Mapping(source = "reservation.evenement.id",      target = "eventId")
    @Mapping(source = "reservation.utilisateur.id",    target = "recipientUserId")
    @Mapping(source = "reservation.utilisateur.email", target = "recipientEmail")
    @Mapping(source = "status",                        target = "status")
    @Mapping(source = "reservation.prestataire.name",  target = "actorName")
    @Mapping(target = "actorRole",                     constant = "PRO")
    @Mapping(source = "reservation.evenement.title",   target = "eventTitle")
    @Mapping(source = "reservation.date",              target = "eventDate")
    ReservationStatusChangedEvent toStatusChangedEventForClient(Reservation reservation, ReservationStatus status);

    /**
     * Construit l'évènement générique de changement de statut à destination du prestataire, pour toute
     * transition déclenchée par le client (prise de contact, confirmation, annulation — à tout stade).
     * {@code actorName} concatène prénom et nom du client — seule exception {@code expression} de ce
     * mapper, pour une simple concaténation d'un champ, toujours dans le périmètre "construire
     * l'évènement" (contrairement à {@code NotificationEventMapper}, où construire des params/href
     * entiers en annotation n'aurait apporté aucun gain).
     *
     * @param reservation la réservation dont le statut vient de changer, à l'initiative du client
     * @param status      le nouveau statut
     * @return l'évènement à publier
     */
    @Mapping(source = "reservation.id",                            target = "reservationId")
    @Mapping(source = "reservation.evenement.id",                  target = "eventId")
    @Mapping(source = "reservation.prestataire.utilisateur.id",    target = "recipientUserId")
    @Mapping(source = "reservation.prestataire.utilisateur.email", target = "recipientEmail")
    @Mapping(source = "status",                                    target = "status")
    @Mapping(target = "actorName",                                 expression = "java(reservation.getUtilisateur().getFirstName() + \" \" + reservation.getUtilisateur().getLastName())")
    @Mapping(target = "actorRole",                                 constant = "USER")
    @Mapping(source = "reservation.evenement.title",               target = "eventTitle")
    @Mapping(source = "reservation.date",                          target = "eventDate")
    ReservationStatusChangedEvent toStatusChangedEventForPro(Reservation reservation, ReservationStatus status);

    /**
     * Construit l'évènement générique d'ajout au feed à destination du client, pour un ajout
     * déclenché par le prestataire. {@code itemType} est passé séparément — même raison que
     * {@code status} dans {@link #toStatusChangedEventForClient}.
     *
     * @param reservation la réservation dont le feed vient de recevoir un élément
     * @param itemType    le type d'élément ajouté (note ou document)
     * @return l'évènement à publier
     */
    @Mapping(source = "reservation.id",                target = "reservationId")
    @Mapping(source = "reservation.evenement.id",      target = "eventId")
    @Mapping(source = "reservation.utilisateur.id",    target = "recipientUserId")
    @Mapping(source = "reservation.utilisateur.email", target = "recipientEmail")
    @Mapping(source = "itemType",                      target = "itemType")
    @Mapping(source = "reservation.prestataire.name",  target = "actorName")
    @Mapping(target = "actorRole",                     constant = "PRO")
    @Mapping(source = "reservation.evenement.title",   target = "eventTitle")
    @Mapping(source = "reservation.date",              target = "eventDate")
    ReservationFeedItemAddedEvent toFeedItemAddedEventForClient(Reservation reservation, FeedItemType itemType);

    /**
     * Construit l'évènement générique d'ajout au feed à destination du prestataire, pour un ajout
     * déclenché par le client. Même exception {@code expression} que {@link #toStatusChangedEventForPro}
     * pour {@code actorName}.
     *
     * @param reservation la réservation dont le feed vient de recevoir un élément
     * @param itemType    le type d'élément ajouté (note ou document)
     * @return l'évènement à publier
     */
    @Mapping(source = "reservation.id",                            target = "reservationId")
    @Mapping(source = "reservation.evenement.id",                  target = "eventId")
    @Mapping(source = "reservation.prestataire.utilisateur.id",    target = "recipientUserId")
    @Mapping(source = "reservation.prestataire.utilisateur.email", target = "recipientEmail")
    @Mapping(source = "itemType",                                  target = "itemType")
    @Mapping(target = "actorName",                                 expression = "java(reservation.getUtilisateur().getFirstName() + \" \" + reservation.getUtilisateur().getLastName())")
    @Mapping(target = "actorRole",                                 constant = "USER")
    @Mapping(source = "reservation.evenement.title",               target = "eventTitle")
    @Mapping(source = "reservation.date",                          target = "eventDate")
    ReservationFeedItemAddedEvent toFeedItemAddedEventForPro(Reservation reservation, FeedItemType itemType);
}
