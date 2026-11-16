package net.franzka.sgilt.core.evenement.billetterie.commande.domain;

/**
 * Résultat du scan d'un e-billet à l'entrée d'un événement, par son organisateur. Un e-billet est à
 * usage unique : le premier scan fait entrer toutes les places de sa commande.
 */
public enum ScanStatus {

    /** Entrée validée par ce scan. */
    VALIDATED,

    /** E-billet déjà scanné : ses places sont déjà entrées. */
    ALREADY_USED,

    /** Commande de cet événement, annulée (remboursée) : ne fait pas entrer. */
    CANCELLED,

    /** Pas un e-billet de cet événement (autre événement, QR inconnu ou d'un autre format). */
    UNKNOWN
}
