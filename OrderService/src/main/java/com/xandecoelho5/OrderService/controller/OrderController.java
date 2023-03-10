package com.xandecoelho5.OrderService.controller;

import com.xandecoelho5.OrderService.model.OrderRequest;
import com.xandecoelho5.OrderService.model.OrderResponse;
import com.xandecoelho5.OrderService.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Log4j2
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/placeOrder")
    public ResponseEntity<Long> placeOrder(@RequestBody OrderRequest orderRequest) {
        long orderId = orderService.placeOrder(orderRequest);
        log.info("Order placed successfully with order id: {} ", orderId);
        return ResponseEntity.ok(orderId);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderDetails(@PathVariable long orderId) {
        OrderResponse orderResponse = orderService.getOrderDetails(orderId);
        return ResponseEntity.ok(orderResponse);
    }
}
