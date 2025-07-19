package com.restaurant.menu_service.service.impl;

import com.restaurant.menu_service.dto.MenuItemRequest;
import com.restaurant.menu_service.dto.MenuItemResponse;
import com.restaurant.menu_service.entity.MenuItem;
import com.restaurant.menu_service.repository.MenuItemRepository;
import com.restaurant.menu_service.service.IMenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuServiceImpl implements IMenuService {

    private final MenuItemRepository menuItemRepository;

    @Override
    @CacheEvict(value = {"menuItems", "availableMenuItems", "menuCategories"}, allEntries = true)
    public MenuItemResponse createMenuItem(MenuItemRequest request, String createdBy) {
        log.info("Creating new menu item with name: {}", request.getName());
        
        // Check if menu item with the same name already exists
        Optional<MenuItem> existingItem = menuItemRepository.findByNameIgnoreCase(request.getName());
        if (existingItem.isPresent()) {
            throw new RuntimeException("Menu item with name '" + request.getName() + "' already exists");
        }

        MenuItem menuItem = MenuItem.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .isAvailable(request.getIsAvailable())
                .imageUrl(request.getImageUrl())
                .ingredients(request.getIngredients())
                .allergens(request.getAllergens())
                .preparationTimeMinutes(request.getPreparationTimeMinutes())
                .calories(request.getCalories())
                .isVegetarian(request.getIsVegetarian() != null ? request.getIsVegetarian() : false)
                .isVegan(request.getIsVegan() != null ? request.getIsVegan() : false)
                .isGlutenFree(request.getIsGlutenFree() != null ? request.getIsGlutenFree() : false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(createdBy)
                .updatedBy(createdBy)
                .build();

        MenuItem savedItem = menuItemRepository.save(menuItem);
        log.info("Menu item created successfully with ID: {}", savedItem.getId());
        
        return mapToResponse(savedItem);
    }

    @Override
    @CacheEvict(value = {"menuItems", "availableMenuItems", "menuCategories"}, allEntries = true)
    public MenuItemResponse updateMenuItem(String id, MenuItemRequest request, String updatedBy) {
        log.info("Updating menu item with ID: {}", id);
        
        MenuItem existingItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found with ID: " + id));

        // Check if another item with the same name exists (excluding current item)
        Optional<MenuItem> itemWithSameName = menuItemRepository.findByNameIgnoreCase(request.getName());
        if (itemWithSameName.isPresent() && !itemWithSameName.get().getId().equals(id)) {
            throw new RuntimeException("Another menu item with name '" + request.getName() + "' already exists");
        }

        existingItem.setName(request.getName());
        existingItem.setDescription(request.getDescription());
        existingItem.setPrice(request.getPrice());
        existingItem.setCategory(request.getCategory());
        existingItem.setIsAvailable(request.getIsAvailable());
        existingItem.setImageUrl(request.getImageUrl());
        existingItem.setIngredients(request.getIngredients());
        existingItem.setAllergens(request.getAllergens());
        existingItem.setPreparationTimeMinutes(request.getPreparationTimeMinutes());
        existingItem.setCalories(request.getCalories());
        existingItem.setIsVegetarian(request.getIsVegetarian() != null ? request.getIsVegetarian() : false);
        existingItem.setIsVegan(request.getIsVegan() != null ? request.getIsVegan() : false);
        existingItem.setIsGlutenFree(request.getIsGlutenFree() != null ? request.getIsGlutenFree() : false);
        existingItem.setUpdatedAt(LocalDateTime.now());
        existingItem.setUpdatedBy(updatedBy);

        MenuItem updatedItem = menuItemRepository.save(existingItem);
        log.info("Menu item updated successfully with ID: {}", updatedItem.getId());
        
        return mapToResponse(updatedItem);
    }

    @Override
    @Cacheable(value = "menuItems", key = "#id")
    public Optional<MenuItemResponse> getMenuItemById(String id) {
        log.info("Fetching menu item with ID: {}", id);
        return menuItemRepository.findById(id).map(this::mapToResponse);
    }

    @Override
    @Cacheable(value = "menuItems", key = "'all'")
    public List<MenuItemResponse> getAllMenuItems() {
        log.info("Fetching all menu items");
        return menuItemRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = {"menuItems", "availableMenuItems", "menuCategories"}, allEntries = true)
    public boolean deleteMenuItem(String id) {
        log.info("Deleting menu item with ID: {}", id);
        
        if (!menuItemRepository.existsById(id)) {
            throw new RuntimeException("Menu item not found with ID: " + id);
        }
        
        menuItemRepository.deleteById(id);
        log.info("Menu item deleted successfully with ID: {}", id);
        return true;
    }

    @Override
    @Cacheable(value = "availableMenuItems", key = "#isAvailable")
    public List<MenuItemResponse> getMenuItemsByAvailability(Boolean isAvailable) {
        log.info("Fetching menu items by availability: {}", isAvailable);
        return menuItemRepository.findByIsAvailable(isAvailable)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "menuCategories", key = "#category")
    public List<MenuItemResponse> getMenuItemsByCategory(String category) {
        log.info("Fetching menu items by category: {}", category);
        return menuItemRepository.findByCategory(category)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "availableMenuItems", key = "'category_' + #category")
    public List<MenuItemResponse> getAvailableMenuItemsByCategory(String category) {
        log.info("Fetching available menu items by category: {}", category);
        return menuItemRepository.findByCategoryAndIsAvailable(category, true)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuItemResponse> searchMenuItems(String searchTerm) {
        log.info("Searching menu items with term: {}", searchTerm);
        return menuItemRepository.searchByNameOrDescription(searchTerm)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuItemResponse> getMenuItemsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        log.info("Fetching menu items by price range: {} - {}", minPrice, maxPrice);
        return menuItemRepository.findByPriceBetween(minPrice, maxPrice)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "vegetarianMenuItems")
    public List<MenuItemResponse> getVegetarianMenuItems() {
        log.info("Fetching vegetarian menu items");
        return menuItemRepository.findByIsVegetarianTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "veganMenuItems")
    public List<MenuItemResponse> getVeganMenuItems() {
        log.info("Fetching vegan menu items");
        return menuItemRepository.findByIsVeganTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "glutenFreeMenuItems")
    public List<MenuItemResponse> getGlutenFreeMenuItems() {
        log.info("Fetching gluten-free menu items");
        return menuItemRepository.findByIsGlutenFreeTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuItemResponse> getMenuItemsByMaxPreparationTime(Integer maxPreparationTime) {
        log.info("Fetching menu items with max preparation time: {} minutes", maxPreparationTime);
        return menuItemRepository.findByPreparationTimeMinutesLessThanEqual(maxPreparationTime)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuItemResponse> getMenuItemsWithIngredient(String ingredient) {
        log.info("Fetching menu items containing ingredient: {}", ingredient);
        return menuItemRepository.findByIngredientsContaining(ingredient)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuItemResponse> getMenuItemsWithoutAllergen(String allergen) {
        log.info("Fetching menu items without allergen: {}", allergen);
        return menuItemRepository.findByAllergensNotContaining(allergen)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = {"menuItems", "availableMenuItems"}, allEntries = true)
    public MenuItemResponse updateMenuItemAvailability(String id, Boolean isAvailable, String updatedBy) {
        log.info("Updating availability for menu item ID: {} to {}", id, isAvailable);
        
        MenuItem existingItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found with ID: " + id));

        existingItem.setIsAvailable(isAvailable);
        existingItem.setUpdatedAt(LocalDateTime.now());
        existingItem.setUpdatedBy(updatedBy);

        MenuItem updatedItem = menuItemRepository.save(existingItem);
        log.info("Menu item availability updated successfully");
        
        return mapToResponse(updatedItem);
    }

    @Override
    public Long getMenuItemCountByCategory(String category) {
        log.info("Getting count of menu items by category: {}", category);
        return menuItemRepository.countByCategory(category);
    }

    @Override
    public Long getAvailableMenuItemCount() {
        log.info("Getting count of available menu items");
        return menuItemRepository.countByIsAvailable(true);
    }

    @Override
    @Cacheable(value = "allCategories")
    public List<String> getAllCategories() {
        log.info("Fetching all menu categories");
        return menuItemRepository.findAll()
                .stream()
                .map(MenuItem::getCategory)
                .distinct()
                .collect(Collectors.toList());
    }

    private MenuItemResponse mapToResponse(MenuItem menuItem) {
        return MenuItemResponse.builder()
                .id(menuItem.getId())
                .name(menuItem.getName())
                .description(menuItem.getDescription())
                .price(menuItem.getPrice())
                .category(menuItem.getCategory())
                .isAvailable(menuItem.getIsAvailable())
                .imageUrl(menuItem.getImageUrl())
                .ingredients(menuItem.getIngredients())
                .allergens(menuItem.getAllergens())
                .preparationTimeMinutes(menuItem.getPreparationTimeMinutes())
                .calories(menuItem.getCalories())
                .isVegetarian(menuItem.getIsVegetarian())
                .isVegan(menuItem.getIsVegan())
                .isGlutenFree(menuItem.getIsGlutenFree())
                .createdAt(menuItem.getCreatedAt())
                .updatedAt(menuItem.getUpdatedAt())
                .createdBy(menuItem.getCreatedBy())
                .updatedBy(menuItem.getUpdatedBy())
                .build();
    }
}