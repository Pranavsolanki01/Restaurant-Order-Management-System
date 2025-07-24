package com.restaurant.order.service.interfaces;

import com.restaurant.order.dto.OrderRequestDTO;
import com.restaurant.order.dto.OrderResponseDTO;
import com.restaurant.order.entity.Order;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface IOrderService {

    /**
     * Create a new order for the authenticated user
     * 
     * @param orderRequestDTO Order details
     * @return Created order response
     */
    OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO);

    /**
     * Get all orders for the authenticated user
     * 
     * @return List of user's orders
     */
    List<OrderResponseDTO> getUserOrders();

    /**
     * Get paginated orders for the authenticated user
     * 
     * @param page Page number (0-based)
     * @param size Page size
     * @return Paginated orders
     */
    Page<OrderResponseDTO> getUserOrdersPaginated(int page, int size);

    /**
     * Get a specific order by ID for the authenticated user
     * 
     * @param orderId Order ID
     * @return Order details
     */
    OrderResponseDTO getOrderById(Long orderId);

    /**
     * Update order status (Admin only)
     * 
     * @param orderId   Order ID
     * @param newStatus New order status
     * @return Updated order
     */
    OrderResponseDTO updateOrderStatus(Long orderId, Order.OrderStatus newStatus);

    /**
     * Update payment status for an order
     * 
     * @param orderId          Order ID
     * @param newPaymentStatus New payment status
     * @return Updated order
     */
    OrderResponseDTO updatePaymentStatus(Long orderId, Order.PaymentStatus newPaymentStatus);

    /**
     * Cancel an order (if cancellable)
     * 
     * @param orderId Order ID
     */
    void cancelOrder(Long orderId);

    /**
     * Get orders by status (Admin only)
     * 
     * @param status Order status
     * @return List of orders with the specified status
     */
    List<OrderResponseDTO> getOrdersByStatus(Order.OrderStatus status);

    /**
     * Get user orders within a date range
     * 
     * @param startDate Start date
     * @param endDate   End date
     * @return List of orders within the date range
     */
    List<OrderResponseDTO> getUserOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get count of user orders by status
     * 
     * @param status Order status
     * @return Count of orders
     */
    Long getUserOrderCount(Order.OrderStatus status);
}
