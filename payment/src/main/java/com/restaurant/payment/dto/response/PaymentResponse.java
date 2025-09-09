package com.restaurant.payment.dto.response;

import com.restaurant.payment.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private Long id;
    private Long orderId;
    private Long userId;
    private String userEmail;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
    private String paymentMethod;

    // Razorpay specific fields for frontend
    private String razorpayOrderId;
    private String razorpayPaymentId;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;

    // Additional info
    private String failureReason;
    private BigDecimal refundAmount;
    private String refundStatus;

    // Success response for payment creation
    public static PaymentResponse success(String message, Object data) {
        PaymentResponse response = new PaymentResponse();
        // This will be used as a wrapper when needed
        return response;
    }
}
