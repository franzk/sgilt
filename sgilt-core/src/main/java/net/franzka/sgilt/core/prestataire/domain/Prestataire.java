package net.franzka.sgilt.core.prestataire.domain;

import jakarta.persistence.*;
import lombok.*;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;

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

    private String baseline;

    private String heroImage;

    private String avatar;

    private String shortDescription;

    @Column(columnDefinition = "jsonb")
    private String photos;

    @Column(columnDefinition = "jsonb")
    private String badges;

    @Column(columnDefinition = "jsonb")
    private String offerings;

    @Column(columnDefinition = "jsonb")
    private String identity;

    @Column(columnDefinition = "jsonb")
    private String budget;

    @Column(columnDefinition = "jsonb")
    private String testimonials;

    @Column(columnDefinition = "jsonb")
    private String logistics;

    @Column(columnDefinition = "jsonb")
    private String technical;

    @Column(columnDefinition = "jsonb")
    private String faq;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "prestataires_sous_categories",
            joinColumns = @JoinColumn(name = "prestataire_id"),
            inverseJoinColumns = @JoinColumn(name = "sous_categorie_id")
    )
    private List<SousCategorie> sousCategories;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}