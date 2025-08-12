package com.restaurant.payment.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEventDTO {

    private Long orderId;
    private Long userId;
    private String userEmail;
    private String eventType; // ORDER_PLACED, ORDER_CONFIRMED, etc.
    private BigDecimal totalPrice;
    private String status;
    private String paymentStatus;
    private List<OrderItemEventDTO> orderItems;
    private String specialInstructions;
    private LocalDateTime timestamp;

    // Constructor for creating ORDER_CONFIRMED events
    public static OrderEventDTO createOrderConfirmed(Long orderId, Long userId, String userEmail,
            BigDecimal totalPrice, List<OrderItemEventDTO> orderItems,
            String specialInstructions) {
        OrderEventDTO event = new OrderEventDTO();
        event.setOrderId(orderId);
        event.setUserId(userId);
        event.setUserEmail(userEmail);
        event.setEventType("ORDER_CONFIRMED");
        event.setTotalPrice(totalPrice);
        event.setStatus("CONFIRMED");
        event.setPaymentStatus("COMPLETED");
        event.setOrderItems(orderItems);
        event.setSpecialInstructions(specialInstructions);
        event.setTimestamp(LocalDateTime.now());
        return event;
    }
}
