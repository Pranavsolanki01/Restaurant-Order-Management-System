package com.restaurant.payment.exception;

public class PaymentNotFoundException extends PaymentException {

    public PaymentNotFoundException(String message) {
        super(message);
    }

    public PaymentNotFoundException(Long paymentId) {
        super("Payment not found with ID: " + paymentId);
    }

    public PaymentNotFoundException(String field, String value) {
        super("Payment not found with " + field + ": " + value);
    }
}