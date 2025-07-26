package com.rms.kitchen_service.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.rms.kitchen_service.dto.KitchenOrderedItemRequest;
import com.rms.kitchen_service.entity.KitchenOrderedItem;

public class KitchenOrderedItemRequestToKitchenOrderedItemMapper {

    public static KitchenOrderedItem mapper(KitchenOrderedItemRequest koir){
        KitchenOrderedItem koi = new KitchenOrderedItem();

        koi.setKitchenOrderID(koir.getKitchenOrderID());
        koi.setOderedItemID(koir.getOderedItemID());
        koi.setOrderedItemName(koir.getOrderedItemName());
        koi.setStatus(koir.getStatus());
        koi.setQuantity(koir.getQuantity());
        koi.setSpecialRequests(koir.getSpecialRequests());
        return koi;
    }

    public static List<KitchenOrderedItem> mapper(List<KitchenOrderedItemRequest> koirs){

        return koirs.stream()
                    .map(KitchenOrderedItemRequestToKitchenOrderedItemMapper::mapper)
                    .collect(Collectors.toList());

    }
}
