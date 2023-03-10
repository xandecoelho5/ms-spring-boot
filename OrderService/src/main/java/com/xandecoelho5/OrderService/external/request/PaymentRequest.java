package com.xandecoelho5.OrderService.external.request;

import com.xandecoelho5.OrderService.model.PaymentMode;
import lombok.Builder;

@Builder
public record PaymentRequest(long orderId, long amount, String referenceNumber, PaymentMode paymentMode) {
}
