package com.rms.kitchen_service.mapper;

import com.rms.kitchen_service.dto.NotificationService;
import com.rms.kitchen_service.entity.KitchenOrder;

public class KithcenOrderToNotificationServiceMapper {

    public static NotificationService mapper(KitchenOrder ko){
        return NotificationService.builder()
                                .orderID(ko.getOrderID())
                                .tableID(ko.getTableID())
                                .userID(ko.getUserID())
                                .userEmail(ko.getUserEmail())
                                .status(ko.getStatus())
                                .totalPrice(ko.getTotalPrice())
                                .build();
    }

}
