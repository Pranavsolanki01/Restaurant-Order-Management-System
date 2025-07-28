package com.rms.kitchen_service.consumers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.rms.kitchen_service.dto.OrderMessageIncomming;
import com.rms.kitchen_service.handlers.IMessageHandler;

@Component
public class OderEventMessageConsumer {

    @Autowired
    private IMessageHandler messageHandler;

    @KafkaListener(topics = "order-events")
    public void consume(@Payload OrderMessageIncomming orderMessageIncomming)
    {
        messageHandler.messageProcess(orderMessageIncomming);
    }

}
