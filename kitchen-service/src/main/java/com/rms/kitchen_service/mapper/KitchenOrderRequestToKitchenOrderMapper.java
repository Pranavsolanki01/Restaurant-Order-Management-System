package com.rms.kitchen_service.mapper;

import com.rms.kitchen_service.dto.KitchenOrderRequest;
import com.rms.kitchen_service.entity.KitchenOrder;

public class KitchenOrderRequestToKitchenOrderMapper {

    public static KitchenOrder mapper(KitchenOrderRequest kor ){

        KitchenOrder ko = new KitchenOrder();
        ko.setOrderID(kor.getOrderID());
        ko.setTotalPrice(kor.getTotalPrice());
        ko.setStatus(kor.getStatus());
        ko.setCreatedAt(kor.getCreatedAt());
        ko.setSpecialInstructions(kor.getSpecialInstructions());
        ko.setUpdatedAt(kor.getUpdatedAt());
        ko.setUserID(kor.getUserID());
        // ko.setTableID(kor.getTableID());
        ko.setUserEmail(kor.getUserEmail());
        return ko;

    }
}
