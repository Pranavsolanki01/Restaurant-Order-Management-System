package com.restaurant.order.service;

import com.restaurant.order.dto.OrderEventDTO;
import com.restaurant.order.dto.OrderRequestDTO;
import com.restaurant.order.dto.OrderResponseDTO;
import com.restaurant.order.entity.Order;
import com.restaurant.order.entity.OrderItem;
import com.restaurant.order.exception.OrderNotFoundException;
import com.restaurant.order.exception.UnauthorizedAccessException;
import com.restaurant.order.repository.OrderRepository;
import com.restaurant.order.service.interfaces.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService implements IOrderService {

    private final OrderRepository orderRepository;
    private final JwtService jwtService;
    private final OrderMapperService orderMapper;
    private final OrderEventPublisher orderEventPublisher;

    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO) {
        // Get current user from JWT token
        Long userId = jwtService.getCurrentUserId();
        String userEmail = jwtService.getCurrentUserEmail();

        // Create order entity
        Order order = orderMapper.toOrder(orderRequestDTO, userId, userEmail);

        // Create order items
        List<OrderItem> orderItems = orderRequestDTO.getOrderItems().stream()
                .map(itemDTO -> orderMapper.toOrderItem(itemDTO, order))
                .collect(Collectors.toList());

        order.setOrderItems(orderItems);

        // Save order
        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with ID: {}", savedOrder.getId());

        // Publish order placed event
        OrderEventDTO orderEvent = orderMapper.toOrderEventDTO(savedOrder, "ORDER_PLACED");
        orderEventPublisher.publishOrderPlaced(orderEvent);

        return orderMapper.toOrderResponseDTO(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getUserOrders() {
        Long userId = jwtService.getCurrentUserId();
        log.info("Fetching orders for user ID: {}", userId);

        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return orders.stream()
                .map(orderMapper::toOrderResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponseDTO> getUserOrdersPaginated(int page, int size) {
        Long userId = jwtService.getCurrentUserId();
        log.info("Fetching paginated orders for user ID: {} - page: {}, size: {}", userId, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

        return orders.map(orderMapper::toOrderResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderById(Long orderId) {
        Long userId = jwtService.getCurrentUserId();
        log.info("Fetching order ID: {} for user ID: {}", orderId, userId);

        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

        return orderMapper.toOrderResponseDTO(order);
    }

    @Override
    public OrderResponseDTO updateOrderStatus(Long orderId, Order.OrderStatus newStatus) {
        log.info("Updating order ID: {} status to: {}", orderId, newStatus);

        // Only allow admins to update order status
        if (!jwtService.isAdmin()) {
            throw new UnauthorizedAccessException("Only administrators can update order status");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

        Order.OrderStatus oldStatus = order.getStatus();
        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        log.info("Order ID: {} status updated from {} to {}", orderId, oldStatus, newStatus);

        // Publish status change event
        OrderEventDTO orderEvent = orderMapper.toOrderEventDTO(updatedOrder, "ORDER_STATUS_CHANGED");
        orderEventPublisher.publishOrderStatusChanged(orderEvent);

        return orderMapper.toOrderResponseDTO(updatedOrder);
    }

    @Override
    public OrderResponseDTO updatePaymentStatus(Long orderId, Order.PaymentStatus newPaymentStatus) {
        log.info("Updating order ID: {} payment status to: {}", orderId, newPaymentStatus);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

        // Check if the current user is the order owner or admin
        Long currentUserId = jwtService.getCurrentUserId();
        if (!order.getUserId().equals(currentUserId) && !jwtService.isAdmin()) {
            throw new UnauthorizedAccessException("Access denied for order ID: " + orderId);
        }

        Order.PaymentStatus oldPaymentStatus = order.getPaymentStatus();
        order.setPaymentStatus(newPaymentStatus);

        // Auto-update order status based on payment status
        if (newPaymentStatus == Order.PaymentStatus.COMPLETED && order.getStatus() == Order.OrderStatus.PENDING) {
            order.setStatus(Order.OrderStatus.CONFIRMED);
            log.info("Order ID: {} auto-confirmed due to successful payment", orderId);
        } else if (newPaymentStatus == Order.PaymentStatus.FAILED
                || newPaymentStatus == Order.PaymentStatus.CANCELLED) {
            order.setStatus(Order.OrderStatus.CANCELLED);
            log.info("Order ID: {} auto-cancelled due to payment failure/cancellation", orderId);
        }

        Order updatedOrder = orderRepository.save(order);

        log.info("Order ID: {} payment status updated from {} to {}", orderId, oldPaymentStatus, newPaymentStatus);

        // Publish payment status change event
        OrderEventDTO paymentEvent = orderMapper.toOrderEventDTO(updatedOrder, "PAYMENT_STATUS_CHANGED");
        orderEventPublisher.publishOrderEvent(paymentEvent);

        return orderMapper.toOrderResponseDTO(updatedOrder);
    }

    @Override
    public void cancelOrder(Long orderId) {
        Long userId = jwtService.getCurrentUserId();
        log.info("Cancelling order ID: {} for user ID: {}", orderId, userId);

        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

        // Check if order can be cancelled
        if (order.getStatus() == Order.OrderStatus.DELIVERED ||
                order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel order with status: " + order.getStatus());
        }

        order.setStatus(Order.OrderStatus.CANCELLED);

        // Update payment status based on current payment state
        if (order.getPaymentStatus() == Order.PaymentStatus.COMPLETED) {
            order.setPaymentStatus(Order.PaymentStatus.REFUNDED);
            log.info("Order ID: {} payment status set to REFUNDED due to cancellation", orderId);
        } else if (order.getPaymentStatus() == Order.PaymentStatus.PENDING ||
                order.getPaymentStatus() == Order.PaymentStatus.PROCESSING) {
            order.setPaymentStatus(Order.PaymentStatus.CANCELLED);
            log.info("Order ID: {} payment status set to CANCELLED", orderId);
        }

        Order cancelledOrder = orderRepository.save(order);

        log.info("Order ID: {} cancelled successfully", orderId);

        // Publish cancellation event
        OrderEventDTO orderEvent = orderMapper.toOrderEventDTO(cancelledOrder, "ORDER_CANCELLED");
        orderEventPublisher.publishOrderCancelled(orderEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrdersByStatus(Order.OrderStatus status) {
        // Only allow admins to view orders by status
        if (!jwtService.isAdmin()) {
            throw new UnauthorizedAccessException("Only administrators can view orders by status");
        }

        log.info("Fetching orders with status: {}", status);
        List<Order> orders = orderRepository.findByStatus(status);
        return orders.stream()
                .map(orderMapper::toOrderResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getUserOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        Long userId = jwtService.getCurrentUserId();
        log.info("Fetching orders for user ID: {} between {} and {}", userId, startDate, endDate);

        List<Order> orders = orderRepository.findByUserIdAndDateRange(userId, startDate, endDate);
        return orders.stream()
                .map(orderMapper::toOrderResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUserOrderCount(Order.OrderStatus status) {
        Long userId = jwtService.getCurrentUserId();
        return orderRepository.countByUserIdAndStatus(userId, status);
    }
}
