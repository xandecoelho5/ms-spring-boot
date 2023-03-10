package com.xandecoelho5.OrderService.model;

import lombok.Builder;

import java.time.Instant;

@Builder
public record OrderResponse(long orderId, Instant orderDate, String orderStatus, long amount,
                            ProducDetails producDetails, PaymentDetails paymentDetails) {

    @Builder
    public record ProducDetails(String productName, long productId, long quantity, long price) {
    }

    @Builder
    public record PaymentDetails(long paymentId, PaymentMode paymentMode, String paymentStatus, Instant paymentDate) {
    }
}

