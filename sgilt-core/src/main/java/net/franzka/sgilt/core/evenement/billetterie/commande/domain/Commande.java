package net.franzka.sgilt.core.evenement.billetterie.commande.domain;

import jakarta.persistence.*;
import lombok.*;
import net.franzka.sgilt.core.evenement.billetterie.domain.Billetterie;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité JPA représentant une commande de billets d'une billetterie. Créée PENDING, elle bloque
 * ses places jusqu'à {@code expiresAt} ; au-delà, non payée, elle ne compte plus dans les places
 * prises. Le prix d'un billet y est figé à la commande.
 */
@Entity
@Table(name = "commandes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billetterie_id", nullable = false)
    private Billetterie billetterie;

    @Column(nullable = false)
    private Integer quantity;

    /** Prix d'un billet à la commande, en centimes d'euro. */
    @Column(nullable = false)
    private Integer unitPriceCents;

    @Column(nullable = false)
    private String buyerFirstName;

    @Column(nullable = false)
    private String buyerLastName;

    @Column(nullable = false)
    private String buyerEmail;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "commande_status", nullable = false)
    private CommandeStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Fin du blocage des places d'une commande PENDING. */
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
