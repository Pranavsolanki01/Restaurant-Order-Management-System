package com.restaurant.order.service;

import com.restaurant.order.dto.*;
import com.restaurant.order.entity.Order;
import com.restaurant.order.entity.OrderItem;
import com.restaurant.order.service.interfaces.IOrderMapperService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderMapperService implements IOrderMapperService {

        @Override
        public OrderResponseDTO toOrderResponseDTO(Order order) {
                return OrderResponseDTO.builder()
                                .id(order.getId())
                                .userId(order.getUserId())
                                .userEmail(order.getUserEmail())
                                .totalPrice(order.getTotalPrice())
                                .status(order.getStatus())
                                .paymentStatus(order.getPaymentStatus())
                                .deliveryAddress(order.getDeliveryAddress())
                                .phoneNumber(order.getPhoneNumber())
                                .specialInstructions(order.getSpecialInstructions())
                                .orderItems(order.getOrderItems().stream()
                                                .map(this::toOrderItemResponseDTO)
                                                .collect(Collectors.toList()))
                                .createdAt(order.getCreatedAt())
                                .updatedAt(order.getUpdatedAt())
                                .build();
        }

        @Override
        public OrderItemResponseDTO toOrderItemResponseDTO(OrderItem orderItem) {
                return OrderItemResponseDTO.builder()
                                .id(orderItem.getId())
                                .menuItemId(orderItem.getMenuItemId())
                                .menuItemName(orderItem.getMenuItemName())
                                .quantity(orderItem.getQuantity())
                                .unitPrice(orderItem.getUnitPrice())
                                .price(orderItem.getPrice())
                                .specialRequests(orderItem.getSpecialRequests())
                                .build();
        }

        @Override
        public Order toOrder(OrderRequestDTO orderRequestDTO, Long userId, String userEmail) {
                BigDecimal totalPrice = orderRequestDTO.getOrderItems().stream()
                                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                return Order.builder()
                                .userId(userId)
                                .userEmail(userEmail)
                                .totalPrice(totalPrice)
                                .status(Order.OrderStatus.PENDING)
                                .paymentStatus(Order.PaymentStatus.PENDING)
                                .deliveryAddress(orderRequestDTO.getDeliveryAddress())
                                .phoneNumber(orderRequestDTO.getPhoneNumber())
                                .specialInstructions(orderRequestDTO.getSpecialInstructions())
                                .build();
        }

        @Override
        public OrderItem toOrderItem(OrderItemRequestDTO itemDTO, Order order) {
                BigDecimal totalPrice = itemDTO.getUnitPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity()));

                return OrderItem.builder()
                                .order(order)
                                .menuItemId(itemDTO.getMenuItemId())
                                .menuItemName(itemDTO.getMenuItemName())
                                .quantity(itemDTO.getQuantity())
                                .unitPrice(itemDTO.getUnitPrice())
                                .price(totalPrice)
                                .specialRequests(itemDTO.getSpecialRequests())
                                .build();
        }

        @Override
        public OrderEventDTO toOrderEventDTO(Order order, String eventType) {
                List<OrderEventDTO.OrderItemEventDTO> orderItemEvents = order.getOrderItems().stream()
                                .map(this::toOrderItemEventDTO)
                                .collect(Collectors.toList());

                return OrderEventDTO.builder()
                                .orderId(order.getId())
                                .userId(order.getUserId())
                                .userEmail(order.getUserEmail())
                                .eventType(eventType)
                                .totalPrice(order.getTotalPrice())
                                .status(order.getStatus().name())
                                .paymentStatus(order.getPaymentStatus().name())
                                .orderItems(orderItemEvents)
                                .timestamp(LocalDateTime.now())
                                .build();
        }

        private OrderEventDTO.OrderItemEventDTO toOrderItemEventDTO(OrderItem orderItem) {
                return OrderEventDTO.OrderItemEventDTO.builder()
                                .menuItemId(orderItem.getMenuItemId())
                                .menuItemName(orderItem.getMenuItemName())
                                .quantity(orderItem.getQuantity())
                                .unitPrice(orderItem.getUnitPrice())
                                .totalPrice(orderItem.getPrice())
                                .build();
        }
}
