package net.franzka.sgilt.core.reservation.service;

import net.franzka.sgilt.core.reservation.domain.Reservation;
import net.franzka.sgilt.core.reservation.domain.ReservationReminder;
import net.franzka.sgilt.core.reservation.domain.ReservationReminderType;
import net.franzka.sgilt.core.reservation.domain.ReservationStatus;
import net.franzka.sgilt.core.reservation.event.mapper.ReservationEventMapper;
import net.franzka.sgilt.core.reservation.event.reservationcontactreminderdue.ReservationContactReminderDueEvent;
import net.franzka.sgilt.core.reservation.repository.ReservationReminderRepository;
import net.franzka.sgilt.core.reservation.repository.ReservationRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationReminderServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationReminderRepository reservationReminderRepository;

    @Mock
    private ReservationEventMapper reservationEventMapper;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private ReservationReminderService service;

    @Nested
    class SendContactPendingReminders {

        @Test
        void givenNewReservationNotYetReminded_whenSendContactPendingReminders_thenTracesReminderAndPublishesEvent() {
            Reservation reservation = Reservation.builder().id(UUID.randomUUID()).status(ReservationStatus.NEW).build();
            ReservationContactReminderDueEvent event = new ReservationContactReminderDueEvent(
                    reservation.getId(), UUID.randomUUID(), UUID.randomUUID(), "client@example.com",
                    "Sophie", "DJ Max", "Anniversaire de Paul", LocalDate.now());
            when(reservationRepository.findByStatusAndCreatedAtBefore(eq(ReservationStatus.NEW), any()))
                    .thenReturn(List.of(reservation));
            when(reservationReminderRepository.existsByReservationIdAndType(reservation.getId(), ReservationReminderType.CONTACT_PENDING))
                    .thenReturn(false);
            when(reservationEventMapper.toContactReminderDueEvent(reservation)).thenReturn(event);

            service.sendContactPendingReminders();

            ArgumentCaptor<ReservationReminder> captor = ArgumentCaptor.forClass(ReservationReminder.class);
            verify(reservationReminderRepository).save(captor.capture());
            assertThat(captor.getValue().getReservation()).isEqualTo(reservation);
            assertThat(captor.getValue().getType()).isEqualTo(ReservationReminderType.CONTACT_PENDING);
            assertThat(captor.getValue().getSentAt()).isNotNull();
            verify(applicationEventPublisher).publishEvent(event);
        }

        @Test
        void givenNewReservationAlreadyReminded_whenSendContactPendingReminders_thenSendsNothing() {
            Reservation reservation = Reservation.builder().id(UUID.randomUUID()).status(ReservationStatus.NEW).build();
            when(reservationRepository.findByStatusAndCreatedAtBefore(eq(ReservationStatus.NEW), any()))
                    .thenReturn(List.of(reservation));
            when(reservationReminderRepository.existsByReservationIdAndType(reservation.getId(), ReservationReminderType.CONTACT_PENDING))
                    .thenReturn(true);

            service.sendContactPendingReminders();

            verify(reservationReminderRepository, never()).save(any());
            verify(applicationEventPublisher, never()).publishEvent(any());
        }

        @Test
        void givenNow_whenSendContactPendingReminders_thenLooksUpReservationsCreatedMoreThanDelayAgo() {
            when(reservationRepository.findByStatusAndCreatedAtBefore(eq(ReservationStatus.NEW), any()))
                    .thenReturn(List.of());

            service.sendContactPendingReminders();

            ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);
            verify(reservationRepository).findByStatusAndCreatedAtBefore(eq(ReservationStatus.NEW), captor.capture());
            assertThat(captor.getValue()).isCloseTo(
                    LocalDateTime.now().minusDays(ReservationReminderService.CONTACT_PENDING_REMINDER_DELAY_DAYS),
                    within(5, SECONDS));
        }
    }
}
