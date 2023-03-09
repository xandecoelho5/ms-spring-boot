package com.xandecoelho5.PaymentService.service;

import com.xandecoelho5.PaymentService.model.PaymentRequest;

public interface PaymentService {
    long doPayment(PaymentRequest paymentRequest);
}
