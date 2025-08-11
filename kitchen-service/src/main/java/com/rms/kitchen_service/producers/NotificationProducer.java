package com.rms.kitchen_service.producers;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.rms.kitchen_service.dto.NotificationService;

@Component
public class NotificationProducer {

    private final KafkaTemplate<String, NotificationService> kafkaTemplate;


    public NotificationProducer(KafkaTemplate<String, NotificationService> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(NotificationService message) {
        kafkaTemplate.send("kitchen-notifications", message);
    
    }
}
