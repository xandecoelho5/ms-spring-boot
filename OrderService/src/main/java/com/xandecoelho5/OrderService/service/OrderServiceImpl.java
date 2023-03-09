package com.xandecoelho5.OrderService.service;

import com.xandecoelho5.OrderService.entity.Order;
import com.xandecoelho5.OrderService.model.OrderRequest;
import com.xandecoelho5.OrderService.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Log4j2
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public long placeOrder(OrderRequest orderRequest) {

        // Order Entity -> Save the data with Status Order Created
        // Product Service -> Block Products (Reduce the quantity)
        // Payment Service -> Payments -> Success -> COMPLETE, else -> CANCELLED

        log.info("Order placed successfully: {} ", orderRequest);

        Order order = Order.builder()
                .amount(orderRequest.totalAmount())
                .orderStatus("CREATED")
                .productId(orderRequest.productId())
                .orderDate(Instant.now())
                .quantity(orderRequest.quantity())
                .build();
        orderRepository.save(order);

        log.info("Order with id {} placed successfully", order.getId());

        return order.getId();
    }
}
