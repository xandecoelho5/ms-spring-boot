package com.xandecoelho5.OrderService.external.response;

import com.xandecoelho5.OrderService.model.PaymentMode;
import lombok.Builder;

import java.time.Instant;

@Builder
public record PaymentResponse(
        long paymentId, String paymentStatus, PaymentMode paymentMode,
        long amount, Instant paymentDate, long orderId) {
}
