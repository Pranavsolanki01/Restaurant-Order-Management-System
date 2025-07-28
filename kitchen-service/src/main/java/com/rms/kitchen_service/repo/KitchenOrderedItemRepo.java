package com.rms.kitchen_service.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.rms.kitchen_service.entity.KitchenOrderedItem;

@Repository
public interface KitchenOrderedItemRepo extends MongoRepository< KitchenOrderedItem, String> {

}
