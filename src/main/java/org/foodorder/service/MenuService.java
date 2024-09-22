package org.foodorder.service;

import java.util.List;
import java.util.Optional;
import org.foodorder.entity.MenuItemEntity;
import org.foodorder.repository.MenuItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MenuService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(MenuService.class);

    /**
     * Add a new menu item to a restaurant.
     *
     * @param restaurantId The ID of the restaurant
     * @param menuItem The menu item to be added
     * @return The added menu item
     */
    public MenuItemEntity addMenuItem(Long restaurantId, MenuItemEntity menuItem) {
        try {
            menuItem.setRestaurantId(restaurantId);
            LOGGER.info("Adding new menu item to restaurant ID: {}", restaurantId);
            return menuItemRepository.save(menuItem);
        } catch (Exception e) {
            LOGGER.error("Error adding menu item to restaurant ID: {}", restaurantId, e);
            throw new RuntimeException("Failed to add menu item", e);
        }
    }

    /**
     * Retrieve all menu items for a specific restaurant.
     *
     * @param restaurantId The ID of the restaurant
     * @return List of menu items
     */
    public List<MenuItemEntity> getMenuItemsByRestaurant(Long restaurantId) {
        try {
            LOGGER.info("Fetching menu items for restaurant ID: {}", restaurantId);
            return menuItemRepository.findByRestaurantId(restaurantId);
        } catch (Exception e) {
            LOGGER.error("Error fetching menu items for restaurant ID: {}", restaurantId, e);
            throw new RuntimeException("Failed to fetch menu items", e);
        }
    }

    /**
     * Update an existing menu item.
     *
     * @param restaurantId The ID of the restaurant
     * @param menuItemId The ID of the menu item to update
     * @param updatedMenuItem The updated menu item data
     * @return The updated menu item
     */
    public MenuItemEntity updateMenuItem(Long restaurantId, Long menuItemId, MenuItemEntity updatedMenuItem) {
        try {
            MenuItemEntity menuItem = menuItemRepository.findById(menuItemId)
                    .orElseThrow(() -> new RuntimeException("Menu item not found"));

            if (!menuItem.getRestaurantId().equals(restaurantId)) {
                throw new RuntimeException("Restaurant ID mismatch");
            }

            menuItem.setName(updatedMenuItem.getName());
            menuItem.setDescription(updatedMenuItem.getDescription());
            menuItem.setPrice(updatedMenuItem.getPrice());
            menuItem.setAvailable(updatedMenuItem.getAvailable());

            LOGGER.info("Updating menu item ID: {} for restaurant ID: {}", menuItemId, restaurantId);
            return menuItemRepository.save(menuItem);
        } catch (Exception e) {
            LOGGER.error("Error updating menu item ID: {} for restaurant ID: {}", menuItemId, restaurantId, e);
            throw new RuntimeException("Failed to update menu item", e);
        }
    }

    /**
     * Delete a menu item from a restaurant.
     *
     * @param restaurantId The ID of the restaurant
     * @param menuItemId The ID of the menu item to delete
     */
    public void deleteMenuItem(Long restaurantId, Long menuItemId) {
        try {
            MenuItemEntity menuItem = menuItemRepository.findById(menuItemId)
                    .orElseThrow(() -> new RuntimeException("Menu item not found"));

            if (!menuItem.getRestaurantId().equals(restaurantId)) {
                throw new RuntimeException("Restaurant ID mismatch");
            }

            LOGGER.info("Deleting menu item ID: {} from restaurant ID: {}", menuItemId, restaurantId);
            menuItemRepository.deleteById(menuItemId);
        } catch (Exception e) {
            LOGGER.error("Error deleting menu item ID: {} from restaurant ID: {}", menuItemId, restaurantId, e);
            throw new RuntimeException("Failed to delete menu item", e);
        }
    }

    /**
     * Search for menu items by name (case insensitive).
     *
     * @param keyword The search keyword
     * @return List of menu items matching the keyword
     */
    public List<MenuItemEntity> searchMenuItems(String keyword) {
        try {
            LOGGER.info("Searching for menu items by keyword: {}", keyword);
            return menuItemRepository.findByNameContainingIgnoreCase(keyword);
        } catch (Exception e) {
            LOGGER.error("Error searching menu items by keyword: {}", keyword, e);
            throw new RuntimeException("Failed to search menu items", e);
        }
    }

    /**
     * Find a specific menu item by its ID.
     *
     * @param menuItemId The ID of the menu item
     * @return Optional containing the found menu item or empty if not found
     */
    public Optional<MenuItemEntity> findMenuItemById(Long menuItemId) {
        try {
            LOGGER.info("Fetching menu item by ID: {}", menuItemId);
            return menuItemRepository.findById(menuItemId);
        } catch (Exception e) {
            LOGGER.error("Error fetching menu item by ID: {}", menuItemId, e);
            throw new RuntimeException("Failed to fetch menu item", e);
        }
    }

    /**
     * Get all menu items for a specific restaurant.
     *
     * @param restaurantId the restaurant ID
     * @return list of menu items
     */
    public List<MenuItemEntity> getMenuItemsByRestaurantCard(Long restaurantId) {
        try {
            LOGGER.info("Fetching menu items for restaurant ID {}", restaurantId);
            return menuItemRepository.findByRestaurantId(restaurantId);
        } catch (Exception e) {
            LOGGER.error("Error while fetching menu items for restaurant ID {}", restaurantId, e);
            throw new RuntimeException("Error fetching menu items", e);
        }
    }
}