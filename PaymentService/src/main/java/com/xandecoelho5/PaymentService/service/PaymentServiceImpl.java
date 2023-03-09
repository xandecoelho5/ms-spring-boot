package com.xandecoelho5.PaymentService.service;

import com.xandecoelho5.PaymentService.entity.TransactionDetails;
import com.xandecoelho5.PaymentService.model.PaymentRequest;
import com.xandecoelho5.PaymentService.repository.TranscationDetailsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Log4j2
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final TranscationDetailsRepository transcationDetailsRepository;

    @Override
    public long doPayment(PaymentRequest paymentRequest) {
        log.info("Payment request received: {}", paymentRequest);

        TransactionDetails transactionDetails = TransactionDetails.builder()
                .paymentDate(Instant.now())
                .paymentMode(paymentRequest.paymentMode().name())
                .paymentStatus("SUCCESS")
                .orderId(paymentRequest.orderId())
                .referenceNumber(paymentRequest.referenceNumber())
                .amount(paymentRequest.amount())
                .build();
        transcationDetailsRepository.save(transactionDetails);

        log.info("Transaction with Id {} Completed successfully", transactionDetails.getId());

        return transactionDetails.getId();
    }
}
