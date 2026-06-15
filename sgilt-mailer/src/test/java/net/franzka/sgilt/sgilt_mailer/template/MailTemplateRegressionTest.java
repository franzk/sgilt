package net.franzka.sgilt.sgilt_mailer.template;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test de non-régression : pour chaque {@link MailType}, le HTML et le sujet
 * rendus par le mailer doivent être identiques à ceux que sgilt-core produisait
 * avant la centralisation du templating (capturés ci-dessous comme référence).
 * Si ce test échoue, le rendu d'un mail a changé.
 */
class MailTemplateRegressionTest {

    private final MailTemplateRegistry registry = new MailTemplateRegistry();
    private final MailTemplateRenderer renderer = new MailTemplateRenderer();

    // Référence : HTML produit par OnboardingMailerService.sendVerificationEmail avant migration.
    private static final String REFERENCE_VERIFICATION_HTML = """
            <p>Bonjour,</p>
            <p>Vous venez d’initier une demande de réservation sur Sgilt avec cette adresse email.</p>
            <p>Pour confirmer votre adresse email et créer votre compte, cliquez sur le lien ci-dessous :</p>
            <p><a href="https://app.sgilt.fr/onboarding/verify?token=abc123">Confirmer mon adresse email</a></p>
            <p>Ce lien expire dans 24 heures.</p>
            <p>Si vous n’êtes pas à l’origine de cette action, vous pouvez simplement ignorer ce message.</p>
            <p>À bientôt,</p>
            <p>L’équipe Sgilt.</p>
            """;

    // Référence : HTML produit par OnboardingMailerService.sendSecurityAlertEmail avant migration.
    private static final String REFERENCE_SECURITY_ALERT_HTML = """
            <p>Bonjour,</p>
            <p>Vous venez d'initier une demande de réservation avec votre adresse email.</p>
            <p>Cette adresse est déjà liée à un compte Sgilt.</p>
            <p>Pour des raisons de sécurité, la demande n’a pas été créée.</p>
            <p></p>
            <p>Pour créer une nouvelle demande ou suivre vos événements, connectez-vous à votre espace :</p>
            <p><a href="https://app.sgilt.fr/app">Accéder à Sgilt</a></p>
            <p></p>
            <p>Si vous n’êtes pas à l’origine de cette action, vous pouvez simplement ignorer ce message.</p>
            <p></p>
            <p>À bientôt,</p>
            <p>L’équipe Sgilt.</p>
            """;

    // Référence : HTML produit par OnboardingMailerService.sendWelcomeEmail avant migration.
    private static final String REFERENCE_WELCOME_HTML = """
            <p>Bonjour,</p>
            <p>Votre adresse email a bien été confirmée et votre compte Sgilt a été créé.</p>
            <p>Votre demande de réservation est maintenant enregistrée.</p>
            <p>Vous pouvez accéder à votre espace pour suivre vos événements et retrouver vos demandes en cours :</p>
            <p><a href="https://app.sgilt.fr/app">Accéder à Sgilt</a></p>
            <p></p>
            <p>À bientôt,</p>
            <p>L’équipe Sgilt.</p>
            """;

    @Test
    void givenVerificationEmailContext_whenRender_thenHtmlMatchesPreMigrationReference() {
        MailTemplate template = registry.getTemplate(MailType.VERIFICATION_EMAIL);
        Map<String, Object> context = Map.of("confirmationUrl", "https://app.sgilt.fr/onboarding/verify?token=abc123");

        String html = renderer.render(template.htmlTemplate(), context, template.htmlSafeVariables());

        assertThat(html).isEqualTo(REFERENCE_VERIFICATION_HTML);
    }

    @Test
    void givenVerificationEmailContext_whenRenderSubject_thenSubjectMatchesPreMigrationReference() {
        MailTemplate template = registry.getTemplate(MailType.VERIFICATION_EMAIL);

        String subject = renderer.render(template.subjectTemplate(), Map.of("confirmationUrl", "https://app.sgilt.fr"), template.htmlSafeVariables());

        assertThat(subject).isEqualTo("Sgilt - Confirmez votre demande");
    }

    @Test
    void givenSecurityAlertEmailContext_whenRender_thenHtmlMatchesPreMigrationReference() {
        MailTemplate template = registry.getTemplate(MailType.SECURITY_ALERT_EMAIL);
        Map<String, Object> context = Map.of("appUrl", "https://app.sgilt.fr/app");

        String html = renderer.render(template.htmlTemplate(), context, template.htmlSafeVariables());

        assertThat(html).isEqualTo(REFERENCE_SECURITY_ALERT_HTML);
    }

    @Test
    void givenSecurityAlertEmailContext_whenRenderSubject_thenSubjectMatchesPreMigrationReference() {
        MailTemplate template = registry.getTemplate(MailType.SECURITY_ALERT_EMAIL);

        String subject = renderer.render(template.subjectTemplate(), Map.of("appUrl", "https://app.sgilt.fr/app"), template.htmlSafeVariables());

        assertThat(subject).isEqualTo("Sgilt - Demande de réservation initiée avec votre adresse email");
    }

    @Test
    void givenWelcomeEmailContext_whenRender_thenHtmlMatchesPreMigrationReference() {
        MailTemplate template = registry.getTemplate(MailType.WELCOME_EMAIL);
        Map<String, Object> context = Map.of("appUrl", "https://app.sgilt.fr/app");

        String html = renderer.render(template.htmlTemplate(), context, template.htmlSafeVariables());

        assertThat(html).isEqualTo(REFERENCE_WELCOME_HTML);
    }

    @Test
    void givenWelcomeEmailContext_whenRenderSubject_thenSubjectMatchesPreMigrationReference() {
        MailTemplate template = registry.getTemplate(MailType.WELCOME_EMAIL);

        String subject = renderer.render(template.subjectTemplate(), Map.of("appUrl", "https://app.sgilt.fr/app"), template.htmlSafeVariables());

        assertThat(subject).isEqualTo("Bienvenue sur Sgilt");
    }
}
