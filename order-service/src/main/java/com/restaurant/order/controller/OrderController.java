package com.restaurant.order.controller;

import com.restaurant.order.dto.OrderRequestDTO;
import com.restaurant.order.dto.OrderResponseDTO;
import com.restaurant.order.entity.Order;
import com.restaurant.order.service.interfaces.IOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Order Management", description = "APIs for managing restaurant orders")
@SecurityRequirement(name = "Bearer Authentication")
public class OrderController {

    private final IOrderService orderService;

    @PostMapping
    @Operation(summary = "Place a new order", description = "Create a new order for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully", content = @Content(schema = @Schema(implementation = OrderResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<OrderResponseDTO> createOrder(
            @Valid @RequestBody OrderRequestDTO orderRequestDTO) {
        OrderResponseDTO orderResponse = orderService.createOrder(orderRequestDTO);
        return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get user orders", description = "Retrieve all orders for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<OrderResponseDTO>> getUserOrders() {
        log.info("Received request to get user orders");
        List<OrderResponseDTO> orders = orderService.getUserOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/paginated")
    @Operation(summary = "Get user orders with pagination", description = "Retrieve orders for the authenticated user with pagination")
    public ResponseEntity<Page<OrderResponseDTO>> getUserOrdersPaginated(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.info("Received request to get user orders with pagination - page: {}, size: {}", page, size);
        Page<OrderResponseDTO> orders = orderService.getUserOrdersPaginated(page, size);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID", description = "Retrieve a specific order by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<OrderResponseDTO> getOrderById(
            @Parameter(description = "Order ID") @PathVariable Long orderId) {
        log.info("Received request to get order by ID: {}", orderId);
        OrderResponseDTO order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update order status", description = "Update the status of an order (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid status"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin only"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @Parameter(description = "Order ID") @PathVariable Long orderId,
            @Parameter(description = "New order status") @RequestParam Order.OrderStatus status) {
        log.info("Received request to update order {} status to: {}", orderId, status);
        OrderResponseDTO updatedOrder = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(updatedOrder);
    }

    @PutMapping("/{orderId}/payment-status")
    @Operation(summary = "Update payment status", description = "Update payment status for an order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied - Order owner or Admin only"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<OrderResponseDTO> updatePaymentStatus(
            @Parameter(description = "Order ID") @PathVariable Long orderId,
            @Parameter(description = "New payment status") @RequestParam Order.PaymentStatus paymentStatus) {
        log.info("Received request to update order {} payment status to: {}", orderId, paymentStatus);
        OrderResponseDTO updatedOrder = orderService.updatePaymentStatus(orderId, paymentStatus);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{orderId}")
    @Operation(summary = "Cancel order", description = "Cancel an order (only if not delivered or already cancelled)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Order cancelled successfully"),
            @ApiResponse(responseCode = "400", description = "Order cannot be cancelled"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> cancelOrder(
            @Parameter(description = "Order ID") @PathVariable Long orderId) {
        log.info("Received request to cancel order: {}", orderId);
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-date-range")
    @Operation(summary = "Get orders by date range", description = "Retrieve user orders within a specific date range")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByDateRange(
            @Parameter(description = "Start date (yyyy-MM-dd'T'HH:mm:ss)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date (yyyy-MM-dd'T'HH:mm:ss)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Received request to get orders between {} and {}", startDate, endDate);
        List<OrderResponseDTO> orders = orderService.getUserOrdersByDateRange(startDate, endDate);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/count")
    @Operation(summary = "Get order count by status", description = "Get the count of user orders by status")
    public ResponseEntity<Map<String, Object>> getOrderCount(
            @Parameter(description = "Order status") @RequestParam Order.OrderStatus status) {
        log.info("Received request to get order count for status: {}", status);
        Long count = orderService.getUserOrderCount(status);
        return ResponseEntity.ok(Map.of("status", status.name(), "count", count));
    }

    // Admin endpoints
    @GetMapping("/admin/by-status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all orders by status", description = "Retrieve all orders with a specific status (Admin only)")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByStatus(
            @Parameter(description = "Order status") @RequestParam Order.OrderStatus status) {
        log.info("Admin request to get orders by status: {}", status);
        List<OrderResponseDTO> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }
}
