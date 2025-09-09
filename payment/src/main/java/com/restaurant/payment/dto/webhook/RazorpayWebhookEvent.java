package com.restaurant.payment.dto.webhook;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RazorpayWebhookEvent {

    private String entity;
    private String account_id;
    private String event;
    private Map<String, Object> payload;
    private Long created_at;

    // Specific getters for payment events
    public Map<String, Object> getPaymentData() {
        if (payload != null && payload.containsKey("payment")) {
            return (Map<String, Object>) ((Map<String, Object>) payload.get("payment")).get("entity");
        }
        return null;
    }

    public String getPaymentId() {
        Map<String, Object> paymentData = getPaymentData();
        return paymentData != null ? (String) paymentData.get("id") : null;
    }

    public String getOrderId() {
        Map<String, Object> paymentData = getPaymentData();
        return paymentData != null ? (String) paymentData.get("order_id") : null;
    }

    public String getPaymentStatus() {
        Map<String, Object> paymentData = getPaymentData();
        return paymentData != null ? (String) paymentData.get("status") : null;
    }
}
