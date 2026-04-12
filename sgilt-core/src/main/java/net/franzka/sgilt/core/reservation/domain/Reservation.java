package net.franzka.sgilt.core.reservation.domain;

import jakarta.persistence.*;
import lombok.*;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité JPA représentant une réservation associée à un événement et un prestataire.
 */
@Entity
@Table(name = "reservations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evenement_id")
    private Evenement evenement;

    //TODO : relation ManyToOne avec prestataire
    private UUID prestataireId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "reservation_status")
    private ReservationStatus status;

    private LocalDateTime createdAt;
}
