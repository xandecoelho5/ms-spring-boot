package com.xandecoelho5.OrderService.service;

import com.xandecoelho5.OrderService.model.OrderRequest;
import com.xandecoelho5.OrderService.model.OrderResponse;

public interface OrderService {
    long placeOrder(OrderRequest orderRequest);

    OrderResponse getOrderDetails(long orderId);
}
