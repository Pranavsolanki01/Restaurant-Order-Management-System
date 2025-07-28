package com.rms.kitchen_service.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Kolkata")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Kolkata")
    private LocalDateTime updatedAt;

}
