package com.rms.kitchen_service.entity;

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
    private String KitchenOrderedItemID;

    private String kitchenOrderID;

    private Long oderedItemID;

    private String orderedItemName;

    private Integer quantity;

    private StatusEnum status;

    private String specialRequests;

}
