package com.rms.kitchen_service.services.Interface;

import java.util.List;

import com.rms.kitchen_service.dto.KitchenOrderRequest;
import com.rms.kitchen_service.dto.KitchenOrderedItemRequest;

public interface IKitchenService {

    public String saveKitchenOrder(KitchenOrderRequest kor);

    public void saveKitchenOrderedItem(List<KitchenOrderedItemRequest> koir);

    // public void publishEvent();

}
