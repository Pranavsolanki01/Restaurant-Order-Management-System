package com.rms.kitchen_service.dto;

import java.math.BigDecimal;
import java.util.List;

import com.rms.kitchen_service.entity.KitchenOrderedItem;
import com.rms.kitchen_service.enums.StatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationService {

    private Long orderID;

    private Long tableID;

    private Long userID;

    private String userEmail;

    private StatusEnum status;

    private BigDecimal totalPrice;

    List<KitchenOrderedItem> orderedItems;

}
