package com.rms.kitchen_service.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rms.kitchen_service.dto.KitchenOrderRequest;
import com.rms.kitchen_service.dto.KitchenOrderedItemRequest;
import com.rms.kitchen_service.dto.NotificationService;
import com.rms.kitchen_service.entity.KitchenOrder;
import com.rms.kitchen_service.entity.KitchenOrderedItem;
import com.rms.kitchen_service.enums.StatusEnum;
import com.rms.kitchen_service.mapper.KitchenOrderRequestToKitchenOrderMapper;
import com.rms.kitchen_service.mapper.KitchenOrderedItemRequestToKitchenOrderedItemMapper;
import com.rms.kitchen_service.mapper.KithcenOrderToNotificationServiceMapper;
import com.rms.kitchen_service.producers.NotificationProducer;
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

    @Autowired
    private final NotificationProducer notificationProducer;


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
    public KitchenOrder updateKitchenOrderStatus(String id, StatusEnum status) {

        long count = kitchenOrderedItemRepo.countByKitchenOrderID(id);

        long count2 = kitchenOrderedItemRepo.countByKitchenOrderIDAndStatus(id, status);
        
        if(count == count2){
            KitchenOrder ko = kitchenOrderRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Order with ID " + id + " not found"));
            ko.setStatus(StatusEnum.READY_TO_SERVE);
            kitchenOrderRepo.save(ko);

            return ko;
        }
        return null;
    }

    @Override
    public KitchenOrder completeOrder(String id) {
        KitchenOrder ko = kitchenOrderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Order with ID " + id + " not found"));
        if(ko.getStatus() == StatusEnum.READY_TO_SERVE)
        {
            ko.setStatus(StatusEnum.SERVED);
            kitchenOrderRepo.save(ko);
            return ko;
        }
        else{
            return null;
        }
    }

    @Override
    public List<KitchenOrder> getAllOrders() {
        return kitchenOrderRepo.findAll();
    }

    @Override
    public void producerService(KitchenOrder message) {

        NotificationService notificationService = KithcenOrderToNotificationServiceMapper.mapper(message);
        notificationService.setOrderedItems(kitchenOrderedItemRepo.findByKitchenOrderID(message.getId()));

        notificationProducer.sendMessage(notificationService);
    }

}
