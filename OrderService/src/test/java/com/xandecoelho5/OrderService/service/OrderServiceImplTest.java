package com.xandecoelho5.OrderService.service;

import com.xandecoelho5.OrderService.entity.Order;
import com.xandecoelho5.OrderService.exception.CustomException;
import com.xandecoelho5.OrderService.external.client.PaymentService;
import com.xandecoelho5.OrderService.external.client.ProductService;
import com.xandecoelho5.OrderService.external.request.PaymentRequest;
import com.xandecoelho5.OrderService.external.response.PaymentResponse;
import com.xandecoelho5.OrderService.external.response.ProductResponse;
import com.xandecoelho5.OrderService.model.OrderRequest;
import com.xandecoelho5.OrderService.model.PaymentMode;
import com.xandecoelho5.OrderService.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductService productService;
    @Mock
    private PaymentService paymentService;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private OrderServiceImpl orderService;

    @Value("${microservices.product}")
    private String productServiceUrl;
    @Value("${microservices.payment}")
    private String paymentServiceUrl;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(orderService, "productServiceUrl", productServiceUrl);
        ReflectionTestUtils.setField(orderService, "paymentServiceUrl", paymentServiceUrl);
    }

    @DisplayName("Get Order - Success Scenario")
    @Test
    void test_When_Order_Success() {
        // Mocking
        Order order = getMockOrder();
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(restTemplate.getForObject(productServiceUrl + order.getProductId(), ProductResponse.class)).thenReturn(getMockProductResponse());
        when(restTemplate.getForObject(paymentServiceUrl + order.getId(), PaymentResponse.class)).thenReturn(getMockPaymentResponse());

        // Actual
        var orderResponse = orderService.getOrderDetails(1);

        // Verification
        verify(orderRepository, times(1)).findById(anyLong());
        verify(restTemplate, times(1)).getForObject(productServiceUrl + order.getProductId(), ProductResponse.class);
        verify(restTemplate, times(1)).getForObject(paymentServiceUrl + order.getId(), PaymentResponse.class);

        // Assert
        assertNotNull(orderResponse);
        assertEquals(order.getId(), orderResponse.orderId());
    }

    @DisplayName("Get Orders - Failure Scenario")
    @Test
    void test_When_Get_Order_NOT_FOUND_then_Not_Found() {

        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        var exception = assertThrows(CustomException.class, () -> orderService.getOrderDetails(1));
        assertEquals("NOT_FOUND", exception.getErrorCode());
        assertEquals(404, exception.getStatus());

        verify(orderRepository, times(1)).findById(anyLong());
    }

    @DisplayName("Place Order - Success Scenario")
    @Test
    void test_When_Place_Order_Success() {
        var order = getMockOrder();
        var orderRequest = getMockOrderRequest();

        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(productService.reduceQuantity(anyLong(), anyLong())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(paymentService.doPayment(any(PaymentRequest.class))).thenReturn(ResponseEntity.ok(1L));

        long orderId = orderService.placeOrder(orderRequest);

        verify(orderRepository, times(2)).save(any(Order.class));
        verify(productService, times(1)).reduceQuantity(anyLong(), anyLong());
        verify(paymentService, times(1)).doPayment(any(PaymentRequest.class));

        assertEquals(order.getId(), orderId);
        assertEquals("PLACED", order.getOrderStatus());
    }

    @DisplayName("Place Order - Failure Scenario")
    @Test
    void test_When_Place_Order_Payment_Fails_then_Order_Placed() {
        var order = getMockOrder();
        var orderRequest = getMockOrderRequest();

        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(productService.reduceQuantity(anyLong(), anyLong())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(paymentService.doPayment(any(PaymentRequest.class))).thenThrow(new RuntimeException());

        long orderId = orderService.placeOrder(orderRequest);

        verify(orderRepository, times(2)).save(any(Order.class));
        verify(productService, times(1)).reduceQuantity(anyLong(), anyLong());
        verify(paymentService, times(1)).doPayment(any(PaymentRequest.class));

        assertEquals(order.getId(), orderId);
        assertEquals("PAYMENT_FAILED", order.getOrderStatus());
    }

    private OrderRequest getMockOrderRequest() {
        return OrderRequest.builder()
                .productId(1)
                .quantity(10)
                .paymentMode(PaymentMode.CASH)
                .totalAmount(100)
                .build();
    }

    private PaymentResponse getMockPaymentResponse() {
        return PaymentResponse.builder()
                .paymentId(1)
                .paymentDate(Instant.now())
                .paymentMode(PaymentMode.CASH)
                .amount(200)
                .orderId(1)
                .paymentStatus("ACCEPTED")
                .build();
    }

    private ProductResponse getMockProductResponse() {
        return ProductResponse.builder()
                .productId(2)
                .productName("iPhone")
                .price(100)
                .quantity(200)
                .build();
    }

    private Order getMockOrder() {
        return Order.builder()
                .id(1)
                .orderStatus("PLACED")
                .orderDate(Instant.now())
                .amount(100)
                .quantity(200)
                .productId(2)
                .build();
    }
}
