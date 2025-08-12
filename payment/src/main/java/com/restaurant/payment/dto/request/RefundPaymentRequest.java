package com.restaurant.payment.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundPaymentRequest {

    @NotNull(message = "Payment ID is required")
    @Positive(message = "Payment ID must be positive")
    private Long paymentId;

    @DecimalMin(value = "1.0", message = "Refund amount must be at least 1.0")
    private BigDecimal refundAmount; // If null, full refund

    @Size(max = 500, message = "Reason cannot exceed 500 characters")
    private String reason;

    @NotBlank(message = "Refund type is required")
    @Pattern(regexp = "FULL|PARTIAL", message = "Refund type must be FULL or PARTIAL")
    private String refundType;
}
