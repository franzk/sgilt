package net.franzka.sgilt.pay.webhook.service;

import com.stripe.StripeClient;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.pay.config.StripeProperties;
import net.franzka.sgilt.pay.webhook.event.PaymentEventPublisher;
import net.franzka.sgilt.pay.webhook.event.PaymentSucceededEvent;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static net.franzka.sgilt.pay.paymentintent.service.PaymentIntentService.METADATA_COMMANDE_ID;

/**
 * Service métier des webhooks Stripe : vérifie la signature des événements et signale à sgilt-core
 * les paiements réussis des commandes.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookService {

    static final String PAYMENT_INTENT_SUCCEEDED = "payment_intent.succeeded";

    private final StripeClient stripeClient;
    private final StripeProperties stripeProperties;
    private final PaymentEventPublisher paymentEventPublisher;

    /**
     * Vérifie la signature d'un événement Stripe puis le traite. Les événements qui ne nous
     * concernent pas sont ignorés sans erreur, pour que Stripe ne les renvoie pas.
     *
     * @param payload   le corps brut de la requête
     * @param signature l'en-tête Stripe-Signature
     * @throws SignatureVerificationException si la signature est invalide
     */
    public void handleEvent(String payload, String signature) throws SignatureVerificationException {
        Event event = stripeClient.constructEvent(payload, signature, stripeProperties.webhookSecret());
        if (PAYMENT_INTENT_SUCCEEDED.equals(event.getType())) {
            onPaymentIntentSucceeded(event);
        } else {
            log.debug("Événement Stripe ignoré — type={} id={}", event.getType(), event.getId());
        }
    }

    // Paiement réussi : retrouve la commande par les metadata du PaymentIntent et le signale à
    // sgilt-core. Un PaymentIntent sans commande (créé depuis le dashboard, ou par `stripe trigger`)
    // est ignoré.
    private void onPaymentIntentSucceeded(Event event) {
        if (!(toStripeObject(event) instanceof PaymentIntent paymentIntent)) {
            log.warn("Événement {} sans PaymentIntent — id={}", PAYMENT_INTENT_SUCCEEDED, event.getId());
            return;
        }
        String commandeId = paymentIntent.getMetadata().get(METADATA_COMMANDE_ID);
        if (commandeId == null) {
            log.warn("PaymentIntent réussi sans commande, ignoré — paymentIntentId={}", paymentIntent.getId());
            return;
        }
        log.info("PaymentIntent réussi — paymentIntentId={} commandeId={}", paymentIntent.getId(), commandeId);
        paymentEventPublisher.publishPaymentSucceeded(
                new PaymentSucceededEvent(UUID.fromString(commandeId), paymentIntent.getId()));
    }

    // Objet de l'événement. getObject() est vide quand la version d'API de l'événement diffère de celle
    // de stripe-java (endpoint ou stripe listen réglés sur une autre version) : on désérialise quand
    // même, seuls l'id et les metadata du PaymentIntent nous servent.
    private StripeObject toStripeObject(Event event) {
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
        return deserializer.getObject().orElseGet(() -> {
            log.warn("Version d'API de l'événement Stripe {} différente de celle de stripe-java", event.getId());
            try {
                return deserializer.deserializeUnsafe();
            } catch (EventDataObjectDeserializationException e) {
                throw new IllegalStateException("Événement Stripe illisible : " + event.getId(), e);
            }
        });
    }
}
