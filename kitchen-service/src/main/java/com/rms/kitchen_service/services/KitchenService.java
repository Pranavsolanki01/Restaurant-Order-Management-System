package com.rms.kitchen_service.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.rms.kitchen_service.dto.KitchenOrderRequest;
import com.rms.kitchen_service.dto.KitchenOrderedItemRequest;
import com.rms.kitchen_service.entity.KitchenOrder;
import com.rms.kitchen_service.entity.KitchenOrderedItem;
import com.rms.kitchen_service.mapper.KitchenOrderRequestToKitchenOrderMapper;
import com.rms.kitchen_service.mapper.KitchenOrderedItemRequestToKitchenOrderedItemMapper;
import com.rms.kitchen_service.repo.KitchenOrderRepo;
import com.rms.kitchen_service.repo.KitchenOrderedItemRepo;
import com.rms.kitchen_service.services.Interface.IKitchenService;

public class KitchenService implements IKitchenService{

    @Autowired
    private KitchenOrderRepo kitchenOrderRepo;

    @Autowired
    private KitchenOrderedItemRepo kitchenOrderedItemRepo;

    @Override
    public String saveKitchenOrder(KitchenOrderRequest kor) {
        
        KitchenOrder ko = KitchenOrderRequestToKitchenOrderMapper.mapper(kor);
        return kitchenOrderRepo.save(ko).getKitchenOrderID();
    }

    @Override
    public void saveKitchenOrderedItem(List<KitchenOrderedItemRequest> koir) {
        List<KitchenOrderedItem> kois = KitchenOrderedItemRequestToKitchenOrderedItemMapper.mapper(koir);

        kitchenOrderedItemRepo.saveAll(kois);
    }

}
