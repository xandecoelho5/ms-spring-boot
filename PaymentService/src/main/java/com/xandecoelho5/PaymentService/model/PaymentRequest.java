package com.xandecoelho5.PaymentService.model;

public record PaymentRequest(long orderId, long amount, String referenceNumber, PaymentMode paymentMode) {
}
