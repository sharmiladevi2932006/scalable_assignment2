package com.example.ordersaga.service;

import com.example.ordersaga.entity.Order;
import com.example.ordersaga.entity.OrderStatus;
import com.example.ordersaga.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class SagaService {

    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;
    private final PaymentService paymentService;

    public SagaService(OrderRepository orderRepository,
                        InventoryService inventoryService,
                        PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.inventoryService = inventoryService;
        this.paymentService = paymentService;
    }

    public Order placeOrder(Order order) {

        order.setStatus(OrderStatus.CREATED);
        order = orderRepository.save(order);

        boolean paymentSuccessful = paymentService.makePayment(order.getAmount());

        if (!paymentSuccessful) {
            order.setStatus(OrderStatus.PAYMENT_FAILED);
            return orderRepository.save(order);
        }

        order.setStatus(OrderStatus.PAID);
        order = orderRepository.save(order);

        boolean stockReserved = inventoryService.reserveStock(order.getProductId(), order.getQuantity());

        if (!stockReserved) {
            paymentService.refund(order.getAmount());
            order.setStatus(OrderStatus.CANCELLED);
            return orderRepository.save(order);
        }

        order.setStatus(OrderStatus.CONFIRMED);
        return orderRepository.save(order);
    }
}
