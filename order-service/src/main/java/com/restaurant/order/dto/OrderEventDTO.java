package com.restaurant.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEventDTO {

    private Long orderId;
    private Long userId;
    private String userEmail;
    private String eventType; // ORDER_PLACED, ORDER_CONFIRMED, ORDER_CANCELLED, etc.
    private BigDecimal totalPrice;
    private String status;
    private String paymentStatus;
    private List<OrderItemEventDTO> orderItems;
    private String specialInstructions;
    private LocalDateTime timestamp;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemEventDTO {
        private Long menuItemId;
        private String menuItemName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
        private String specialRequests;
    }
}
