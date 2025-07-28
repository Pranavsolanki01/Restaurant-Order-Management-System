package com.rms.kitchen_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderMessageIncomming {

    private Long orderId;
    private Long userId;
    private String userEmail;
    private String eventType;
    private BigDecimal totalPrice;
    private String status;
    private String paymentStatus;
    private List<OrderItemMessageIncomming> orderItems;
    private String specialInstructions;
    private LocalDateTime timestamp;
}
