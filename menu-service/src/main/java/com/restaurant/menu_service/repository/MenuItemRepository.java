package com.restaurant.menu_service.repository;

import com.restaurant.menu_service.entity.MenuItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemRepository extends MongoRepository<MenuItem, String> {

    // Find by availability status
    List<MenuItem> findByIsAvailable(Boolean isAvailable);

    // Find by category
    List<MenuItem> findByCategory(String category);

    // Find by category and availability
    List<MenuItem> findByCategoryAndIsAvailable(String category, Boolean isAvailable);

    // Find by name (case-insensitive)
    Optional<MenuItem> findByNameIgnoreCase(String name);

    // Find items by price range
    List<MenuItem> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    // Find vegetarian items
    List<MenuItem> findByIsVegetarianTrue();

    // Find vegan items
    List<MenuItem> findByIsVeganTrue();

    // Find gluten-free items
    List<MenuItem> findByIsGlutenFreeTrue();

    // Find by preparation time less than or equal to specified minutes
    List<MenuItem> findByPreparationTimeMinutesLessThanEqual(Integer maxPreparationTime);

    // Search items by name or description (case-insensitive)
    @Query("{ $or: [ { 'name': { $regex: ?0, $options: 'i' } }, { 'description': { $regex: ?0, $options: 'i' } } ] }")
    List<MenuItem> searchByNameOrDescription(String searchTerm);

    // Find items containing specific ingredient
    @Query("{ 'ingredients': { $in: [?0] } }")
    List<MenuItem> findByIngredientsContaining(String ingredient);

    // Find items without specific allergen
    @Query("{ 'allergens': { $nin: [?0] } }")
    List<MenuItem> findByAllergensNotContaining(String allergen);

    // Find available items by category with price range
    @Query("{ 'category': ?0, 'isAvailable': true, 'price': { $gte: ?1, $lte: ?2 } }")
    List<MenuItem> findAvailableItemsByCategoryAndPriceRange(String category, BigDecimal minPrice, BigDecimal maxPrice);

    // Count items by category
    Long countByCategory(String category);

    // Count available items
    Long countByIsAvailable(Boolean isAvailable);
}