package com.example.orders;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class ApiInfoController {

    @GetMapping("/")
    public Map<String, String> apiInfo() {
        return Map.of(
                "message", "Order workflow API is running",
                "orders", "/orders",
                "cqsOrders", "/cqs/orders",
                "h2Console", "/h2-console");
    }
}