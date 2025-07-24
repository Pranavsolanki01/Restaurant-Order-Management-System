package com.restaurant.order.service.interfaces;

import com.restaurant.order.dto.OrderEventDTO;

public interface IOrderEventPublisher {

    /**
     * Publish order event to Kafka
     * 
     * @param orderEvent Order event DTO
     */
    void publishOrderEvent(OrderEventDTO orderEvent);

    /**
     * Publish order placed event
     * 
     * @param orderEvent Order event DTO
     */
    void publishOrderPlaced(OrderEventDTO orderEvent);

    /**
     * Publish order confirmed event
     * 
     * @param orderEvent Order event DTO
     */
    void publishOrderConfirmed(OrderEventDTO orderEvent);

    /**
     * Publish order cancelled event
     * 
     * @param orderEvent Order event DTO
     */
    void publishOrderCancelled(OrderEventDTO orderEvent);

    /**
     * Publish order status changed event
     * 
     * @param orderEvent Order event DTO
     */
    void publishOrderStatusChanged(OrderEventDTO orderEvent);
}
