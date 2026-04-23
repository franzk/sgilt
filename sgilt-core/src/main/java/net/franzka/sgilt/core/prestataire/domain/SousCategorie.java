package net.franzka.sgilt.core.prestataire.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Entité JPA représentant une sous-catégorie de prestataires.
 */
@Entity
@Table(name = "sous_categories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SousCategorie {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String categoryKey;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer order;

    @Column(nullable = false)
    private Boolean active;
}