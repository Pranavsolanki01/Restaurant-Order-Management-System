package com.restaurant.order.dto;

import com.restaurant.order.entity.Order;
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
public class OrderResponseDTO {

    private Long id;
    private Long userId;
    private String userEmail;
    private BigDecimal totalPrice;
    private Order.OrderStatus status;
    private Order.PaymentStatus paymentStatus;
    private String deliveryAddress;
    private String phoneNumber;
    private String specialInstructions;
    private List<OrderItemResponseDTO> orderItems;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
