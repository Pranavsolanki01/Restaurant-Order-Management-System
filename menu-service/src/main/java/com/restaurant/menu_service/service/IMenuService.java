package com.restaurant.menu_service.service;

import com.restaurant.menu_service.dto.MenuItemRequest;
import com.restaurant.menu_service.dto.MenuItemResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface IMenuService {

    // ==================== MENU ITEM CRUD OPERATIONS ====================

    /**
     * Create a new menu item
     * 
     * @param request The menu item request data
     * @param createdBy The user who created the item
     * @return Created menu item response
     */
    MenuItemResponse createMenuItem(MenuItemRequest request, String createdBy);

    /**
     * Update an existing menu item
     * 
     * @param id The menu item ID
     * @param request The updated menu item request data
     * @param updatedBy The user who updated the item
     * @return Updated menu item response
     */
    MenuItemResponse updateMenuItem(String id, MenuItemRequest request, String updatedBy);

    /**
     * Get menu item by ID
     * 
     * @param id The menu item ID
     * @return Optional menu item response
     */
    Optional<MenuItemResponse> getMenuItemById(String id);

    /**
     * Get all menu items
     * 
     * @return List of all menu items
     */
    List<MenuItemResponse> getAllMenuItems();

    /**
     * Delete menu item by ID
     * 
     * @param id The menu item ID
     * @return true if deleted successfully
     */
    boolean deleteMenuItem(String id);

    // ==================== MENU ITEM SEARCH OPERATIONS ====================

    /**
     * Get menu items by availability status
     * 
     * @param isAvailable The availability status
     * @return List of menu items
     */
    List<MenuItemResponse> getMenuItemsByAvailability(Boolean isAvailable);

    /**
     * Get menu items by category
     * 
     * @param category The menu item category
     * @return List of menu items
     */
    List<MenuItemResponse> getMenuItemsByCategory(String category);

    /**
     * Get available menu items by category
     * 
     * @param category The menu item category
     * @return List of available menu items
     */
    List<MenuItemResponse> getAvailableMenuItemsByCategory(String category);

    /**
     * Search menu items by name or description
     * 
     * @param searchTerm The search term
     * @return List of matching menu items
     */
    List<MenuItemResponse> searchMenuItems(String searchTerm);

    /**
     * Get menu items by price range
     * 
     * @param minPrice The minimum price
     * @param maxPrice The maximum price
     * @return List of menu items within price range
     */
    List<MenuItemResponse> getMenuItemsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * Get vegetarian menu items
     * 
     * @return List of vegetarian menu items
     */
    List<MenuItemResponse> getVegetarianMenuItems();

    /**
     * Get vegan menu items
     * 
     * @return List of vegan menu items
     */
    List<MenuItemResponse> getVeganMenuItems();

    /**
     * Get gluten-free menu items
     * 
     * @return List of gluten-free menu items
     */
    List<MenuItemResponse> getGlutenFreeMenuItems();

    /**
     * Get menu items by maximum preparation time
     * 
     * @param maxPreparationTime The maximum preparation time in minutes
     * @return List of menu items
     */
    List<MenuItemResponse> getMenuItemsByMaxPreparationTime(Integer maxPreparationTime);

    /**
     * Get menu items containing specific ingredient
     * 
     * @param ingredient The ingredient to search for
     * @return List of menu items containing the ingredient
     */
    List<MenuItemResponse> getMenuItemsWithIngredient(String ingredient);

    /**
     * Get menu items without specific allergen
     * 
     * @param allergen The allergen to avoid
     * @return List of menu items without the allergen
     */
    List<MenuItemResponse> getMenuItemsWithoutAllergen(String allergen);

    // ==================== MENU ITEM AVAILABILITY OPERATIONS ====================

    /**
     * Update menu item availability
     * 
     * @param id The menu item ID
     * @param isAvailable The new availability status
     * @param updatedBy The user who updated the availability
     * @return Updated menu item response
     */
    MenuItemResponse updateMenuItemAvailability(String id, Boolean isAvailable, String updatedBy);

    // ==================== MENU STATISTICS OPERATIONS ====================

    /**
     * Get count of menu items by category
     * 
     * @param category The menu item category
     * @return Count of menu items
     */
    Long getMenuItemCountByCategory(String category);

    /**
     * Get count of available menu items
     * 
     * @return Count of available menu items
     */
    Long getAvailableMenuItemCount();

    /**
     * Get all menu categories
     * 
     * @return List of all categories
     */
    List<String> getAllCategories();
}