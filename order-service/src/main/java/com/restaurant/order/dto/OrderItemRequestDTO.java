package com.restaurant.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequestDTO {

    @NotNull(message = "Menu item ID is required")
    @Positive(message = "Menu item ID must be positive")
    private Long menuItemId;

    @NotBlank(message = "Menu item name is required")
    private String menuItemName;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Unit price must have maximum 8 integer digits and 2 decimal places")
    private BigDecimal unitPrice;

    @Size(max = 500, message = "Special requests cannot exceed 500 characters")
    private String specialRequests;
}
