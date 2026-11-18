package net.franzka.sgilt.core.reservation.event;

import net.franzka.sgilt.core.reservation.domain.FeedCaller;
import net.franzka.sgilt.core.reservation.domain.Reservation;
import net.franzka.sgilt.core.reservation.domain.ReservationStatus;
import net.franzka.sgilt.core.reservation.event.mapper.ReservationEventMapper;
import net.franzka.sgilt.core.reservation.event.reservationaddedbysgilt.ReservationAddedBySgiltEvent;
import net.franzka.sgilt.core.reservation.event.reservationcontactreminderdue.ReservationContactReminderDueEvent;
import net.franzka.sgilt.core.reservation.event.reservationcreated.ReservationCreatedEvent;
import net.franzka.sgilt.core.reservation.event.reservationfeeditemadded.ReservationFeedItemAddedEvent;
import net.franzka.sgilt.core.reservation.event.reservationstatuschanged.ReservationStatusChangedEvent;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import net.franzka.sgilt.core.utilisateur.domain.UtilisateurStatus;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationEventPublisherTest {

    @Mock
    private ReservationEventMapper reservationEventMapper;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private ReservationEventPublisher reservationEventPublisher;

    // réservation dont le client est dans le statut donné, au statut CONFIRMED
    private Reservation reservationWithClient(UtilisateurStatus clientStatus) {
        return Reservation.builder()
                .id(UUID.randomUUID())
                .status(ReservationStatus.CONFIRMED)
                .utilisateur(Utilisateur.builder().id(UUID.randomUUID()).status(clientStatus).build())
                .build();
    }

    // -------------------------------------------------------------------------
    // évènements à destination du prestataire
    // -------------------------------------------------------------------------

    @Nested
    class PublishCreated {

        @Test
        void givenReservation_whenPublishCreated_thenPublishesMappedEvent() {
            Reservation reservation = reservationWithClient(UtilisateurStatus.ACTIVE);
            ReservationCreatedEvent event = mock(ReservationCreatedEvent.class);
            when(reservationEventMapper.toReservationCreatedEvent(reservation)).thenReturn(event);

            reservationEventPublisher.publishCreated(reservation);

            verify(applicationEventPublisher).publishEvent(event);
        }
    }

    @Nested
    class PublishAddedBySgilt {

        @Test
        void givenInvitedClient_whenPublishAddedBySgilt_thenPublishesMappedEventToPro() {
            Reservation reservation = reservationWithClient(UtilisateurStatus.INVITED);
            ReservationAddedBySgiltEvent event = mock(ReservationAddedBySgiltEvent.class);
            when(reservationEventMapper.toReservationAddedBySgiltEvent(reservation)).thenReturn(event);

            reservationEventPublisher.publishAddedBySgilt(reservation);

            verify(applicationEventPublisher).publishEvent(event);
        }
    }

    @Nested
    class PublishStatusChangedToPro {

        @Test
        void givenReservation_whenPublishStatusChangedToPro_thenPublishesMappedEventWithCurrentStatus() {
            Reservation reservation = reservationWithClient(UtilisateurStatus.ACTIVE);
            ReservationStatusChangedEvent event = mock(ReservationStatusChangedEvent.class);
            when(reservationEventMapper.toStatusChangedEventForPro(reservation, ReservationStatus.CONFIRMED)).thenReturn(event);

            reservationEventPublisher.publishStatusChangedToPro(reservation);

            verify(applicationEventPublisher).publishEvent(event);
        }
    }

    // -------------------------------------------------------------------------
    // évènements à destination du client
    // -------------------------------------------------------------------------

    @Nested
    class PublishStatusChangedToClient {

        @Test
        void givenActiveClient_whenPublishStatusChangedToClient_thenPublishesMappedEventWithCurrentStatus() {
            Reservation reservation = reservationWithClient(UtilisateurStatus.ACTIVE);
            ReservationStatusChangedEvent event = mock(ReservationStatusChangedEvent.class);
            when(reservationEventMapper.toStatusChangedEventForClient(reservation, ReservationStatus.CONFIRMED)).thenReturn(event);

            reservationEventPublisher.publishStatusChangedToClient(reservation);

            verify(applicationEventPublisher).publishEvent(event);
        }

        @Test
        void givenInvitedClient_whenPublishStatusChangedToClient_thenPublishesNothing() {
            Reservation reservation = reservationWithClient(UtilisateurStatus.INVITED);

            reservationEventPublisher.publishStatusChangedToClient(reservation);

            verifyNoInteractions(reservationEventMapper, applicationEventPublisher);
        }
    }

    @Nested
    class PublishFeedItemAdded {

        @Test
        void givenNonPersonalItemFromPrestataireAndActiveClient_whenPublishFeedItemAdded_thenPublishesEventForClient() {
            Reservation reservation = reservationWithClient(UtilisateurStatus.ACTIVE);
            ReservationFeedItemAddedEvent event = mock(ReservationFeedItemAddedEvent.class);
            when(reservationEventMapper.toFeedItemAddedEventForClient(reservation, FeedItemType.DOCUMENT)).thenReturn(event);

            reservationEventPublisher.publishFeedItemAdded(reservation, FeedCaller.PRESTATAIRE, FeedItemType.DOCUMENT, false);

            verify(applicationEventPublisher).publishEvent(event);
        }

        @Test
        void givenNonPersonalItemFromPrestataireAndInvitedClient_whenPublishFeedItemAdded_thenPublishesNothing() {
            Reservation reservation = reservationWithClient(UtilisateurStatus.INVITED);

            reservationEventPublisher.publishFeedItemAdded(reservation, FeedCaller.PRESTATAIRE, FeedItemType.DOCUMENT, false);

            verifyNoInteractions(reservationEventMapper, applicationEventPublisher);
        }

        @Test
        void givenNonPersonalItemFromClient_whenPublishFeedItemAdded_thenPublishesEventForPro() {
            Reservation reservation = reservationWithClient(UtilisateurStatus.ACTIVE);
            ReservationFeedItemAddedEvent event = mock(ReservationFeedItemAddedEvent.class);
            when(reservationEventMapper.toFeedItemAddedEventForPro(reservation, FeedItemType.NOTE)).thenReturn(event);

            reservationEventPublisher.publishFeedItemAdded(reservation, FeedCaller.CLIENT, FeedItemType.NOTE, false);

            verify(applicationEventPublisher).publishEvent(event);
        }

        @Test
        void givenPersonalItem_whenPublishFeedItemAdded_thenPublishesNothing() {
            Reservation reservation = reservationWithClient(UtilisateurStatus.ACTIVE);

            reservationEventPublisher.publishFeedItemAdded(reservation, FeedCaller.PRESTATAIRE, FeedItemType.NOTE, true);

            verifyNoInteractions(reservationEventMapper, applicationEventPublisher);
        }
    }

    @Nested
    class PublishContactReminderDue {

        @Test
        void givenActiveClient_whenPublishContactReminderDue_thenPublishesMappedEvent() {
            Reservation reservation = reservationWithClient(UtilisateurStatus.ACTIVE);
            ReservationContactReminderDueEvent event = mock(ReservationContactReminderDueEvent.class);
            when(reservationEventMapper.toContactReminderDueEvent(reservation)).thenReturn(event);

            reservationEventPublisher.publishContactReminderDue(reservation);

            verify(applicationEventPublisher).publishEvent(event);
        }

        @Test
        void givenInvitedClient_whenPublishContactReminderDue_thenPublishesNothing() {
            Reservation reservation = reservationWithClient(UtilisateurStatus.INVITED);

            reservationEventPublisher.publishContactReminderDue(reservation);

            verifyNoInteractions(reservationEventMapper, applicationEventPublisher);
        }
    }
}
