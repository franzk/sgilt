package net.franzka.sgilt.core.prestataire.domain;

/**
 * Statut de publication d'une fiche prestataire.
 */
public enum PrestataireStatus {
    DRAFT,
    IN_REVIEW,
    /**
     * Fiche provisionnée par le flow clé-en-main (onboarding géré par l'équipe Sgilt, en
     * impersonation) — en construction, jamais soumise par le prestataire, aucun mail
     * d'activation encore envoyé. Transitionne directement vers {@link #PUBLISHED}.
     */
    WAITING_FOR_CREATION_SERVICE,
    PUBLISHED
}
