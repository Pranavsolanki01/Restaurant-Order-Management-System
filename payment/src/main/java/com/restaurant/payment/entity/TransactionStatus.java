package com.restaurant.payment.entity;

public enum TransactionStatus {
    PENDING("Transaction is pending"),
    PROCESSING("Transaction is being processed"),
    COMPLETED("Transaction completed successfully"),
    FAILED("Transaction failed"),
    CANCELLED("Transaction was cancelled");

    private final String description;

    TransactionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED;
    }

    public boolean isSuccessful() {
        return this == COMPLETED;
    }
}
