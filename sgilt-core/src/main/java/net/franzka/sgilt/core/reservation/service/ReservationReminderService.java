package net.franzka.sgilt.core.reservation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.reservation.domain.Reservation;
import net.franzka.sgilt.core.reservation.domain.ReservationReminder;
import net.franzka.sgilt.core.reservation.domain.ReservationReminderType;
import net.franzka.sgilt.core.reservation.domain.ReservationStatus;
import net.franzka.sgilt.core.reservation.event.mapper.ReservationEventMapper;
import net.franzka.sgilt.core.reservation.repository.ReservationReminderRepository;
import net.franzka.sgilt.core.reservation.repository.ReservationRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Envoie les rappels automatiques liés aux réservations. Chaque rappel envoyé est tracé dans
 * {@link ReservationReminder}, dans la même transaction que la publication de l'évènement : un
 * rappel ne part qu'une fois par réservation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationReminderService {

    /** Ancienneté, en jours, à partir de laquelle une réservation NEW donne lieu à un rappel au client. */
    static final int CONTACT_PENDING_REMINDER_DELAY_DAYS = 7;

    private final ReservationRepository reservationRepository;
    private final ReservationReminderRepository reservationReminderRepository;
    private final ReservationEventMapper reservationEventMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Rappelle au client chaque réservation restée NEW (en attente de contact) depuis
     * {@value #CONTACT_PENDING_REMINDER_DELAY_DAYS} jours et pas encore rappelée — tous les jours à
     * 18h30. {@code @Transactional} porté ici faute de controller appelant, comme
     * {@code PrestataireService.shuffleDisplayOrder}.
     */
    @Transactional
    @Scheduled(cron = "0 30 18 * * *", zone = "Europe/Paris")
    public void sendContactPendingReminders() {
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> reservations = reservationRepository
                .findByStatusAndCreatedAtBefore(ReservationStatus.NEW, now.minusDays(CONTACT_PENDING_REMINDER_DELAY_DAYS))
                .stream()
                .filter(r -> !reservationReminderRepository.existsByReservationIdAndType(r.getId(), ReservationReminderType.CONTACT_PENDING))
                .toList();
        log.info("sendContactPendingReminders : {} réservation(s) à rappeler", reservations.size());

        reservations.forEach(reservation -> sendContactPendingReminder(reservation, now));
    }

    // trace le rappel puis publie l'évènement — republié sur RabbitMQ après commit
    private void sendContactPendingReminder(Reservation reservation, LocalDateTime sentAt) {
        reservationReminderRepository.save(ReservationReminder.builder()
                .reservation(reservation)
                .type(ReservationReminderType.CONTACT_PENDING)
                .sentAt(sentAt)
                .build());
        applicationEventPublisher.publishEvent(reservationEventMapper.toContactReminderDueEvent(reservation));
    }
}
