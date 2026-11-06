package net.franzka.sgilt.pay.paymentintent.service;

import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentIntentCreateParams.AllowedPaymentMethodType;
import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.pay.paymentintent.dto.CreatePaymentIntentRequest;
import net.franzka.sgilt.pay.paymentintent.dto.PaymentIntentDto;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service métier des PaymentIntents Stripe.
 */
@Service
@RequiredArgsConstructor
public class PaymentIntentService {

    static final String CURRENCY = "eur";
    // Moyens de paiement confirmés en quelques secondes : cartes, Apple Pay et Google Pay compris.
    // Pas de moyens différés (SEPA…) : leur succès arriverait bien après le blocage des places. Pas
    // de Link : il redemande les coordonnées de l'acheteur, déjà saisies sur l'écran de commande.
    static final List<AllowedPaymentMethodType> ALLOWED_PAYMENT_METHOD_TYPES =
            List.of(AllowedPaymentMethodType.CARD);
    // Clé des metadata Stripe qui relie le PaymentIntent à sa commande (lue par le webhook).
    public static final String METADATA_COMMANDE_ID = "commandeId";

    private final StripeClient stripeClient;

    /**
     * Crée le PaymentIntent d'une commande. Idempotent : la clé d'idempotence est l'identifiant de la
     * commande, un second appel pour la même commande renvoie le même PaymentIntent.
     *
     * @param request la commande et son montant
     * @return le secret client du PaymentIntent
     * @throws StripeException si l'appel à Stripe échoue
     */
    public PaymentIntentDto createPaymentIntent(CreatePaymentIntentRequest request) throws StripeException {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount((long) request.amountCents())
                .setCurrency(CURRENCY)
                .addAllAllowedPaymentMethodType(ALLOWED_PAYMENT_METHOD_TYPES)
                .putMetadata(METADATA_COMMANDE_ID, request.commandeId().toString())
                .build();
        RequestOptions options = RequestOptions.builder()
                .setIdempotencyKey(request.commandeId().toString())
                .build();
        PaymentIntent paymentIntent = stripeClient.v1().paymentIntents().create(params, options);
        return new PaymentIntentDto(paymentIntent.getClientSecret());
    }
}
