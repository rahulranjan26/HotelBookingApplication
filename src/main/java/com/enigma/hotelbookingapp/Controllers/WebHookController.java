package com.enigma.hotelbookingapp.Controllers;

import com.enigma.hotelbookingapp.Services.BookingService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "/webhook")
@RequiredArgsConstructor
@Slf4j
public class WebHookController {

    private final BookingService bookingService;

    @Value("${stripe.webhook.secret}")
    private  String endPointSecret;


    @PostMapping(path = "/payments")
    public ResponseEntity<Void> capturePayments(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
//        log.info("Received Webhook: {} =========================================================================================", payload);
//        log.info("Signature: {} #############################################################################################", sigHeader);
        try {

            Event event = Webhook.constructEvent(payload, sigHeader, endPointSecret);
            log.info("✅ Event Constructed: {}", event.getType());

            // CALL capturePayments
            bookingService.capturepayment(event);

            return ResponseEntity.noContent().build();
        } catch (SignatureVerificationException e) {
            log.error("❌ Signature Verification Failed!", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error("❌ Error processing webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

