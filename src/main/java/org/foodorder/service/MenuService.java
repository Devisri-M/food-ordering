package org.foodorder.service;

import java.util.List;
import java.util.Optional;
import org.foodorder.entity.MenuItemEntity;

/**
 * Interface for managing menu items.
 * Provides methods for adding, updating, retrieving, and deleting menu items.
 */
public interface MenuService {

    /**
     * Adds a new menu item to a specific restaurant.
     *
     * @param restaurantId The ID of the restaurant
     * @param menuItem The menu item to be added
     * @return The added menu item
     */
    MenuItemEntity addMenuItem(Long restaurantId, MenuItemEntity menuItem);

    /**
     * Retrieves all menu items for a specific restaurant.
     *
     * @param restaurantId The ID of the restaurant
     * @return A list of menu items for the restaurant
     */
    List<MenuItemEntity> getMenuItemsByRestaurant(Long restaurantId);

    /**
     * Updates an existing menu item for a specific restaurant.
     *
     * @param restaurantId The ID of the restaurant
     * @param menuItemId The ID of the menu item to update
     * @param updatedMenuItem The updated menu item data
     * @return The updated menu item
     */
    MenuItemEntity updateMenuItem(Long restaurantId, Long menuItemId, MenuItemEntity updatedMenuItem);

    /**
     * Deletes a menu item from a specific restaurant.
     *
     * @param restaurantId The ID of the restaurant
     * @param menuItemId The ID of the menu item to delete
     */
    void deleteMenuItem(Long restaurantId, Long menuItemId);

    /**
     * Searches for menu items by name (case insensitive).
     *
     * @param keyword The search keyword
     * @return A list of menu items matching the keyword
     */
    List<MenuItemEntity> searchMenuItems(String keyword);

    /**
     * Finds a specific menu item by its ID.
     *
     * @param menuItemId The ID of the menu item
     * @return An Optional containing the found menu item or empty if not found
     */
    Optional<MenuItemEntity> findMenuItemById(Long menuItemId);
}