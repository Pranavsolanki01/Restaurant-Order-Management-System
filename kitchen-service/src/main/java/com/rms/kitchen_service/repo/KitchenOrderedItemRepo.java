package com.rms.kitchen_service.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.rms.kitchen_service.entity.KitchenOrderedItem;
import com.rms.kitchen_service.enums.StatusEnum;


@Repository
public interface KitchenOrderedItemRepo extends MongoRepository< KitchenOrderedItem, String> {

    List<KitchenOrderedItem> findByKitchenOrderID(String kitchenOrderID);
    // KitchenOrderedItem findById(String id);
    List<KitchenOrderedItem> findByKitchenOrderIDAndStatus(String kitchenOrderID, StatusEnum status);

    long countByKitchenOrderIDAndStatus(String id, StatusEnum status);

    long countByKitchenOrderID(String id);

}
