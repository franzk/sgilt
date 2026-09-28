package net.franzka.sgilt.core.prestataire.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Entité JPA représentant une catégorie du référentiel prestataire ('musique', 'lieu'…).
 * Le référentiel est alimenté par SQL, jamais modifié par l'application.
 */
@Entity
@Table(name = "categories")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Categorie {

    @Id
    @Column(updatable = false, nullable = false)
    private String key;

    @Column(nullable = false)
    private String name;

    /** Rang d'affichage de la catégorie. */
    @Column(nullable = false)
    private int position;

    /** Sous-catégories de la catégorie, par rang d'affichage. */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_key", insertable = false, updatable = false)
    @OrderBy("position")
    private List<SousCategorie> subcategories;
}
