package com.example.orders;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cqs/orders")
public class CqsOrderController {

    private final OrderCqsService service;

    public CqsOrderController(OrderCqsService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order create(@Valid @RequestBody CreateOrderRequest request) {
        return service.createOrder(new Order(
                request.productId(), request.quantity(), request.amount(), OrderStatus.CREATED));
    }

    @GetMapping("/{id}")
    public Order findById(@PathVariable Long id) {
        return service.getOrder(id);
    }
}
