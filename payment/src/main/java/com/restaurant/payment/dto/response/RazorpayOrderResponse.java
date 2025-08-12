package com.restaurant.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RazorpayOrderResponse {

    private String razorpayOrderId;
    private String amount;
    private String currency;
    private String key; // Razorpay key for frontend
    private String name; // Merchant name
    private String description;

    // Additional options for Razorpay checkout
    private Map<String, Object> theme;
    private Map<String, String> prefill;
    private Map<String, Object> notes;

    // Our internal payment ID
    private Long paymentId;
}
