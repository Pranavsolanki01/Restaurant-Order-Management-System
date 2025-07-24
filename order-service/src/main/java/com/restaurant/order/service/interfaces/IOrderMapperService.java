package com.restaurant.order.service.interfaces;

import com.restaurant.order.dto.*;
import com.restaurant.order.entity.Order;
import com.restaurant.order.entity.OrderItem;

public interface IOrderMapperService {

    /**
     * Convert Order entity to OrderResponseDTO
     * 
     * @param order Order entity
     * @return OrderResponseDTO
     */
    OrderResponseDTO toOrderResponseDTO(Order order);

    /**
     * Convert OrderItem entity to OrderItemResponseDTO
     * 
     * @param orderItem OrderItem entity
     * @return OrderItemResponseDTO
     */
    OrderItemResponseDTO toOrderItemResponseDTO(OrderItem orderItem);

    /**
     * Convert OrderRequestDTO to Order entity
     * 
     * @param orderRequestDTO Order request DTO
     * @param userId          User ID
     * @param userEmail       User email
     * @return Order entity
     */
    Order toOrder(OrderRequestDTO orderRequestDTO, Long userId, String userEmail);

    /**
     * Convert OrderItemRequestDTO to OrderItem entity
     * 
     * @param itemDTO Order item request DTO
     * @param order   Parent order
     * @return OrderItem entity
     */
    OrderItem toOrderItem(OrderItemRequestDTO itemDTO, Order order);

    /**
     * Convert Order entity to OrderEventDTO for Kafka events
     * 
     * @param order     Order entity
     * @param eventType Event type
     * @return OrderEventDTO
     */
    OrderEventDTO toOrderEventDTO(Order order, String eventType);
}
