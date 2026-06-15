package net.franzka.sgilt.core.mailer;

import java.util.Map;

/**
 * DTO représentant une demande d'envoi de mail vers sgilt-mailer.
 * Core fournit le destinataire, le type de mail et les variables à interpoler ;
 * sgilt-mailer détient et compose le HTML (gabarit, sujet, expéditeur).
 *
 * @param to       l'adresse email destinataire
 * @param mailType le type de mail à envoyer (sélectionne le gabarit côté mailer)
 * @param context  les variables nommées à interpoler dans le gabarit
 * @param locale   la locale du destinataire (non implémenté côté mailer pour l'instant, mono-FR)
 */
public record MailRequest(
        String to,
        MailType mailType,
        Map<String, Object> context,
        String locale
) {}
