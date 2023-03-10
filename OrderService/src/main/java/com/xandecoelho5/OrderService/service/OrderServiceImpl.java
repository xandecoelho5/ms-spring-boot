package com.xandecoelho5.OrderService.service;

import com.xandecoelho5.OrderService.entity.Order;
import com.xandecoelho5.OrderService.exception.CustomException;
import com.xandecoelho5.OrderService.external.client.PaymentService;
import com.xandecoelho5.OrderService.external.client.ProductService;
import com.xandecoelho5.OrderService.external.request.PaymentRequest;
import com.xandecoelho5.OrderService.external.response.PaymentResponse;
import com.xandecoelho5.OrderService.external.response.ProductResponse;
import com.xandecoelho5.OrderService.model.OrderRequest;
import com.xandecoelho5.OrderService.model.OrderResponse;
import com.xandecoelho5.OrderService.repository.OrderRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
@Log4j2
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final PaymentService paymentService;
    private final RestTemplate restTemplate;

    @Override
    public long placeOrder(OrderRequest orderRequest) {

        // Order Entity -> Save the data with Status Order Created
        // Product Service -> Block Products (Reduce the quantity)
        // Payment Service -> Payments -> Success -> COMPLETE, else -> CANCELLED

        log.info("Order placed successfully: {} ", orderRequest);

        productService.reduceQuantity(orderRequest.productId(), orderRequest.quantity());

        log.info("Creating Order with Status CREATED");
        Order order = Order.builder()
                .amount(orderRequest.totalAmount())
                .orderStatus("CREATED")
                .productId(orderRequest.productId())
                .orderDate(Instant.now())
                .quantity(orderRequest.quantity())
                .build();
        order = orderRepository.save(order);

        log.info("Calling Payment Service to complete the payment");
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(order.getId())
                .paymentMode(orderRequest.paymentMode())
                .amount(orderRequest.totalAmount())
                .build();

        String orderStatus;
        try {
            paymentService.doPayment(paymentRequest);
            log.info("Payment completed successfully. Changing order status to PLACED");
            orderStatus = "PLACED";
        } catch (Exception e) {
            log.error("Payment failed. Changing order status to PAYMENT_FAILED");
            orderStatus = "PAYMENT_FAILED";
        }

        order.setOrderStatus(orderStatus);
        order = orderRepository.save(order);

        log.info("Order with id {} placed successfully", order.getId());

        return order.getId();
    }

    @Override
    public OrderResponse getOrderDetails(long orderId) {
        log.info("Getting order details for order id: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException("Order not found for the order Id: " + orderId, "NOT_FOUND", 404));

        log.info("Invoking Product service to fetch the product");

        var productResponse = restTemplate.getForObject("http://PRODUCT-SERVICE/product/" + order.getProductId(), ProductResponse.class);

        log.info("Getting payment information from the payment service");

        var paymentResponse = restTemplate.getForObject("http://PAYMENT-SERVICE/payment/order/" + order.getId(), PaymentResponse.class);

        var productDetails = OrderResponse.ProducDetails.builder()
                .productName(productResponse.productName())
                .productId(productResponse.productId())
                .build();

        var paymentDetails = OrderResponse.PaymentDetails.builder()
                .paymentId(paymentResponse.paymentId())
                .paymentMode(paymentResponse.paymentMode())
                .paymentStatus(paymentResponse.paymentStatus())
                .paymentDate(paymentResponse.paymentDate())
                .build();

        return OrderResponse.builder()
                .orderId(order.getId())
                .orderStatus(order.getOrderStatus())
                .amount(order.getAmount())
                .orderDate(order.getOrderDate())
                .producDetails(productDetails)
                .paymentDetails(paymentDetails)
                .build();
    }
}
