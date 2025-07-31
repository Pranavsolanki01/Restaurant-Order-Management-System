package com.rms.kitchen_service.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.rms.kitchen_service.enums.PriorityEnum;
import com.rms.kitchen_service.enums.StatusEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("kitchen-order")
public class KitchenOrder {

    @Id
    private String id;

    private Long orderID;

    private Long tableID;

    private Long userID;

    private String userEmail;

    private StatusEnum status;

    private BigDecimal totalPrice;

    private PriorityEnum priority;

    private String specialInstructions;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
