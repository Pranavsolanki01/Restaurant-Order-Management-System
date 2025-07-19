package com.restaurant.menu_service.data;

import com.restaurant.menu_service.entity.MenuItem;
import com.restaurant.menu_service.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final MenuItemRepository menuItemRepository;

    @Override
    public void run(String... args) throws Exception {
        if (menuItemRepository.count() == 0) {
            log.info("Loading sample menu data...");
            loadSampleData();
            log.info("Sample menu data loaded successfully!");
        } else {
            log.info("Menu data already exists, skipping data loading.");
        }
    }

    private void loadSampleData() {
        List<MenuItem> sampleItems = Arrays.asList(
            // Appetizers
            MenuItem.builder()
                .name("Caesar Salad")
                .description("Fresh romaine lettuce with parmesan cheese, croutons, and caesar dressing")
                .price(new BigDecimal("12.99"))
                .category("APPETIZER")
                .isAvailable(true)
                .ingredients(Arrays.asList("Romaine lettuce", "Parmesan cheese", "Croutons", "Caesar dressing"))
                .allergens(Arrays.asList("Eggs", "Dairy"))
                .preparationTimeMinutes(10)
                .calories(250)
                .isVegetarian(true)
                .isVegan(false)
                .isGlutenFree(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy("system")
                .updatedBy("system")
                .build(),

            MenuItem.builder()
                .name("Buffalo Wings")
                .description("Spicy chicken wings served with blue cheese dip and celery sticks")
                .price(new BigDecimal("15.99"))
                .category("APPETIZER")
                .isAvailable(true)
                .ingredients(Arrays.asList("Chicken wings", "Buffalo sauce", "Blue cheese", "Celery"))
                .allergens(Arrays.asList("Dairy"))
                .preparationTimeMinutes(20)
                .calories(380)
                .isVegetarian(false)
                .isVegan(false)
                .isGlutenFree(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy("system")
                .updatedBy("system")
                .build(),

            // Main Courses
            MenuItem.builder()
                .name("Grilled Salmon")
                .description("Fresh Atlantic salmon grilled to perfection with lemon herb butter")
                .price(new BigDecimal("24.99"))
                .category("MAIN_COURSE")
                .isAvailable(true)
                .ingredients(Arrays.asList("Atlantic salmon", "Lemon", "Herbs", "Butter"))
                .allergens(Arrays.asList("Fish", "Dairy"))
                .preparationTimeMinutes(25)
                .calories(450)
                .isVegetarian(false)
                .isVegan(false)
                .isGlutenFree(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy("system")
                .updatedBy("system")
                .build(),

            MenuItem.builder()
                .name("Margherita Pizza")
                .description("Classic pizza with tomato sauce, mozzarella cheese, and fresh basil")
                .price(new BigDecimal("18.99"))
                .category("MAIN_COURSE")
                .isAvailable(true)
                .ingredients(Arrays.asList("Pizza dough", "Tomato sauce", "Mozzarella cheese", "Fresh basil"))
                .allergens(Arrays.asList("Gluten", "Dairy"))
                .preparationTimeMinutes(15)
                .calories(520)
                .isVegetarian(true)
                .isVegan(false)
                .isGlutenFree(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy("system")
                .updatedBy("system")
                .build(),

            MenuItem.builder()
                .name("Beef Steak")
                .description("Premium ribeye steak cooked to your preference with garlic mashed potatoes")
                .price(new BigDecimal("29.99"))
                .category("MAIN_COURSE")
                .isAvailable(true)
                .ingredients(Arrays.asList("Ribeye steak", "Potatoes", "Garlic", "Butter", "Herbs"))
                .allergens(Arrays.asList("Dairy"))
                .preparationTimeMinutes(30)
                .calories(650)
                .isVegetarian(false)
                .isVegan(false)
                .isGlutenFree(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy("system")
                .updatedBy("system")
                .build(),

            // Desserts
            MenuItem.builder()
                .name("Chocolate Lava Cake")
                .description("Rich chocolate cake with molten chocolate center, served with vanilla ice cream")
                .price(new BigDecimal("8.99"))
                .category("DESSERT")
                .isAvailable(true)
                .ingredients(Arrays.asList("Dark chocolate", "Butter", "Eggs", "Flour", "Sugar", "Vanilla ice cream"))
                .allergens(Arrays.asList("Gluten", "Dairy", "Eggs"))
                .preparationTimeMinutes(20)
                .calories(480)
                .isVegetarian(true)
                .isVegan(false)
                .isGlutenFree(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy("system")
                .updatedBy("system")
                .build(),

            MenuItem.builder()
                .name("Fresh Fruit Salad")
                .description("Seasonal fresh fruits with honey-lime dressing")
                .price(new BigDecimal("6.99"))
                .category("DESSERT")
                .isAvailable(true)
                .ingredients(Arrays.asList("Mixed seasonal fruits", "Honey", "Lime"))
                .allergens(Arrays.asList())
                .preparationTimeMinutes(5)
                .calories(120)
                .isVegetarian(true)
                .isVegan(true)
                .isGlutenFree(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy("system")
                .updatedBy("system")
                .build(),

            // Beverages
            MenuItem.builder()
                .name("Fresh Orange Juice")
                .description("Freshly squeezed orange juice")
                .price(new BigDecimal("4.99"))
                .category("BEVERAGE")
                .isAvailable(true)
                .ingredients(Arrays.asList("Fresh oranges"))
                .allergens(Arrays.asList())
                .preparationTimeMinutes(2)
                .calories(110)
                .isVegetarian(true)
                .isVegan(true)
                .isGlutenFree(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy("system")
                .updatedBy("system")
                .build(),

            MenuItem.builder()
                .name("Coffee")
                .description("Premium roasted coffee served hot")
                .price(new BigDecimal("3.99"))
                .category("BEVERAGE")
                .isAvailable(true)
                .ingredients(Arrays.asList("Coffee beans", "Water"))
                .allergens(Arrays.asList())
                .preparationTimeMinutes(3)
                .calories(5)
                .isVegetarian(true)
                .isVegan(true)
                .isGlutenFree(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy("system")
                .updatedBy("system")
                .build()
        );

        menuItemRepository.saveAll(sampleItems);
    }
}