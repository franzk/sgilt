package net.franzka.sgilt.core.evenement.page.domain;

/**
 * État de la page publique d'un événement pour son organisateur, quand le back-office l'a activée.
 */
public enum PublicPageStatus {
    /** L'organisateur n'a pas encore enregistré son contenu. */
    NOT_CREATED,
    /** Brouillon, introuvable sur /e/{slug}. */
    DRAFT,
    /** En ligne sur /e/{slug}. */
    PUBLISHED
}
