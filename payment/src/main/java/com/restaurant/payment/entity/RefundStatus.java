package com.restaurant.payment.entity;

public enum RefundStatus {
    NOT_REFUNDED("No refund initiated"),
    REFUND_PENDING("Refund is pending"),
    REFUND_PROCESSING("Refund is being processed"),
    REFUNDED("Fully refunded"),
    PARTIALLY_REFUNDED("Partially refunded"),
    REFUND_FAILED("Refund failed");

    private final String description;

    RefundStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
