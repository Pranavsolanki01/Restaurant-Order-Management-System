package com.restaurant.menu_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @DecimalMax(value = "9999.99", message = "Price must not exceed 9999.99")
    private BigDecimal price;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Availability status is required")
    private Boolean isAvailable;

    private String imageUrl;

    private List<String> ingredients;

    private List<String> allergens;

    @Min(value = 1, message = "Preparation time must be at least 1 minute")
    @Max(value = 300, message = "Preparation time must not exceed 300 minutes")
    private Integer preparationTimeMinutes;

    @Min(value = 0, message = "Calories cannot be negative")
    @Max(value = 10000, message = "Calories must not exceed 10000")
    private Integer calories;

    private Boolean isVegetarian;

    private Boolean isVegan;

    private Boolean isGlutenFree;
}