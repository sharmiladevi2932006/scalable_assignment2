package com.example.ordersaga.controller;

import com.example.ordersaga.entity.Order;
import com.example.ordersaga.exception.InvalidOrderRequestException;
import com.example.ordersaga.service.SagaService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final SagaService sagaService;

    public OrderController(SagaService sagaService) {
        this.sagaService = sagaService;
    }

    @PostMapping
    public Order placeOrder(@RequestBody Order order) {
        validate(order);
        return sagaService.placeOrder(order);
    }

    private void validate(Order order) {
        if (order.getProductId() == null) {
            throw new InvalidOrderRequestException("productId is required");
        }
        if (order.getQuantity() <= 0) {
            throw new InvalidOrderRequestException("quantity must be greater than zero");
        }
        if (order.getAmount() <= 0) {
            throw new InvalidOrderRequestException("amount must be greater than zero");
        }
    }
}
