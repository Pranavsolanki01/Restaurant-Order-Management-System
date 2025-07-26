package com.rms.kitchen_service.entity;

import java.time.ZonedDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rms.kitchen_service.enums.PriorityEnum;
import com.rms.kitchen_service.enums.StatusEnum;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("kitchen-order")
public class KitchenOrder {

    @Id
    private String kitchenOrderID;

    private Long orderID;

    private Long tableID;

    private Long userID;

    private String userName;

    private StatusEnum status;

    private PriorityEnum priority;

    private String specialInstructuction;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Kolkata")
    private ZonedDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Kolkata")
    private ZonedDateTime updatedAt;

}
