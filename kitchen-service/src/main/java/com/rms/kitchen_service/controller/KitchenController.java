package com.rms.kitchen_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rms.kitchen_service.entity.KitchenOrder;
import com.rms.kitchen_service.entity.KitchenOrderedItem;
import com.rms.kitchen_service.enums.StatusEnum;
import com.rms.kitchen_service.services.Interface.IKitchenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/kitchen")
@Tag(name = "Kitchen Management", description = "APIs for managing Kitchen orders")
public class KitchenController {

    @Autowired
    private IKitchenService kitchenService;

    @PutMapping("/update-item-status/{id}/{status}")
    @Operation(summary = "Update ordered Item status", description = "Updated the ordered item status by using kitchenOrderID and status")
    public ResponseEntity<?> updateStatus(@PathVariable String id, @PathVariable StatusEnum status){

        boolean isChanged=kitchenService.updateItemStatus(id, status);
        if(!isChanged)
            return ResponseEntity.ok("Status not updated");
        return ResponseEntity.ok("Status updated successfully");

    }

    @GetMapping("/check-order-status/{id}")
    @Operation(summary = "Get all order status", description = "Show all ordered items status by using kitchenOrderID")
    public List<KitchenOrderedItem> getMethodName(@PathVariable String id) {
        return kitchenService.getKitchenOrderStatus(id);
    }
    

    @PutMapping("update-order-status/{id}")
    @Operation(summary = "Update order", description = "Update the order by changing its status to READY, using kitchenOrderID")
    public ResponseEntity<?> changeOrderStatus(@PathVariable String id) {
        
        KitchenOrder isChanged=kitchenService.updateKitchenOrderStatus(id, StatusEnum.READY);
        
        if(isChanged == null)
            return ResponseEntity.ok("Status not updated, items yet to be prepared!...");

        
        kitchenService.producerService(isChanged);
        return ResponseEntity.ok("Order Status updated successfully (READY_TO_SERVE)");
    }

    @PutMapping("complete-order/{id}")
    @Operation(summary = "Complete Order", description = "Complete the order by changing its status to SERVED, using kitchenOrderID")
    public ResponseEntity<?> completeOrder(@PathVariable String id) {
        
        KitchenOrder isChanged=kitchenService.completeOrder(id);
        
        if(isChanged == null)
            return ResponseEntity.ok("Status not updated, Order is not ready yet...");

        kitchenService.producerService(isChanged);
        return ResponseEntity.ok("Order Status updated successfully (SERVED)");

    }

    @GetMapping("/allOrders")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all orders", description = "Show all orders")
    public List<KitchenOrder> getAll() {
        return kitchenService.getAllOrders();
    }
    

}
