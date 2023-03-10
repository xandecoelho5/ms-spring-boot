package com.xandecoelho5.CloudGateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping("/orderServiceFallback")
    public String orderServiceFallback() {
        return "Order service is taking too long to respond or is down. Please try again later";
    }

    @GetMapping("/paymentServiceFallback")
    public String paymentServiceFallback() {
        return "Payment service is taking too long to respond or is down. Please try again later";
    }

    @GetMapping("/productServiceFallback")
    public String productServiceFallback() {
        return "Product service is taking too long to respond or is down. Please try again later";
    }
}
