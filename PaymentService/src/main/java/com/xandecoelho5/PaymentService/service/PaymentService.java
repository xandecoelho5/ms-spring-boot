package com.xandecoelho5.PaymentService.service;

import com.xandecoelho5.PaymentService.model.PaymentRequest;
import com.xandecoelho5.PaymentService.model.PaymentResponse;

public interface PaymentService {
    long doPayment(PaymentRequest paymentRequest);

    PaymentResponse getPaymentDetailsByOrderId(long orderId);
}
