package com.rms.kitchen_service.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.rms.kitchen_service.entity.KitchenOrder;

public interface KitchenOrderRepo extends MongoRepository< KitchenOrder, String > {

}
