package net.franzka.sgilt.core.evenement.billetterie.commande.mailer;

import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.evenement.billetterie.commande.domain.Commande;
import net.franzka.sgilt.core.evenement.billetterie.commande.ebillet.EBilletLabelUtils;
import net.franzka.sgilt.core.evenement.page.dto.EvenementInfosDto;
import net.franzka.sgilt.core.mailer.MailRequest;
import net.franzka.sgilt.core.mailer.MailType;
import net.franzka.sgilt.core.mailer.MailerClient;
import org.springframework.amqp.AmqpException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

/**
 * Service d'envoi des emails liés aux commandes de places (e-billet à l'acheteur).
 * Délègue l'envoi effectif à {@link MailerClient}, qui compose le HTML
 * à partir du {@link MailType} et des variables fournies.
 */
@Service
@Slf4j
public class CommandeMailerService {

    private final MailerClient mailerClient;
    private final String frontendUrl;

    /**
     * Construit le service avec ses dépendances.
     *
     * @param mailerClient le client de publication des mails (RabbitMQ)
     * @param frontendUrl  l'URL de base du frontend, pour construire le lien vers l'e-billet
     */
    public CommandeMailerService(MailerClient mailerClient, @Value("${sgilt.frontend.url}") String frontendUrl) {
        this.mailerClient = mailerClient;
        this.frontendUrl = frontendUrl;
    }

    /**
     * Envoie à l'acheteur d'une commande qui vient d'être payée le lien vers son e-billet. Ne relance
     * pas l'exception en cas d'échec : le paiement est acquis, la commande reste payée.
     *
     * @param commande       la commande payée, avec sa billetterie et son événement
     * @param slug           le slug de la page de l'événement, pour le lien vers l'e-billet
     * @param evenementInfos les infos de l'événement (date, horaires, lieu)
     */
    public void sendEBilletEmail(Commande commande, String slug, EvenementInfosDto evenementInfos) {
        String eBilletUrl = frontendUrl + "/e/" + slug + "/e-billet?commande=" + commande.getId();
        try {
            log.info("sendEBilletEmail → {} (commandeId={})", commande.getBuyerEmail(), commande.getId());
            mailerClient.sendMail(new MailRequest(
                    commande.getBuyerEmail(),
                    MailType.COMMANDE_E_BILLET_EMAIL,
                    Map.of(
                            "firstName", commande.getBuyerFirstName(),
                            "eventTitle", commande.getBilletterie().getEvenement().getTitle(),
                            "validFor", EBilletLabelUtils.validFor(commande.getQuantity()),
                            "eventDate", EventDateLabelUtils.eventDateLabel(
                                    evenementInfos.date(), evenementInfos.startsAt(), evenementInfos.endsAt()),
                            // Lieu et ville facultatifs sur l'événement ; Map.of refuse null.
                            "venue", Objects.requireNonNullElse(evenementInfos.venue(), ""),
                            "city", Objects.requireNonNullElse(evenementInfos.city(), ""),
                            "eBilletUrl", eBilletUrl),
                    null));
        } catch (AmqpException e) {
            log.error("Échec de l'envoi de l'e-billet pour {} — commandeId={}", commande.getBuyerEmail(), commande.getId(), e);
        }
    }
}
