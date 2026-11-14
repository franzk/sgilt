package net.franzka.sgilt.core.evenement.billetterie.commande.domain;

/**
 * Statut d'une commande vu par l'organisateur de l'événement. Reprend {@link CommandeStatus}, avec
 * EXPIRED en plus : une commande PENDING dont le blocage des places est passé sans paiement
 * (abandonnée). Calculé à la lecture, jamais enregistré.
 */
public enum OrganizerCommandeStatus {
    PAID,
    PENDING,
    EXPIRED,
    CANCELLED
}
