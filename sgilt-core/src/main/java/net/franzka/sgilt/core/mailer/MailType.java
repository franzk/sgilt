package net.franzka.sgilt.core.mailer;

/**
 * Types de mails gérés par le registre de gabarits de sgilt-mailer.
 * Les constantes doivent rester synchronisées avec l'enum équivalente
 * côté sgilt-mailer (même noms, sérialisés en JSON).
 * Distinct du {@code NotificationType} de sgilt-core.
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
     * Mail envoyé à un prestataire dont la fiche vient d'être publiée, quand son compte est déjà
     * activé (flow autonome — a nécessairement déjà un mot de passe pour avoir pu soumettre sa
     * fiche). Sans lien d'activation, contrairement à {@link #PRESTATAIRE_ONBOARDING_EMAIL}.
     */
    PRESTATAIRE_PUBLISHED_EMAIL,

    /**
     * Mail envoyé à un prestataire dont la fiche clé-en-main vient d'être publiée sans jamais
     * avoir reçu de mail auparavant — met en avant la page déjà construite, avec le lien
     * d'activation pour accéder à son espace.
     */
    PRESTATAIRE_PAGE_READY_EMAIL
}
