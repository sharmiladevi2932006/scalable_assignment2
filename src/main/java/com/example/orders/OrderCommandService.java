package com.example.orders;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderCommandService {

    private final OrderRepository repository;

    public OrderCommandService(OrderRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Order create(Order order) {
        order.setStatus(OrderStatus.CREATED);
        return repository.save(order);
    }

    @Transactional
    public Order updateStatus(Order order, OrderStatus status) {
        order.setStatus(status);
        return repository.save(order);
    }
}
