package com.restaurant.payment.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEventDTO {

    private Long paymentId;
    private Long orderId;
    private Long userId;
    private String userEmail;
    private String eventType; // PAYMENT_COMPLETED, PAYMENT_FAILED
    private BigDecimal amount;
    private String paymentMethod;
    private String razorpayPaymentId;
    private String status;
    private String failureReason; // Only for failed payments
    private LocalDateTime timestamp;

    // Factory methods for different event types
    public static PaymentEventDTO createPaymentCompleted(Long paymentId, Long orderId, Long userId,
            String userEmail, BigDecimal amount,
            String paymentMethod, String razorpayPaymentId) {
        PaymentEventDTO event = new PaymentEventDTO();
        event.setPaymentId(paymentId);
        event.setOrderId(orderId);
        event.setUserId(userId);
        event.setUserEmail(userEmail);
        event.setEventType("PAYMENT_COMPLETED");
        event.setAmount(amount);
        event.setPaymentMethod(paymentMethod);
        event.setRazorpayPaymentId(razorpayPaymentId);
        event.setStatus("COMPLETED");
        event.setTimestamp(LocalDateTime.now());
        return event;
    }

    public static PaymentEventDTO createPaymentFailed(Long paymentId, Long orderId, Long userId,
            String userEmail, BigDecimal amount,
            String failureReason) {
        PaymentEventDTO event = new PaymentEventDTO();
        event.setPaymentId(paymentId);
        event.setOrderId(orderId);
        event.setUserId(userId);
        event.setUserEmail(userEmail);
        event.setEventType("PAYMENT_FAILED");
        event.setAmount(amount);
        event.setStatus("FAILED");
        event.setFailureReason(failureReason);
        event.setTimestamp(LocalDateTime.now());
        return event;
    }
}
