package com.restaurant.order.repository;

import com.restaurant.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.userId = :userId")
    List<OrderItem> findByUserId(@Param("userId") Long userId);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.menuItemId = :menuItemId")
    List<OrderItem> findByMenuItemId(@Param("menuItemId") Long menuItemId);

    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.menuItemId = :menuItemId")
    Long getTotalQuantityByMenuItemId(@Param("menuItemId") Long menuItemId);
}
