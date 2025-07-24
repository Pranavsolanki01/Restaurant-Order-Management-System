package com.restaurant.order.service;

import com.restaurant.order.dto.OrderEventDTO;
import com.restaurant.order.service.interfaces.IOrderEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher implements IOrderEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String ORDER_TOPIC = "order-events";

    @Override
    public void publishOrderEvent(OrderEventDTO orderEvent) {
        try {
            log.info("Publishing order event: {}", orderEvent);

            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(ORDER_TOPIC,
                    orderEvent.getOrderId().toString(), orderEvent);

            future.whenComplete((result, exception) -> {
                if (exception == null) {
                    log.info("Successfully published order event with orderId: {} to topic: {}",
                            orderEvent.getOrderId(), ORDER_TOPIC);
                } else {
                    log.error("Failed to publish order event with orderId: {} to topic: {}. Error: {}",
                            orderEvent.getOrderId(), ORDER_TOPIC, exception.getMessage());
                }
            });
        } catch (Exception e) {
            log.error("Error publishing order event: {}", e.getMessage(), e);
        }
    }

    @Override
    public void publishOrderPlaced(OrderEventDTO orderEvent) {
        orderEvent.setEventType("ORDER_PLACED");
        publishOrderEvent(orderEvent);
    }

    @Override
    public void publishOrderConfirmed(OrderEventDTO orderEvent) {
        orderEvent.setEventType("ORDER_CONFIRMED");
        publishOrderEvent(orderEvent);
    }

    @Override
    public void publishOrderCancelled(OrderEventDTO orderEvent) {
        orderEvent.setEventType("ORDER_CANCELLED");
        publishOrderEvent(orderEvent);
    }

    @Override
    public void publishOrderStatusChanged(OrderEventDTO orderEvent) {
        orderEvent.setEventType("ORDER_STATUS_CHANGED");
        publishOrderEvent(orderEvent);
    }
}
