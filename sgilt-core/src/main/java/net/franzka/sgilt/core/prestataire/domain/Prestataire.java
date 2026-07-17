package net.franzka.sgilt.core.prestataire.domain;

import jakarta.persistence.*;
import lombok.*;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Entité JPA représentant un prestataire de la plateforme Sgilt.
 */
@Entity
@Table(name = "prestataires")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Prestataire {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false, unique = true)
    private Utilisateur utilisateur;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(name = "category_key", nullable = false)
    private String categoryKey;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "prestataires_sous_categories",
            joinColumns = @JoinColumn(name = "prestataire_id")
    )
    @Column(name = "subcat_key", nullable = false)
    private List<String> subcatKeys;

    private String baseline;

    private String avatar;

    private String shortDescription;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String medias;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String badges;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String offerings;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String identity;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String budget;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String testimonials;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String details;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String faq;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "prestataire_status", nullable = false)
    private PrestataireStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
