package net.franzka.sgilt.notifications.mailer;

import java.util.Map;

/**
 * DTO représentant une demande d'envoi de mail vers sgilt-mailer. Copie locale — mêmes noms de champs
 * que {@code net.franzka.sgilt.core.mailer.MailRequest} et que le DTO côté sgilt-mailer, qui
 * désérialise par matching structurel (son listener a un paramètre typé, pas de dépendance au FQCN
 * producteur). sgilt-notifications fournit le destinataire, le type de mail et les variables à
 * interpoler ; sgilt-mailer détient et compose le HTML (gabarit, sujet, expéditeur).
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
