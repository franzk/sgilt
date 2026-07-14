package net.franzka.sgilt.sgilt_mailer.template;

/**
 * Types de mails gérés par le registre de gabarits.
 * Les constantes doivent rester synchronisées avec l'enum équivalente
 * côté sgilt-core (même noms, sérialisés en JSON).
 */
public enum MailType {

    /**
     * Mail de confirmation d'adresse email envoyé lors de l'initiation
     * du tunnel de demande de réservation.
     */
    VERIFICATION_EMAIL,

    /**
     * Mail d'alerte de sécurité envoyé lorsqu'une demande de réservation
     * est initiée avec une adresse email déjà associée à un compte existant.
     */
    SECURITY_ALERT_EMAIL,

    /**
     * Mail de bienvenue envoyé après la confirmation de compte réussie.
     */
    WELCOME_EMAIL,

    /**
     * Mail d'activation envoyé à un prestataire nouvellement provisionné par un admin,
     * l'invitant à définir son mot de passe.
     */
    PRESTATAIRE_ONBOARDING_EMAIL,

    /**
     * Mail déjà entièrement rendu par l'appelant (sujet + HTML fournis tels quels dans le
     * contexte), sans passer par un gabarit métier. Utilisé exclusivement par
     * {@code sgilt-smtp-bridge} pour relayer les mails envoyés nativement par Keycloak
     * (reset password, verify email), dont le contenu est déjà généré par le thème Keycloak.
     */
    RAW_EMAIL,

    /**
     * Mail envoyé au prestataire quand un client lui envoie une nouvelle demande de réservation.
     * Producteur : {@code sgilt-notifications}.
     */
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
