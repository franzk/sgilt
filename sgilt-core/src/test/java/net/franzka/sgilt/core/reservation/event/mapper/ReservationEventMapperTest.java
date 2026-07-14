package net.franzka.sgilt.core.reservation.event.mapper;

import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.reservation.domain.Reservation;
import net.franzka.sgilt.core.reservation.domain.ReservationStatus;
import net.franzka.sgilt.core.reservation.event.ActorRole;
import net.franzka.sgilt.core.reservation.event.reservationcreated.ReservationCreatedEvent;
import net.franzka.sgilt.core.reservation.event.reservationstatuschanged.ReservationStatusChangedEvent;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationEventMapperTest {

    private final ReservationEventMapper mapper = new ReservationEventMapperImpl();

    @Nested
    class ToReservationCreatedEvent {

        @Test
        void givenReservation_whenToReservationCreatedEvent_thenMapsAllFields() {
            UUID reservationId = UUID.randomUUID();
            UUID prestataireUserId = UUID.randomUUID();
            LocalDate date = LocalDate.now();

            Utilisateur prestataireUtilisateur = Utilisateur.builder()
                    .id(prestataireUserId)
                    .email("presta@example.com")
                    .build();
            Prestataire prestataire = Prestataire.builder()
                    .utilisateur(prestataireUtilisateur)
                    .build();
            Utilisateur client = Utilisateur.builder()
                    .firstName("Sophie")
                    .lastName("Leroy")
                    .build();
            Evenement evenement = Evenement.builder()
                    .title("Anniversaire de Paul")
                    .build();
            Reservation reservation = Reservation.builder()
                    .id(reservationId)
                    .prestataire(prestataire)
                    .utilisateur(client)
                    .evenement(evenement)
                    .date(date)
                    .status(ReservationStatus.NEW)
                    .build();

            ReservationCreatedEvent event = mapper.toReservationCreatedEvent(reservation);

            assertThat(event.reservationId()).isEqualTo(reservationId);
            assertThat(event.recipientUserId()).isEqualTo(prestataireUserId);
            assertThat(event.recipientEmail()).isEqualTo("presta@example.com");
            assertThat(event.clientFirstName()).isEqualTo("Sophie");
            assertThat(event.clientLastName()).isEqualTo("Leroy");
            assertThat(event.eventTitle()).isEqualTo("Anniversaire de Paul");
            assertThat(event.eventDate()).isEqualTo(date);
        }
    }

    @Nested
    class ToStatusChangedEventForClient {

        @Test
        void givenReservationAndStatus_whenToStatusChangedEventForClient_thenMapsAllFields() {
            UUID reservationId = UUID.randomUUID();
            UUID clientId = UUID.randomUUID();
            UUID eventId = UUID.randomUUID();
            LocalDate date = LocalDate.now();

            Utilisateur client = Utilisateur.builder()
                    .id(clientId)
                    .email("client@example.com")
                    .build();
            Prestataire prestataire = Prestataire.builder()
                    .name("Studio Fleur")
                    .build();
            Evenement evenement = Evenement.builder()
                    .id(eventId)
                    .title("Anniversaire de Paul")
                    .build();
            Reservation reservation = Reservation.builder()
                    .id(reservationId)
                    .prestataire(prestataire)
                    .utilisateur(client)
                    .evenement(evenement)
                    .date(date)
                    .status(ReservationStatus.CONFIRMED)
                    .build();

            ReservationStatusChangedEvent event = mapper.toStatusChangedEventForClient(reservation, ReservationStatus.CONFIRMED);

            assertThat(event.reservationId()).isEqualTo(reservationId);
            assertThat(event.eventId()).isEqualTo(eventId);
            assertThat(event.recipientUserId()).isEqualTo(clientId);
            assertThat(event.recipientEmail()).isEqualTo("client@example.com");
            assertThat(event.status()).isEqualTo(ReservationStatus.CONFIRMED);
            assertThat(event.actorName()).isEqualTo("Studio Fleur");
            assertThat(event.actorRole()).isEqualTo(ActorRole.PRO);
            assertThat(event.eventTitle()).isEqualTo("Anniversaire de Paul");
            assertThat(event.eventDate()).isEqualTo(date);
        }
    }

    @Nested
    class ToStatusChangedEventForPro {

        @Test
        void givenReservationAndStatus_whenToStatusChangedEventForPro_thenMapsAllFields() {
            UUID reservationId = UUID.randomUUID();
            UUID prestataireUserId = UUID.randomUUID();
            UUID eventId = UUID.randomUUID();
            LocalDate date = LocalDate.now();

            Utilisateur prestataireUtilisateur = Utilisateur.builder()
                    .id(prestataireUserId)
                    .email("presta@example.com")
                    .build();
            Prestataire prestataire = Prestataire.builder()
                    .utilisateur(prestataireUtilisateur)
                    .build();
            Utilisateur client = Utilisateur.builder()
                    .firstName("Sophie")
                    .lastName("Leroy")
                    .build();
            Evenement evenement = Evenement.builder()
                    .id(eventId)
                    .title("Anniversaire de Paul")
                    .build();
            Reservation reservation = Reservation.builder()
                    .id(reservationId)
                    .prestataire(prestataire)
                    .utilisateur(client)
                    .evenement(evenement)
                    .date(date)
                    .status(ReservationStatus.CANCELED_BY_CLIENT_PRE_CONTACT)
                    .build();

            ReservationStatusChangedEvent event =
                    mapper.toStatusChangedEventForPro(reservation, ReservationStatus.CANCELED_BY_CLIENT_PRE_CONTACT);

            assertThat(event.reservationId()).isEqualTo(reservationId);
            assertThat(event.eventId()).isEqualTo(eventId);
            assertThat(event.recipientUserId()).isEqualTo(prestataireUserId);
            assertThat(event.recipientEmail()).isEqualTo("presta@example.com");
            assertThat(event.status()).isEqualTo(ReservationStatus.CANCELED_BY_CLIENT_PRE_CONTACT);
            assertThat(event.actorName()).isEqualTo("Sophie Leroy");
            assertThat(event.actorRole()).isEqualTo(ActorRole.USER);
            assertThat(event.eventTitle()).isEqualTo("Anniversaire de Paul");
            assertThat(event.eventDate()).isEqualTo(date);
        }
    }
}
