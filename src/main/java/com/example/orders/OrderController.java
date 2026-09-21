package com.example.orders;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderWorkflowService workflowService;
    private final OrderQueryService queryService;

    public OrderController(OrderWorkflowService workflowService, OrderQueryService queryService) {
        this.workflowService = workflowService;
        this.queryService = queryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order create(
            @Valid @RequestBody CreateOrderRequest request,
            @RequestHeader(value = "X-Event-Id", required = false) String eventId) {
        return workflowService.create(request, eventId);
    }

    @GetMapping("/{id}")
    public Order findById(@PathVariable Long id) {
        return queryService.findById(id);
    }

    @GetMapping
    public List<Order> findAll() {
        return queryService.findAll();
    }

    @PostMapping("/{id}/reconcile")
    public ReconciliationResponse reconcile(@PathVariable Long id) {
        return workflowService.reconcile(queryService.findById(id));
    }
}
