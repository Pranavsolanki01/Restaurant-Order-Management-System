package com.restaurant.payment.controller;

import com.restaurant.payment.dto.request.CreatePaymentRequest;
import com.restaurant.payment.dto.response.ApiResponse;
import com.restaurant.payment.dto.response.PaymentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/payment/test-dto")
@RequiredArgsConstructor
@Validated
public class DtoTestController {

    @PostMapping("/validate-request")
    public ApiResponse<String> testRequestValidation(@Valid @RequestBody CreatePaymentRequest request) {
        return ApiResponse.success(
                "Request validation successful",
                "Order ID: " + request.getOrderId() + ", Amount: " + request.getAmount());
    }

    @GetMapping("/sample-response")
    public ApiResponse<PaymentResponse> getSampleResponse() {
        PaymentResponse payment = new PaymentResponse();
        payment.setId(1L);
        payment.setOrderId(123L);
        payment.setUserId(1L);
        payment.setUserEmail("raj@gmail.com");
        payment.setAmount(new BigDecimal("250.00"));
        payment.setCurrency("INR");
        payment.setCreatedAt(LocalDateTime.now());

        return ApiResponse.success("Sample payment response", payment);
    }

    @GetMapping("/error-response")
    public ApiResponse<String> getErrorResponse() {
        return ApiResponse.error(
                "Payment processing failed",
                "Invalid payment details provided");
    }
}
