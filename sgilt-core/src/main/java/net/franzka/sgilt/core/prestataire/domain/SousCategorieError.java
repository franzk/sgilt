package net.franzka.sgilt.core.prestataire.domain;

/**
 * Motif de refus d'une modification des sous-catégories.
 */
public enum SousCategorieError {
    /** La clé de sous-catégorie est déjà utilisée. */
    KEY_ALREADY_EXISTS,
    /** La catégorie visée n'existe pas. */
    UNKNOWN_CATEGORIE,
    /** La sous-catégorie est utilisée par des prestataires. */
    SOUS_CATEGORIE_IN_USE
}
