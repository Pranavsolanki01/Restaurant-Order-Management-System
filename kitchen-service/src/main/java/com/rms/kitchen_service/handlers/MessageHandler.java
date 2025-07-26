package com.rms.kitchen_service.handlers;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.rms.kitchen_service.dto.KitchenOrderRequest;
import com.rms.kitchen_service.dto.KitchenOrderedItemRequest;
import com.rms.kitchen_service.dto.OrderMessageIncomming;
import com.rms.kitchen_service.enums.StatusEnum;
import com.rms.kitchen_service.services.KitchenService;

public class MessageHandler implements IMessageHandler {

    @Autowired
    private KitchenService kitchenService;

    public void messageProcess(OrderMessageIncomming orderMessage){

        KitchenOrderRequest kor = new KitchenOrderRequest();
        kor.setOrderID(orderMessage.getId());
        kor.setTableID(orderMessage.getTableID());
        kor.setStatus(StatusEnum.PENDING);
        kor.setCreatedAt(orderMessage.getCreatedAt());
        kor.setUpdatedAt(ZonedDateTime.now());
        kor.setSpecialInstructuction(orderMessage.getSpecialInstructions());
        kor.setUserName(orderMessage.getUserName());

        String kitchenOrderID= kitchenService.saveKitchenOrder(kor);

        // List<KitchenOrderedItemRequest> koir = new ArrayList<>();

        // for (OrderItemMessageIncomming orderedItem : orderMessage.getOrderItems()) {
        //     KitchenOrderedItemRequest itemRequest = new KitchenOrderedItemRequest();

        //     itemRequest.setKitchenOrderID(kitchenOrderID); // link to parent Kitchen Order
        //     itemRequest.setOderedItemID(orderedItem.getMenuItemId());
        //     itemRequest.setOrderedItemName(orderedItem.getMenuItemName());
        //     itemRequest.setQuantity(orderedItem.getQuantity());
        //     itemRequest.setStatus(StatusEnum.PENDING); // default status for new items
        //     itemRequest.setSpecialRequests(orderedItem.getSpecialRequests());

        //     koir.add(itemRequest);
        // }

        List<KitchenOrderedItemRequest> koir = orderMessage.getOrderItems()
                    .stream()
                    .map(orderedItem -> {
                        KitchenOrderedItemRequest itemRequest = new KitchenOrderedItemRequest();
                        itemRequest.setKitchenOrderID(kitchenOrderID);
                        itemRequest.setOderedItemID(orderedItem.getMenuItemId());
                        itemRequest.setOrderedItemName(orderedItem.getMenuItemName());
                        itemRequest.setQuantity(orderedItem.getQuantity());
                        itemRequest.setStatus(StatusEnum.PENDING);
                        itemRequest.setSpecialRequests(orderedItem.getSpecialRequests());
                        return itemRequest;
                    })
                    .collect(Collectors.toList());
            
        kitchenService.saveKitchenOrderedItem(koir);
    }

}
