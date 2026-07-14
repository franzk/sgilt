package net.franzka.sgilt.notifications.mailer;

/**
 * Types de mails que sgilt-notifications peut demander à sgilt-mailer d'envoyer. Doit rester
 * synchronisé avec les valeurs correspondantes de l'enum équivalente côté sgilt-mailer (même noms,
 * sérialisés en JSON) — contrairement à sgilt-core, dont l'enum {@code MailType} porte un jeu de
 * valeurs disjoint (onboarding/sécurité), sgilt-notifications ne porte ici que les types qu'il
 * produit réellement.
 */
public enum MailType {

    /** Mail envoyé au prestataire quand un client lui envoie une nouvelle demande de réservation. */
    RESERVATION_NEW_REQUEST_EMAIL,

    /** Mail envoyé au client quand le prestataire a pris contact (NEW → IN_DISCUSSION). */
    RESERVATION_STATUS_IN_DISCUSSION_EMAIL,

    /** Mail envoyé au client quand le prestataire confirme sa réservation. */
    RESERVATION_STATUS_CONFIRMED_EMAIL,

    /** Mail envoyé au client quand le prestataire refuse sa demande, avant prise de contact. */
    RESERVATION_STATUS_REFUSED_PRE_CONTACT_EMAIL,

    /** Mail envoyé au client quand le prestataire refuse sa demande, après discussion. */
    RESERVATION_STATUS_REFUSED_POST_CONTACT_EMAIL,

    /** Mail envoyé au client quand le prestataire annule une réservation déjà confirmée. */
    RESERVATION_STATUS_CANCELED_POST_CONFIRMATION_EMAIL,

    /** Mail envoyé au prestataire quand le client retire sa demande, avant prise de contact. */
    RESERVATION_STATUS_CANCELED_BY_CLIENT_PRE_CONTACT_EMAIL,

    /** Mail envoyé au prestataire quand le client annule sa réservation, après discussion. */
    RESERVATION_STATUS_CANCELED_BY_CLIENT_POST_CONTACT_EMAIL,

    /** Mail envoyé à l'autre partie quand une note (non personnelle) est ajoutée au feed. */
    RESERVATION_NOTE_ADDED_EMAIL,

    /** Mail envoyé à l'autre partie quand un document (non personnel) est ajouté au feed. */
    RESERVATION_DOCUMENT_ADDED_EMAIL
}
