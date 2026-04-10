package net.franzka.sgilt.core.reservation.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reservations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evenement_id")
    private Evenement evenement;

    private UUID prestataireId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "reservation_status")
    private ReservationStatus status;

    private LocalDateTime createdAt;

    public static Reservation createDraft(Evenement evenement, UUID prestataireId) {
        Reservation r = new Reservation();
        r.evenement = evenement;
        r.prestataireId = prestataireId;
        r.status = ReservationStatus.DRAFT;
        r.createdAt = LocalDateTime.now();
        return r;
    }
}
