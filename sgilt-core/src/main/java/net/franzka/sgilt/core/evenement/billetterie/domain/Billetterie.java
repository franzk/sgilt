package net.franzka.sgilt.core.evenement.billetterie.domain;

import jakarta.persistence.*;
import lombok.*;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité JPA représentant la billetterie d'un événement, qui en a une au plus. Feature de
 * l'événement, au même titre que sa page, dont elle a besoin pour vendre (règle portée par le
 * service). Les places restantes ne sont pas stockées : elles se déduisent de la jauge et des
 * billets vendus ou bloqués.
 */
@Entity
@Table(name = "billetteries")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Billetterie {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evenement_id", nullable = false, unique = true)
    private Evenement evenement;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "billetterie_status", nullable = false)
    private BilletterieStatus status;

    /** Prix d'un billet, en centimes d'euro. */
    @Column(nullable = false)
    private Integer unitPriceCents;

    /** Nombre total de places mises en vente. */
    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
