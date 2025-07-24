package com.restaurant.menu_service.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.restaurant.menu_service.dto.MenuItemRequest;
import com.restaurant.menu_service.dto.MenuItemsResponse;
import com.restaurant.menu_service.entity.MenuItems;
import com.restaurant.menu_service.enums.CategoryEnum;
import com.restaurant.menu_service.mapper.MenuItemRequestToMenuItemsMapper;
import com.restaurant.menu_service.mapper.MenuItemToResponseMapper;
import com.restaurant.menu_service.repository.MenuItemsRepo;
import com.restaurant.menu_service.services.Interface.IMenuService;

@Service
public class MenuService implements IMenuService {

    @Autowired
    private MenuItemsRepo menuItemsRepo;

    @Override
    public List<MenuItemsResponse> getAll() {
        List<MenuItems> items= menuItemsRepo.findAll();
        return MenuItemToResponseMapper.toResponseList(items);
    }

    @Override
    public boolean addItem(MenuItemRequest req) {
        try {
            
            MenuItems item =  MenuItemRequestToMenuItemsMapper.toResponse(req);

            MenuItems savedItem = menuItemsRepo.save(item);

            // Confirm by checking if saved item has a valid ID (Mongo assigns it)
            return savedItem != null && savedItem.getId() != null;
        } catch (Exception e) {
            // Optional: log the error
            System.err.println("Error saving item: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<MenuItemsResponse> getItemByCategory(CategoryEnum type) {
        List<MenuItems> fetched= menuItemsRepo.findByCategoryType(type);
        return MenuItemToResponseMapper.toResponseList(fetched);
    }
}
