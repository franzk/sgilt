package net.franzka.sgilt.pay.paymentintent.api;

import com.stripe.exception.StripeException;
import jakarta.validation.Valid;
import net.franzka.sgilt.pay.paymentintent.dto.CreatePaymentIntentRequest;
import net.franzka.sgilt.pay.paymentintent.dto.PaymentIntentDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

// Endpoint interne : appelé par sgilt-core sur le réseau interne, sans route dans la gateway.
@RequestMapping("internal/payment-intents")
public interface PaymentIntentApi {

    @PostMapping
    ResponseEntity<PaymentIntentDto> createPaymentIntent(@RequestBody @Valid CreatePaymentIntentRequest request)
            throws StripeException;
}
