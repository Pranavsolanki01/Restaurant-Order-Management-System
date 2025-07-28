package com.rms.kitchen_service.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.rms.kitchen_service.entity.KitchenOrder;

@Repository
public interface KitchenOrderRepo extends MongoRepository< KitchenOrder, String > {

}
