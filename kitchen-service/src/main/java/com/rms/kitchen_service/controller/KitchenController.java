package com.rms.kitchen_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rms.kitchen_service.entity.KitchenOrderedItem;
import com.rms.kitchen_service.enums.StatusEnum;
import com.rms.kitchen_service.services.Interface.IKitchenService;




@RestController
@RequestMapping("/kitchen")
public class KitchenController {

    @Autowired
    private IKitchenService kitchenService;

    @PutMapping("/update-item-status/{id}/{status}")
    public ResponseEntity<?> updateStatus(@PathVariable String id, @PathVariable StatusEnum status){

        boolean isChanged=kitchenService.updateItemStatus(id, status);
        if(!isChanged)
            return ResponseEntity.ok("Status not updated");
        return ResponseEntity.ok("Status updated successfully");

    }

    @GetMapping("/check-order-status/{id}")
    public List<KitchenOrderedItem> getMethodName(@PathVariable String id) {
        return kitchenService.getKitchenOrderStatus(id);
    }
    

    @PutMapping("update-order-status/{id}")
    public ResponseEntity<?> changeOrderStatus(@PathVariable String id) {
        
        boolean isChanged=kitchenService.updateKitchenOrderStatus(id, StatusEnum.READY);
        
        if(!isChanged)
            return ResponseEntity.ok("Status not updated, items yet to be prepared!...");
        return ResponseEntity.ok("Order Status updated successfully (READY_TO_SERVE)");
    }

    @PutMapping("complete-order/{id}")
    public ResponseEntity<?> completeOrder(@PathVariable String id) {
        
        boolean isChanged=kitchenService.completeOrder(id);
        
        if(!isChanged)
            return ResponseEntity.ok("Status not updated, Order is not ready yet...");
        return ResponseEntity.ok("Order Status updated successfully (SERVED)");

    }

}
