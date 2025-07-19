package com.restaurant.menu_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemResponse {

    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private Boolean isAvailable;
    private String imageUrl;
    private List<String> ingredients;
    private List<String> allergens;
    private Integer preparationTimeMinutes;
    private Integer calories;
    private Boolean isVegetarian;
    private Boolean isVegan;
    private Boolean isGlutenFree;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}