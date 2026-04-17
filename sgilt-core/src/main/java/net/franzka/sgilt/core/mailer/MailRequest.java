package net.franzka.sgilt.core.mailer;

/**
 * DTO représentant une demande d'envoi de mail vers sgilt-mailer.
 *
 * @param from    l'adresse email expéditrice
 * @param to      l'adresse email destinataire
 * @param subject le sujet du mail
 * @param html    le corps du mail au format HTML
 */
public record MailRequest(
        String from,
        String to,
        String subject,
        String html
) {}
