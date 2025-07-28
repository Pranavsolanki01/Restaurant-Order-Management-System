package com.rms.kitchen_service.handlers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rms.kitchen_service.dto.KitchenOrderRequest;
import com.rms.kitchen_service.dto.KitchenOrderedItemRequest;
import com.rms.kitchen_service.dto.OrderMessageIncomming;
import com.rms.kitchen_service.enums.StatusEnum;
import com.rms.kitchen_service.services.Interface.IKitchenService;

@Component
public class MessageHandler implements IMessageHandler {

    @Autowired
    private IKitchenService kitchenService;

    public void messageProcess(OrderMessageIncomming orderMessage){

        KitchenOrderRequest kor = new KitchenOrderRequest();
        kor.setOrderID(orderMessage.getOrderId());
        // kor.setTableID(orderMessage.getTableID());
        kor.setTotalPrice(orderMessage.getTotalPrice());
        kor.setUserID(orderMessage.getUserId());
        kor.setStatus(StatusEnum.PENDING);
        kor.setCreatedAt(orderMessage.getTimestamp());
        kor.setUpdatedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
        kor.setSpecialInstructions(orderMessage.getSpecialInstructions());
        kor.setUserEmail(orderMessage.getUserEmail());

        String kitchenOrderID= kitchenService.saveKitchenOrder(kor);

        List<KitchenOrderedItemRequest> koir = orderMessage.getOrderItems()
                    .stream()
                    .map(orderedItem -> {
                        KitchenOrderedItemRequest itemRequest = new KitchenOrderedItemRequest();
                        itemRequest.setKitchenOrderID(kitchenOrderID);
                        itemRequest.setOderedItemID(orderedItem.getMenuItemId());
                        itemRequest.setOrderedItemName(orderedItem.getMenuItemName());
                        itemRequest.setQuantity(orderedItem.getQuantity());
                        itemRequest.setStatus(StatusEnum.PENDING);
                        itemRequest.setTotalPrice(orderedItem.getTotalPrice());
                        itemRequest.setUnitPrice(orderedItem.getUnitPrice());
                        itemRequest.setSpecialRequests(orderedItem.getSpecialRequests());
                        return itemRequest;
                    })
                    .collect(Collectors.toList());
            
        kitchenService.saveKitchenOrderedItem(koir);
    }

}
