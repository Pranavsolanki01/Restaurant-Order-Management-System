package com.rms.kitchen_service.dto;

import com.rms.kitchen_service.enums.StatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KitchenOrderedItemRequest {


    private String kitchenOrderID;

    private Long oderedItemID;

    private String orderedItemName;

    private Integer quantity;

    private StatusEnum status;

    private String specialRequests;

}
