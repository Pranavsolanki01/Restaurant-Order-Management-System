package com.restaurant.payment.entity;

public enum TransactionType {
    PAYMENT("Payment transaction"),
    REFUND("Refund transaction"),
    PARTIAL_REFUND("Partial refund transaction"),
    CHARGEBACK("Chargeback transaction");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
