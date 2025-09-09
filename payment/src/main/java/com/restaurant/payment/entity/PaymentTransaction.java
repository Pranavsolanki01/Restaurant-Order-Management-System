package com.restaurant.payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "gateway_transaction_id")
    private String gatewayTransactionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatus status;

    @Column(name = "gateway_response", columnDefinition = "TEXT")
    private String gatewayResponse;

    @Column(name = "error_code")
    private String errorCode;

    @Column(name = "error_description")
    private String errorDescription;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructor for creating new transaction
    public PaymentTransaction(Payment payment, TransactionType type, BigDecimal amount) {
        this.payment = payment;
        this.transactionType = type;
        this.amount = amount;
        this.status = TransactionStatus.PENDING;
    }

    public void markAsCompleted(String transactionId, String gatewayTransactionId) {
        this.status = TransactionStatus.COMPLETED;
        this.transactionId = transactionId;
        this.gatewayTransactionId = gatewayTransactionId;
    }

    public void markAsFailed(String errorCode, String errorDescription) {
        this.status = TransactionStatus.FAILED;
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
    }
}
