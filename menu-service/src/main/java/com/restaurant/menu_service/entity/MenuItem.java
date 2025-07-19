package com.restaurant.menu_service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "menu_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItem {

    @Id
    private String id;

    @Field("name")
    private String name;

    @Field("description")
    private String description;

    @Field("price")
    private BigDecimal price;

    @Field("category")
    private String category;

    @Field("is_available")
    private Boolean isAvailable;

    @Field("image_url")
    private String imageUrl;

    @Field("ingredients")
    private List<String> ingredients;

    @Field("allergens")
    private List<String> allergens;

    @Field("preparation_time")
    private Integer preparationTimeMinutes;

    @Field("calories")
    private Integer calories;

    @Field("is_vegetarian")
    private Boolean isVegetarian;

    @Field("is_vegan")
    private Boolean isVegan;

    @Field("is_gluten_free")
    private Boolean isGlutenFree;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    @Field("created_by")
    private String createdBy;

    @Field("updated_by")
    private String updatedBy;

    public enum Category {
        APPETIZER,
        MAIN_COURSE,
        DESSERT,
        BEVERAGE,
        SALAD,
        SOUP,
        SIDE_DISH,
        BREAKFAST,
        LUNCH,
        DINNER
    }
}