package com.rms.notification.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;

import com.rms.notification.dto.IncommingKitchenMessages;
import com.rms.notification.dto.IncommingOrderMessages;
import com.rms.notification.dto.IncommingPaymentsMessages;

public class AllTopicsConsumer {

    @KafkaListener(topics = "kitchen-notifications")
    public void consume(@Payload IncommingKitchenMessages message){

    }

    @KafkaListener(topics = "order-notifications")
    public void consume(@Payload IncommingOrderMessages message) {

    }

    @KafkaListener(topics = "payments-notifications")
    public void consume(@Payload IncommingPaymentsMessages message) {

    }
}