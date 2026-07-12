package net.franzka.sgilt.core.reservation.event.mapper;

import net.franzka.sgilt.core.reservation.domain.Reservation;
import net.franzka.sgilt.core.reservation.event.events.ReservationCreatedEvent;
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
}
