package net.franzka.sgilt.core.ficheia.domain;

import lombok.Getter;

/**
 * Section du contenu généré par IA pouvant être appliquée à la fiche prestataire.
 * Chaque section correspond à un champ de l'entité {@code Prestataire} du même nom.
 */
@Getter
public enum FicheIaSection {
    BASELINE(false),
    SHORT_DESCRIPTION(false),
    IDENTITY(false),
    BUDGET(false),
    OFFERINGS(true),
    TESTIMONIALS(true),
    DETAILS(true),
    FAQ(true);

    /**
     *  Indique si cette section correspond à un champ de type liste sur
     *  (seules ces sections acceptent l'action AJOUTER).
     *
     */
    private final boolean list;

    FicheIaSection(boolean list) {
        this.list = list;
    }

}
