package net.franzka.sgilt.core.ficheia.domain;

/**
 * Action à appliquer sur une section du contenu généré par IA.
 */
public enum FicheIaAction {
    /** Écrase le contenu du champ {@code Prestataire} correspondant par la valeur générée. */
    REMPLACER,
    /** Concatène le contenu généré à la fin de la liste existante (sections liste uniquement). */
    AJOUTER,
    /** Applique REMPLACER aux 8 sections en une seule fois. Ne prend pas de section associée. */
    ECRASER_TOUT
}
