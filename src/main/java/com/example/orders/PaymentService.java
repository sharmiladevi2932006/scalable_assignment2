package com.example.orders;

import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    public void charge(Order order) {
        System.out.println("Payment succeeded for order " + order.getId());
    }

    public void refund(Order order) {
        System.out.println("Payment refunded for order " + order.getId());
    }
}
