package com.example.orders;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderWorkflowService {

    private final OrderCommandService commandService;
    private final PaymentService paymentService;
    private final InventoryService inventoryService;
    private final Map<String, Order> processedEvents = new ConcurrentHashMap<>();

    public OrderWorkflowService(
            OrderCommandService commandService,
            PaymentService paymentService,
            InventoryService inventoryService) {
        this.commandService = commandService;
        this.paymentService = paymentService;
        this.inventoryService = inventoryService;
    }

    @Transactional
    public Order create(CreateOrderRequest request, String eventId) {
        String effectiveEventId = eventId == null || eventId.isBlank()
                ? UUID.randomUUID().toString()
                : eventId;
        Order duplicate = processedEvents.get(effectiveEventId);
        if (duplicate != null) {
            System.out.println("Duplicate event ignored: " + effectiveEventId);
            return duplicate;
        }

        Order order = commandService.create(new Order(
                request.productId(), request.quantity(), request.amount(), OrderStatus.CREATED));
        paymentService.charge(order);
        commandService.updateStatus(order, OrderStatus.PAYMENT_CONFIRMED);

        if (reserveWithRetry(order)) {
            commandService.updateStatus(order, OrderStatus.STOCK_RESERVED);
            commandService.updateStatus(order, OrderStatus.CONFIRMED);
        } else {
            paymentService.refund(order);
            commandService.updateStatus(order, OrderStatus.CANCELLED);
        }

        processedEvents.put(effectiveEventId, order);
        return order;
    }

    private boolean reserveWithRetry(Order order) {
        int attempts = 0;
        while (attempts < 3) {
            try {
                inventoryService.reserveStock(order);
                return true;
            } catch (InventoryUnavailableException exception) {
                attempts++;
                System.out.println("Retry attempt: " + attempts + " for order " + order.getId());
            }
        }
        System.out.println("Inventory update failed after retries for order " + order.getId());
        return false;
    }

    @Transactional
    public ReconciliationResponse reconcile(Order order) {
        String message = "No reconciliation action required.";
        if (OrderStatus.PAYMENT_CONFIRMED.equals(order.getStatus())) {
            message = "Order is paid but not confirmed. Check inventory workflow.";
        }
        if (OrderStatus.CANCELLED.equals(order.getStatus())) {
            message = "Verify that compensation/refund completed.";
        }
        System.out.println(message);
        return new ReconciliationResponse(order, message);
    }
}
