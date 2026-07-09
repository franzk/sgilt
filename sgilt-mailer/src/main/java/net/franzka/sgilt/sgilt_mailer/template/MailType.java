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
    RAW_EMAIL
}
