package net.franzka.sgilt.core.reservation.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité JPA traçant l'envoi d'un rappel automatique pour une réservation. Une seule ligne par
 * couple (réservation, type) — garanti par contrainte unique en base.
 */
@Entity
@Table(name = "reservation_reminders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationReminder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "reservation_reminder_type", nullable = false)
    private ReservationReminderType type;

    @Column(nullable = false, updatable = false)
    private LocalDateTime sentAt;
}
