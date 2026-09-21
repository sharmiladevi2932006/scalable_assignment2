package com.example.orders;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderCqsService {

    private final OrderRepository repository;

    public OrderCqsService(OrderRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Order createOrder(Order order) {
        order.setStatus(OrderStatus.CREATED);
        return repository.save(order);
    }

    public Order getOrder(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }
}
