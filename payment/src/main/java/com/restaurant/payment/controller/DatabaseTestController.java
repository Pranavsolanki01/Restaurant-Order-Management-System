package com.restaurant.payment.controller;

import com.restaurant.payment.entity.Payment;
import com.restaurant.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment/test")
@RequiredArgsConstructor
public class DatabaseTestController {

    private final PaymentRepository paymentRepository;

    @GetMapping("/db-status")
    public Map<String, Object> testDatabase() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Test database connection
            long count = paymentRepository.count();
            response.put("status", "Database connected successfully");
            response.put("totalPayments", count);
            response.put("dbWorking", true);
        } catch (Exception e) {
            response.put("status", "Database connection failed");
            response.put("error", e.getMessage());
            response.put("dbWorking", false);
        }

        return response;
    }

    @PostMapping("/create-test-payment")
    public Map<String, Object> createTestPayment() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Create a test payment
            Payment testPayment = new Payment(
                    999L, // Test order ID
                    1L, // Test user ID
                    "test@example.com",
                    new BigDecimal("100.00"));

            Payment savedPayment = paymentRepository.save(testPayment);

            response.put("status", "Test payment created successfully");
            response.put("paymentId", savedPayment.getId());
            response.put("orderId", savedPayment.getOrderId());
            response.put("amount", savedPayment.getAmount());
            response.put("createdAt", savedPayment.getCreatedAt());

        } catch (Exception e) {
            response.put("status", "Failed to create test payment");
            response.put("error", e.getMessage());
        }

        return response;
    }
}
