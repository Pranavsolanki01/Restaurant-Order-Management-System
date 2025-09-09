package com.restaurant.payment.entity;

public enum PaymentMethod {
    CREDIT_CARD("Credit Card"),
    DEBIT_CARD("Debit Card"),
    UPI("UPI"),
    NET_BANKING("Net Banking"),
    WALLET("Digital Wallet"),
    COD("Cash on Delivery"),
    UNKNOWN("Unknown");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PaymentMethod fromString(String method) {
        if (method == null)
            return UNKNOWN;

        return switch (method.toLowerCase()) {
            case "card", "credit_card", "creditcard" -> CREDIT_CARD;
            case "debit_card", "debitcard" -> DEBIT_CARD;
            case "upi" -> UPI;
            case "netbanking", "net_banking" -> NET_BANKING;
            case "wallet" -> WALLET;
            case "cod", "cash" -> COD;
            default -> UNKNOWN;
        };
    }
}
