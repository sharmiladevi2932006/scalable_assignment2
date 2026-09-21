package com.example.orders;

import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    public void reserveStock(Order order) {
        if (order.getQuantity() > 3) {
            throw new InventoryUnavailableException(
                    "Inventory unavailable for quantity " + order.getQuantity());
        }
        System.out.println("Stock reserved for order " + order.getId());
    }
}
