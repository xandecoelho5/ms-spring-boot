package com.xandecoelho5.OrderService.model;

import lombok.Builder;

@Builder
public record OrderRequest(long productId, long totalAmount, long quantity, PaymentMode paymentMode) {
}
