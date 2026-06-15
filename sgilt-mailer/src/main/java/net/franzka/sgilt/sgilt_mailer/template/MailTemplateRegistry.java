package net.franzka.sgilt.sgilt_mailer.template;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * Registre des gabarits de mail, indexés par {@link MailType}.
 * Chaque gabarit est isolé et porte ses propres variables attendues,
 * de façon à pouvoir migrer plus tard vers un moteur de template
 * sans changer le contrat d'appel.
 */
@Component
public class MailTemplateRegistry {

    private static final Map<MailType, MailTemplate> TEMPLATES = Map.of(
            MailType.VERIFICATION_EMAIL, new MailTemplate(
                    "Sgilt - Confirmez votre demande",
                    """
                    <p>Bonjour,</p>
                    <p>Vous venez d’initier une demande de réservation sur Sgilt avec cette adresse email.</p>
                    <p>Pour confirmer votre adresse email et créer votre compte, cliquez sur le lien ci-dessous :</p>
                    <p><a href="{confirmationUrl}">Confirmer mon adresse email</a></p>
                    <p>Ce lien expire dans 24 heures.</p>
                    <p>Si vous n’êtes pas à l’origine de cette action, vous pouvez simplement ignorer ce message.</p>
                    <p>À bientôt,</p>
                    <p>L’équipe Sgilt.</p>
                    """,
                    Set.of("confirmationUrl"),
                    Set.of()
            ),
            MailType.SECURITY_ALERT_EMAIL, new MailTemplate(
                    "Sgilt - Demande de réservation initiée avec votre adresse email",
                    """
                    <p>Bonjour,</p>
                    <p>Vous venez d'initier une demande de réservation avec votre adresse email.</p>
                    <p>Cette adresse est déjà liée à un compte Sgilt.</p>
                    <p>Pour des raisons de sécurité, la demande n’a pas été créée.</p>
                    <p></p>
                    <p>Pour créer une nouvelle demande ou suivre vos événements, connectez-vous à votre espace :</p>
                    <p><a href="{appUrl}">Accéder à Sgilt</a></p>
                    <p></p>
                    <p>Si vous n’êtes pas à l’origine de cette action, vous pouvez simplement ignorer ce message.</p>
                    <p></p>
                    <p>À bientôt,</p>
                    <p>L’équipe Sgilt.</p>
                    """,
                    Set.of("appUrl"),
                    Set.of()
            ),
            MailType.WELCOME_EMAIL, new MailTemplate(
                    "Bienvenue sur Sgilt",
                    """
                    <p>Bonjour,</p>
                    <p>Votre adresse email a bien été confirmée et votre compte Sgilt a été créé.</p>
                    <p>Votre demande de réservation est maintenant enregistrée.</p>
                    <p>Vous pouvez accéder à votre espace pour suivre vos événements et retrouver vos demandes en cours :</p>
                    <p><a href="{appUrl}">Accéder à Sgilt</a></p>
                    <p></p>
                    <p>À bientôt,</p>
                    <p>L’équipe Sgilt.</p>
                    """,
                    Set.of("appUrl"),
                    Set.of()
            )
    );

    /**
     * Retourne le gabarit associé au type de mail donné.
     *
     * @param mailType le type de mail
     * @return le gabarit correspondant
     * @throws IllegalArgumentException si aucun gabarit n'est enregistré pour ce type
     */
    public MailTemplate getTemplate(MailType mailType) {
        MailTemplate template = TEMPLATES.get(mailType);
        if (template == null) {
            throw new IllegalArgumentException("No mail template registered for mail type " + mailType);
        }
        return template;
    }
}
