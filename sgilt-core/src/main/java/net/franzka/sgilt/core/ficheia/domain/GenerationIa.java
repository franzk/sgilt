package net.franzka.sgilt.core.ficheia.domain;

import jakarta.persistence.*;
import lombok.*;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité JPA représentant le résultat de la dernière génération IA de fiche d'un prestataire.
 * Une seule ligne par prestataire : mise à jour en place à chaque nouvelle génération, sans
 * historique ni versioning.
 */
@Entity
@Table(name = "generations_ia")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerationIa {

    private static final int DEFAULT_TRIES_LEFT = 3;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestataire_id", nullable = false, unique = true)
    private Prestataire prestataire;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String lastGeneration;

    private LocalDateTime lastGenerationDateTime;

    @Column(nullable = false)
    private Integer triesLeft = DEFAULT_TRIES_LEFT;

    private String sourceUrl;

    @PrePersist
    protected void onCreate() {
        if (this.triesLeft == null) this.triesLeft = DEFAULT_TRIES_LEFT;
    }

    /**
     * Crée une nouvelle ligne de génération IA pour un prestataire, avec le quota par défaut déjà
     * positionné. À utiliser plutôt que le builder directement : {@code @Builder} ignore
     * l'initialisation de champ de {@code triesLeft}, qui ne serait sinon appliquée qu'au
     * {@code @PrePersist}, trop tard pour un contrôle de quota fait avant sauvegarde.
     *
     * @param prestataire le prestataire pour lequel créer la ligne
     * @return une nouvelle instance non persistée, prête à être sauvegardée
     */
    public static GenerationIa createFor(Prestataire prestataire) {
        return GenerationIa.builder()
                .prestataire(prestataire)
                .triesLeft(DEFAULT_TRIES_LEFT)
                .build();
    }
}
