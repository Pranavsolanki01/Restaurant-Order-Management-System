package com.rms.kitchen_service.consumers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;

import com.rms.kitchen_service.dto.OrderMessageIncomming;
import com.rms.kitchen_service.handlers.MessageHandler;

public class OderEventMessageConsumer {

    @Autowired
    private MessageHandler messageHandler;

    @KafkaListener(topics = "order-events", groupId = "kitchen_group")
    public void consume(@Payload OrderMessageIncomming orderMessageIncomming)
    {
        messageHandler.messageProcess(orderMessageIncomming);
    }

}
