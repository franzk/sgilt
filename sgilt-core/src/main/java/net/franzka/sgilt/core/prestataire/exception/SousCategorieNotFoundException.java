package net.franzka.sgilt.core.prestataire.exception;

/**
 * Exception levée lorsqu'aucune sous-catégorie ne correspond à la clé demandée.
 */
public class SousCategorieNotFoundException extends RuntimeException {

    /**
     * Construit l'exception pour la clé introuvable.
     *
     * @param key la clé de sous-catégorie
     */
    public SousCategorieNotFoundException(String key) {
        super("Sous-catégorie introuvable : " + key);
    }
}
