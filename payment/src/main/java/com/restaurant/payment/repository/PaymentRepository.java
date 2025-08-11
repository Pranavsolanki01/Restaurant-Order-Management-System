package com.restaurant.payment.repository;

import com.restaurant.payment.entity.Payment;
import com.restaurant.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Find by order ID
    Optional<Payment> findByOrderId(Long orderId);

    // Find by user ID
    List<Payment> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Find by user ID and status
    List<Payment> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, PaymentStatus status);

    // Find by Razorpay order ID
    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);

    // Find by Razorpay payment ID
    Optional<Payment> findByRazorpayPaymentId(String razorpayPaymentId);

    // Find by status
    List<Payment> findByStatusOrderByCreatedAtDesc(PaymentStatus status);

    // Find payments by date range
    @Query("SELECT p FROM Payment p WHERE p.createdAt BETWEEN :startDate AND :endDate ORDER BY p.createdAt DESC")
    List<Payment> findPaymentsByDateRange(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Find pending payments older than specified time
    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' AND p.createdAt < :cutoffTime")
    List<Payment> findPendingPaymentsOlderThan(@Param("cutoffTime") LocalDateTime cutoffTime);

    // Get payment statistics
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = :status AND p.createdAt >= :fromDate")
    Long countPaymentsByStatusSince(@Param("status") PaymentStatus status, @Param("fromDate") LocalDateTime fromDate);

    // Check if order already has a payment
    boolean existsByOrderId(Long orderId);

    // Find user's recent payments
    @Query("SELECT p FROM Payment p WHERE p.userId = :userId ORDER BY p.createdAt DESC LIMIT :limit")
    List<Payment> findRecentPaymentsByUser(@Param("userId") Long userId, @Param("limit") int limit);
}
