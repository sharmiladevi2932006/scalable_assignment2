package com.example.ordersaga.config;

import com.example.ordersaga.entity.Product;
import com.example.ordersaga.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedProducts(ProductRepository productRepository) {
        return args -> {
            if (productRepository.findById(101L).isEmpty()) {
                productRepository.save(new Product(101L, "Laptop", 10));
            }
            if (productRepository.findById(102L).isEmpty()) {
                productRepository.save(new Product(102L, "Keyboard", 5));
            }
        };
    }
}
