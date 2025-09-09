package com.restaurant.payment.repository;

import com.restaurant.payment.entity.PaymentTransaction;
import com.restaurant.payment.entity.TransactionStatus;
import com.restaurant.payment.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    // Find transactions by payment ID
    List<PaymentTransaction> findByPaymentIdOrderByCreatedAtDesc(Long paymentId);

    // Find by transaction ID
    Optional<PaymentTransaction> findByTransactionId(String transactionId);

    // Find by gateway transaction ID
    Optional<PaymentTransaction> findByGatewayTransactionId(String gatewayTransactionId);

    // Find transactions by type and status
    List<PaymentTransaction> findByTransactionTypeAndStatusOrderByCreatedAtDesc(
            TransactionType transactionType, TransactionStatus status);

    // Find failed transactions
    @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.status = 'FAILED' AND pt.createdAt >= :fromDate")
    List<PaymentTransaction> findFailedTransactionsSince(@Param("fromDate") LocalDateTime fromDate);

    // Get transaction count by payment
    @Query("SELECT COUNT(pt) FROM PaymentTransaction pt WHERE pt.payment.id = :paymentId AND pt.transactionType = :type")
    Long countTransactionsByPaymentAndType(@Param("paymentId") Long paymentId, @Param("type") TransactionType type);
}
