package com.example.ordersaga.service;

import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    public boolean makePayment(double amount) {
        if (amount <= 0) {
            System.out.println("Payment failed: invalid amount Rs." + amount);
            return false;
        }
        System.out.println("Payment successful: Rs." + amount);
        return true;
    }

    public void refund(double amount) {
        System.out.println("Payment refunded: Rs." + amount);
    }
}
