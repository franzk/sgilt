package net.franzka.sgilt.core.prestataire.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.franzka.sgilt.core.reservation.domain.Reservation;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Entité JPA représentant une entrée dans le calendrier d'un prestataire.
 */
@Entity
@Table(name = "calendrier_prestataire",
        uniqueConstraints = @UniqueConstraint(columnNames = {"prestataire_id", "date"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendrierPrestataire {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestataire_id", nullable = false)
    private Prestataire prestataire;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CalendrierSource source;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    private String gcalEventId;
}