package com.restaurant.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentHistoryResponse {

    private Long userId;
    private String userEmail;
    private List<PaymentResponse> payments;
    private int totalPayments;
    private int successfulPayments;
    private int failedPayments;
    private LocalDateTime lastPaymentDate;
}
