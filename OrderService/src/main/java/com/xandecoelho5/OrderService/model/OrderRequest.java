package com.xandecoelho5.OrderService.model;

public record OrderRequest(long productId, long totalAmount, long quantity, PaymentMode paymentMode) {
}
