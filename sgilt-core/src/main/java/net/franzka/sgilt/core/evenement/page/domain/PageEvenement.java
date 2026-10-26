package net.franzka.sgilt.core.evenement.page.domain;

import jakarta.persistence.*;
import lombok.*;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Entité JPA représentant la page d'un événement, sa face publique (/e/{slug}). Sous-feature de
 * l'événement, qui en a une au plus. Le titre, la date, les horaires, l'image, le lieu et la ville
 * restent ceux de l'{@link Evenement} ; seul ce qui est propre à la page vit ici.
 */
@Entity
@Table(name = "pages_evenement")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageEvenement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evenement_id", nullable = false, unique = true)
    private Evenement evenement;

    @Column(nullable = false, unique = true)
    private String slug;

    /** Libellé libre au-dessus du titre, choisi par l'organisateur (« Édition du 12 mars »…). */
    private String tag;
    private String subtitle;
    /** Titre de la présentation, au-dessus de la description ; facultatif. */
    private String sectionTitle;

    /** Texte de présentation au public, distinct de la description de l'événement. */
    private String description;

    /** Infos importantes, montrées avant l'achat (écran de commande). */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private List<String> importantInfo;

    /** Infos pratiques, montrées après l'achat (confirmation). */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private List<String> practicalInfo;

    /** Points forts de la page, dans l'ordre d'affichage. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private List<PageEvenementPicto> pictos;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
