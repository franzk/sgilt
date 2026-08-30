package net.franzka.sgilt.core.prestataire.domain;

/**
 * Flow à l'origine d'une fiche prestataire — détermine quelle notification lui est due (voir
 * {@link net.franzka.sgilt.core.prestataire.service.PrestataireService#publish} et
 * {@link net.franzka.sgilt.core.prestataire.service.PrestataireService#resendOnboardingEmail}).
 * Aujourd'hui limité aux flows de création ; conçu pour accueillir d'autres flows futurs sans
 * lien avec la création.
 */
public enum PrestataireFlow {
    CREATION_CLE_EN_MAIN,
    CREATION_AUTONOME,
    /**
     * Fiche créée avant l'introduction de ce champ — flow d'origine non tracé.
     */
    AUCUN
}
