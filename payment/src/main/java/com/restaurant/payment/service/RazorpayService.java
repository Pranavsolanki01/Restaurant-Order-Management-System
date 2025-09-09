package com.restaurant.payment.service;

import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Refund;
import com.razorpay.Utils;
import com.restaurant.payment.config.RazorpayConfig;
import com.restaurant.payment.dto.response.RazorpayOrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RazorpayService {

    private final RazorpayClient razorpayClient;
    private final RazorpayConfig razorpayConfig;

    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * Create Razorpay order for payment
     */
    public RazorpayOrderResponse createOrder(Long orderId, BigDecimal amount, String currency,
            String customerEmail, String customerName) throws RazorpayException {
        try {
            // Convert amount to paise (Razorpay uses paise)
            int amountInPaise = amount.multiply(new BigDecimal("100")).intValue();

            // Create order request
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", currency);
            orderRequest.put("receipt", "order_" + orderId + "_" + System.currentTimeMillis());

            // Add notes
            JSONObject notes = new JSONObject();
            notes.put("order_id", orderId);
            notes.put("customer_email", customerEmail);
            notes.put("service", applicationName);
            orderRequest.put("notes", notes);

            // Create order
            Order razorpayOrder = razorpayClient.orders.create(orderRequest);

            log.info("Razorpay order created successfully: {} for order: {}",
                    razorpayOrder.get("id"), orderId);

            // Prepare response for frontend
            RazorpayOrderResponse response = new RazorpayOrderResponse();
            response.setRazorpayOrderId(razorpayOrder.get("id"));
            response.setAmount(String.valueOf(amountInPaise));
            response.setCurrency(currency);
            response.setKey(razorpayConfig.getRazorpayKeyId());
            response.setName("Restaurant Management System");
            response.setDescription("Payment for Order #" + orderId);

            // Prefill customer details
            Map<String, String> prefill = new HashMap<>();
            prefill.put("email", customerEmail);
            if (customerName != null) {
                prefill.put("name", customerName);
            }
            response.setPrefill(prefill);

            // Theme customization
            Map<String, Object> theme = new HashMap<>();
            theme.put("color", "#3399cc");
            response.setTheme(theme);

            // Notes for order tracking
            Map<String, Object> responseNotes = new HashMap<>();
            responseNotes.put("order_id", orderId);
            responseNotes.put("service", applicationName);
            response.setNotes(responseNotes);

            return response;

        } catch (RazorpayException e) {
            log.error("Failed to create Razorpay order for order {}: {}", orderId, e.getMessage());
            throw e;
        }
    }

    /**
     * Verify payment signature
     */
    public boolean verifyPaymentSignature(String razorpayOrderId, String razorpayPaymentId,
            String razorpaySignature) {
        try {
            // Create signature verification data
            String payload = razorpayOrderId + "|" + razorpayPaymentId;

            // Calculate expected signature
            String expectedSignature = calculateHMAC(payload, razorpayConfig.getRazorpayKeySecret());

            // Compare signatures
            boolean isValid = expectedSignature.equals(razorpaySignature);

            log.info("Payment signature verification for payment {}: {}",
                    razorpayPaymentId, isValid ? "SUCCESS" : "FAILED");

            return isValid;

        } catch (Exception e) {
            log.error("Error verifying payment signature for payment {}: {}",
                    razorpayPaymentId, e.getMessage());
            return false;
        }
    }

    /**
     * Get payment details from Razorpay
     */
    public JSONObject getPaymentDetails(String razorpayPaymentId) throws RazorpayException {
        try {
            Payment payment = razorpayClient.payments.fetch(razorpayPaymentId);
            log.info("Retrieved payment details for payment: {}", razorpayPaymentId);
            return payment.toJson();
        } catch (RazorpayException e) {
            log.error("Failed to fetch payment details for payment {}: {}",
                    razorpayPaymentId, e.getMessage());
            throw e;
        }
    }

    /**
     * Verify webhook signature
     */
    public boolean verifyWebhookSignature(String payload, String signature) {
        try {
            String expectedSignature = calculateHMAC(payload, webhookSecret);
            boolean isValid = expectedSignature.equals(signature);

            log.info("Webhook signature verification: {}", isValid ? "SUCCESS" : "FAILED");
            return isValid;

        } catch (Exception e) {
            log.error("Error verifying webhook signature: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Create refund
     */
    public JSONObject createRefund(String razorpayPaymentId, BigDecimal refundAmount) throws RazorpayException {
        try {
            int refundAmountInPaise = refundAmount.multiply(new BigDecimal("100")).intValue();

            JSONObject refundRequest = new JSONObject();
            refundRequest.put("amount", refundAmountInPaise);
            refundRequest.put("speed", "normal");

            JSONObject notes = new JSONObject();
            notes.put("service", applicationName);
            notes.put("reason", "Customer requested refund");
            refundRequest.put("notes", notes);

            // Use the correct API - create refund through RazorpayClient
            Refund refund = razorpayClient.payments.refund(razorpayPaymentId, refundRequest);

            log.info("Refund created successfully: {} for payment: {}",
                    refund.get("id"), razorpayPaymentId);

            return refund.toJson();

        } catch (RazorpayException e) {
            log.error("Failed to create refund for payment {}: {}",
                    razorpayPaymentId, e.getMessage());
            throw e;
        }
    }

    /**
     * Calculate HMAC SHA256 signature
     */
    private String calculateHMAC(String data, String secret) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(data.getBytes());

        // Convert to hex string
        StringBuilder result = new StringBuilder();
        for (byte b : hash) {
            result.append(String.format("%02x", b));
        }

        return result.toString();
    }

    /**
     * Get order details from Razorpay
     */
    public JSONObject getOrderDetails(String razorpayOrderId) throws RazorpayException {
        try {
            Order order = razorpayClient.orders.fetch(razorpayOrderId);
            log.info("Retrieved order details for order: {}", razorpayOrderId);
            return order.toJson();
        } catch (RazorpayException e) {
            log.error("Failed to fetch order details for order {}: {}",
                    razorpayOrderId, e.getMessage());
            throw e;
        }
    }
}
