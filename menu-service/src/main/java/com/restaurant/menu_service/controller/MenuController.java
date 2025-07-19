package com.restaurant.menu_service.controller;

import com.restaurant.menu_service.dto.ErrorResponse;
import com.restaurant.menu_service.dto.MenuItemRequest;
import com.restaurant.menu_service.dto.MenuItemResponse;
import com.restaurant.menu_service.service.IMenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class MenuController {

    private final IMenuService menuService;

    // ==================== MENU ITEM CRUD OPERATIONS ====================

    @PostMapping
    public ResponseEntity<?> createMenuItem(
            @Valid @RequestBody MenuItemRequest request,
            @RequestParam(defaultValue = "system") String createdBy) {
        try {
            MenuItemResponse response = menuService.createMenuItem(request, createdBy);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            log.error("Error creating menu item: {}", e.getMessage());
            ErrorResponse error = new ErrorResponse("CREATION_ERROR", e.getMessage(), HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMenuItem(
            @PathVariable String id,
            @Valid @RequestBody MenuItemRequest request,
            @RequestParam(defaultValue = "system") String updatedBy) {
        try {
            MenuItemResponse response = menuService.updateMenuItem(id, request, updatedBy);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error updating menu item: {}", e.getMessage());
            ErrorResponse error = new ErrorResponse("UPDATE_ERROR", e.getMessage(), HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMenuItemById(@PathVariable String id) {
        try {
            Optional<MenuItemResponse> response = menuService.getMenuItemById(id);
            if (response.isPresent()) {
                return ResponseEntity.ok(response.get());
            } else {
                ErrorResponse error = new ErrorResponse("NOT_FOUND", "Menu item not found with ID: " + id, HttpStatus.NOT_FOUND.value());
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error fetching menu item: {}", e.getMessage());
            ErrorResponse error = new ErrorResponse("FETCH_ERROR", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllMenuItems() {
        try {
            List<MenuItemResponse> response = menuService.getAllMenuItems();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching all menu items: {}", e.getMessage());
            ErrorResponse error = new ErrorResponse("FETCH_ERROR", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMenuItem(@PathVariable String id) {
        try {
            boolean deleted = menuService.deleteMenuItem(id);
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Menu item deleted successfully", "id", id));
            } else {
                ErrorResponse error = new ErrorResponse("DELETE_ERROR", "Failed to delete menu item", HttpStatus.INTERNAL_SERVER_ERROR.value());
                return ResponseEntity.internalServerError().body(error);
            }
        } catch (RuntimeException e) {
            log.error("Error deleting menu item: {}", e.getMessage());
            ErrorResponse error = new ErrorResponse("DELETE_ERROR", e.getMessage(), HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // ==================== MENU ITEM SEARCH OPERATIONS ====================

    @GetMapping("/available")
    public ResponseEntity<?> getAvailableMenuItems() {
        try {
            List<MenuItemResponse> response = menuService.getMenuItemsByAvailability(true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching available menu items: {}", e.getMessage());
            ErrorResponse error = new ErrorResponse("FETCH_ERROR", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/unavailable")
    public ResponseEntity<?> getUnavailableMenuItems() {
        try {
            List<MenuItemResponse> response = menuService.getMenuItemsByAvailability(false);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching unavailable menu items: {}", e.getMessage());
            ErrorResponse error = new ErrorResponse("FETCH_ERROR", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> getMenuItemsByCategory(@PathVariable String category) {
        try {
            List<MenuItemResponse> response = menuService.getMenuItemsByCategory(category);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching menu items by category: {}", e.getMessage());
            ErrorResponse error = new ErrorResponse("FETCH_ERROR", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/category/{category}/available")
    public ResponseEntity<?> getAvailableMenuItemsByCategory(@PathVariable String category) {
        try {
            List<MenuItemResponse> response = menuService.getAvailableMenuItemsByCategory(category);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching available menu items by category: {}", e.getMessage());
            ErrorResponse error = new ErrorResponse("FETCH_ERROR", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchMenuItems(@RequestParam String query) {
        try {
            List<MenuItemResponse> response = menuService.searchMenuItems(query);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error searching menu items: {}", e.getMessage());
            ErrorResponse error = new ErrorResponse("SEARCH_ERROR", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/price-range")
    public ResponseEntity<?> getMenuItemsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        try {
            List<MenuItemResponse> response = menuService.getMenuItemsByPriceRange(minPrice, maxPrice);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching menu items by price range: {}", e.getMessage());
            ErrorResponse error = new ErrorResponse("FETCH_ERROR", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/vegetarian")
    public ResponseEntity<?> getVegetarianMenuItems() {
        try {
            List<MenuItemResponse> response = menuService.getVegetarianMenuItems();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching vegetarian menu items: {}", e.getMessage());
            ErrorResponse error = new ErrorResponse("FETCH_ERROR", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/vegan")
    public ResponseEntity<?> getVeganMenuItems() {
        try {
            List<MenuItemResponse> response = menuService.getVeganMenuItems();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching vegan menu items: {}", e.getMessage());
            ErrorResponse error = new ErrorResponse("FETCH_ERROR", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/gluten-free")
    public ResponseEntity<?> getGlutenFreeMenuItems() {
        try {
            List<MenuItemResponse> response = menuService.getGlutenFreeMenuItems();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching gluten-free menu items: {}", e.getMessage());
            ErrorResponse error = new ErrorResponse("FETCH_ERROR", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/quick-prep")
    public ResponseEntity<?> getQuickPrepMenuItems(@RequestParam Integer maxMinutes) {
        try {
            List<MenuItemResponse> response = menuService.getMenuItemsByMaxPreparationTime(maxMinutes);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching quick prep menu items: {}", e.getMessage());
            ErrorResponse error = new ErrorResponse("FETCH_ERROR", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/with-ingredient")
    public ResponseEntity<?> getMenuItemsWithIngredient(@RequestParam String ingredient) {
        try {
            List<MenuItemResponse> response = menuService.getMenuItemsWithIngredient(ingredient);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching menu items with ingredient: {}", e.getMessage());
            ErrorResponse error = new ErrorResponse("FETCH_ERROR", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/without-allergen")
    public ResponseEntity<?> getMenuItemsWithoutAllergen(@RequestParam String allergen) {
        try {
            List<MenuItemResponse> response = menuService.getMenuItemsWithoutAllergen(allergen);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching menu items without allergen: {}", e.getMessage());
            ErrorResponse error = new ErrorResponse("FETCH_ERROR", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // ==================== MENU ITEM AVAILABILITY OPERATIONS ====================

    @PatchMapping("/{id}/availability")
    public ResponseEntity<?> updateMenuItemAvailability(
            @PathVariable String id,
            @RequestParam Boolean isAvailable,
            @RequestParam(defaultValue = "system") String updatedBy) {
        try {
            MenuItemResponse response = menuService.updateMenuItemAvailability(id, isAvailable, updatedBy);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error updating menu item availability: {}", e.getMessage());
            ErrorResponse error = new ErrorResponse("UPDATE_ERROR", e.getMessage(), HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // ==================== MENU STATISTICS OPERATIONS ====================

    @GetMapping("/stats/category-count")
    public ResponseEntity<?> getMenuItemCountByCategory(@RequestParam String category) {
        try {
            Long count = menuService.getMenuItemCountByCategory(category);
            return ResponseEntity.ok(Map.of("category", category, "count", count));
        } catch (Exception e) {
            log.error("Error getting menu item count by category: {}", e.getMessage());
            ErrorResponse error = new ErrorResponse("STATS_ERROR", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/stats/available-count")
    public ResponseEntity<?> getAvailableMenuItemCount() {
        try {
            Long count = menuService.getAvailableMenuItemCount();
            return ResponseEntity.ok(Map.of("availableCount", count));
        } catch (Exception e) {
            log.error("Error getting available menu item count: {}", e.getMessage());
            ErrorResponse error = new ErrorResponse("STATS_ERROR", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getAllCategories() {
        try {
            List<String> categories = menuService.getAllCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            log.error("Error fetching all categories: {}", e.getMessage());
            ErrorResponse error = new ErrorResponse("FETCH_ERROR", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // ==================== HEALTH CHECK ====================

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "menu-service",
                "timestamp", System.currentTimeMillis()
        ));
    }
}