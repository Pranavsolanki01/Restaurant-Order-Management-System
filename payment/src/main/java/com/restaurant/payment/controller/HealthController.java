package com.restaurant.payment.controller;

import com.restaurant.payment.jwt.JwtUserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class HealthController {

    private final JwtUserContext jwtUserContext;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Value("${server.port}")
    private String serverPort;

    // Public health endpoint
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "payment-service");
        response.put("status", "UP");
        response.put("port", serverPort);
        response.put("profile", activeProfile);
        response.put("timestamp", LocalDateTime.now());
        response.put("version", "1.0.0");
        response.put("description", "Payment Service with Razorpay Integration");
        return response;
    }

    // Protected endpoint to test JWT authentication
    @GetMapping("/status")
    public Map<String, Object> status() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "payment-service");
        response.put("status", "AUTHENTICATED");
        response.put("userId", jwtUserContext.getCurrentUserId());
        response.put("userEmail", jwtUserContext.getCurrentUserEmail());
        response.put("userRole", jwtUserContext.getCurrentUserRole());
        response.put("isAdmin", jwtUserContext.isCurrentUserAdmin());
        response.put("port", serverPort);
        response.put("profile", activeProfile);
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "JWT Authentication successful!");
        return response;
    }
}