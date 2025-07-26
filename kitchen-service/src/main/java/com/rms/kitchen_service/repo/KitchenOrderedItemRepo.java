package com.rms.kitchen_service.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.rms.kitchen_service.entity.KitchenOrderedItem;

public interface KitchenOrderedItemRepo extends MongoRepository< KitchenOrderedItem, String> {

}
