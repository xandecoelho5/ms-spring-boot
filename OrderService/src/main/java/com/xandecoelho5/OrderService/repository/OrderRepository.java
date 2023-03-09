package com.xandecoelho5.OrderService.repository;

import com.xandecoelho5.OrderService.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
