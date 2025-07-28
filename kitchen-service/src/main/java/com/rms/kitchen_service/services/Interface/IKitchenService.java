package com.rms.kitchen_service.services.Interface;

import java.util.List;

import com.rms.kitchen_service.dto.KitchenOrderRequest;
import com.rms.kitchen_service.dto.KitchenOrderedItemRequest;
import com.rms.kitchen_service.entity.KitchenOrderedItem;
import com.rms.kitchen_service.enums.StatusEnum;

public interface IKitchenService {

    public String saveKitchenOrder(KitchenOrderRequest kor);

    public void saveKitchenOrderedItem(List<KitchenOrderedItemRequest> koir);

    public boolean updateItemStatus(String id, StatusEnum status);

    public List<KitchenOrderedItem> getKitchenOrderStatus(String id);

    public boolean updateKitchenOrderStatus(String id, StatusEnum status);

    public boolean completeOrder(String id);

    // public void publishEvent();

}
