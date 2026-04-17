package net.franzka.sgilt.core.mailer;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * Client HTTP déclaratif vers sgilt-mailer.
 * Instancié comme bean Spring via {@link MailerConfig}.
 */
@HttpExchange
public interface MailerClient {

    /**
     * Envoie un mail via l'API de sgilt-mailer.
     *
     * @param request les données du mail à envoyer (from, to, subject, html)
     */
    @PostExchange("/api/v1/mail")
    void sendMail(@RequestBody MailRequest request);
}
