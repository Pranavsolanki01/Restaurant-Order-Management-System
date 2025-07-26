package com.rms.kitchen_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.rms.kitchen_service.enums.StatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderMessageIncomming {

    private Long id;
    private Long tableID;
    private Long userId;
    private String userName;
    private BigDecimal totalPrice;
    private StatusEnum status;
    private String deliveryAddress;
    private String phoneNumber;
    private String specialInstructions;
    private List<OrderItemMessageIncomming> orderItems;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
}
