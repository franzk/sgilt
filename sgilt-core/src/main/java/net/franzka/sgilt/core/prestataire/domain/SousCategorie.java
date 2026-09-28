package net.franzka.sgilt.core.prestataire.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entité JPA représentant une sous-catégorie du référentiel prestataire ('dj', 'traiteur'…).
 * Le référentiel est alimenté par SQL, jamais modifié par l'application.
 */
@Entity
@Table(name = "sous_categories")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SousCategorie {

    @Id
    @Column(updatable = false, nullable = false)
    private String key;

    @Column(nullable = false)
    private String name;

    @Column(name = "category_key", nullable = false)
    private String categoryKey;

    /** Rang d'affichage de la sous-catégorie dans sa catégorie. */
    @Column(nullable = false)
    private int position;
}
