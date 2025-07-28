package com.rms.kitchen_service.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rms.kitchen_service.dto.KitchenOrderRequest;
import com.rms.kitchen_service.dto.KitchenOrderedItemRequest;
import com.rms.kitchen_service.entity.KitchenOrder;
import com.rms.kitchen_service.entity.KitchenOrderedItem;
import com.rms.kitchen_service.enums.StatusEnum;
import com.rms.kitchen_service.mapper.KitchenOrderRequestToKitchenOrderMapper;
import com.rms.kitchen_service.mapper.KitchenOrderedItemRequestToKitchenOrderedItemMapper;
import com.rms.kitchen_service.repo.KitchenOrderRepo;
import com.rms.kitchen_service.repo.KitchenOrderedItemRepo;
import com.rms.kitchen_service.services.Interface.IKitchenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KitchenService implements IKitchenService{

    @Autowired
    private final KitchenOrderRepo kitchenOrderRepo;

    @Autowired
    private final KitchenOrderedItemRepo kitchenOrderedItemRepo;

    @Override
    public String saveKitchenOrder(KitchenOrderRequest kor) {
        
        KitchenOrder ko = KitchenOrderRequestToKitchenOrderMapper.mapper(kor);
        return kitchenOrderRepo.save(ko).getId();
    }

    @Override
    public void saveKitchenOrderedItem(List<KitchenOrderedItemRequest> koir) {
        List<KitchenOrderedItem> kois = KitchenOrderedItemRequestToKitchenOrderedItemMapper.mapper(koir);

        kitchenOrderedItemRepo.saveAll(kois);
    }

    @Override
    public boolean updateItemStatus(String id, StatusEnum status) {
        KitchenOrderedItem koi = kitchenOrderedItemRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Item with ID " + id + " not found"));

        koi.setStatus(status);
        return kitchenOrderedItemRepo.save(koi) != null;
    }

    @Override
    public List<KitchenOrderedItem> getKitchenOrderStatus(String id) {
        return kitchenOrderedItemRepo.findByKitchenOrderID(id);
    }

    @Override
    public boolean updateKitchenOrderStatus(String id, StatusEnum status) {

        long count = kitchenOrderedItemRepo.countByKitchenOrderID(id);

        long count2 = kitchenOrderedItemRepo.countByKitchenOrderIDAndStatus(id, status);
        
        if(count == count2){
            KitchenOrder ko = kitchenOrderRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Order with ID " + id + " not found"));
            ko.setStatus(StatusEnum.READY_TO_SERVE);
            return kitchenOrderRepo.save(ko)!= null;
        }
        return false;
    }

    @Override
    public boolean completeOrder(String id) {
        KitchenOrder ko = kitchenOrderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Order with ID " + id + " not found"));
        if(ko.getStatus() == StatusEnum.READY_TO_SERVE)
        {
            ko.setStatus(StatusEnum.SERVED);
            return kitchenOrderRepo.save(ko) != null;
        }
        else{
            return false;
        }
    }

}
