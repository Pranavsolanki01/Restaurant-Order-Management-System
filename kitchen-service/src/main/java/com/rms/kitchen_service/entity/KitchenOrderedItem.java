package com.rms.kitchen_service.entity;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.rms.kitchen_service.enums.StatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("kitchen-ordered-item")
public class KitchenOrderedItem {

    @Id
    private String id;

    private String kitchenOrderID;

    private Long oderedItemID;

    private String orderedItemName;

    private Integer quantity;

    private StatusEnum status;

    private BigDecimal unitPrice;

    private BigDecimal totalPrice;

    private String specialRequests;

}
