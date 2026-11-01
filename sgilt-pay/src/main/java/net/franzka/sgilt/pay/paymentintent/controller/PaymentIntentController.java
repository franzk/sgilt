package net.franzka.sgilt.pay.paymentintent.controller;

import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.pay.paymentintent.api.PaymentIntentApi;
import net.franzka.sgilt.pay.paymentintent.dto.CreatePaymentIntentRequest;
import net.franzka.sgilt.pay.paymentintent.dto.PaymentIntentDto;
import net.franzka.sgilt.pay.paymentintent.service.PaymentIntentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller HTTP interne des PaymentIntents, appelé par sgilt-core.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class PaymentIntentController implements PaymentIntentApi {

    private final PaymentIntentService paymentIntentService;

    /**
     * Crée le PaymentIntent d'une commande.
     *
     * @param request la commande et son montant
     * @return 201 Created avec le secret client du PaymentIntent
     * @throws StripeException si l'appel à Stripe échoue
     */
    @Override
    public ResponseEntity<PaymentIntentDto> createPaymentIntent(CreatePaymentIntentRequest request)
            throws StripeException {
        log.info("POST /internal/payment-intents — commandeId={} amountCents={}",
                request.commandeId(), request.amountCents());
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentIntentService.createPaymentIntent(request));
    }
}
