package com.xandecoelho5.OrderService.service;

import com.xandecoelho5.OrderService.model.OrderRequest;

public interface OrderService {
    long placeOrder(OrderRequest orderRequest);
}
