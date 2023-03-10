package com.xandecoelho5.PaymentService.controller;

import com.xandecoelho5.PaymentService.model.PaymentRequest;
import com.xandecoelho5.PaymentService.model.PaymentResponse;
import com.xandecoelho5.PaymentService.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Long> doPayment(@RequestBody PaymentRequest paymentRequest) {
        return ResponseEntity.ok(paymentService.doPayment(paymentRequest));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentDetailsByOrderId(@PathVariable long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentDetailsByOrderId(orderId));
    }
}
