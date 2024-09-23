package org.foodorder.service;

import org.foodorder.model.CartItem;

import java.math.BigDecimal;
import java.util.List;

/**
 * Interface for managing shopping cart operations.
 */
public interface CartService {

    /**
     * Adds an item to the cart. If the item already exists (same menuItemId and restaurantId),
     * it increments the quantity.
     *
     * @param menuItemId   the ID of the menu item
     * @param name         the name of the menu item
     * @param price        the price of the menu item
     * @param quantity     the quantity of the item to add
     * @param restaurantId the ID of the restaurant to which the item belongs
     */
    void addToCart(Long menuItemId, String name, BigDecimal price, int quantity, Long restaurantId);

    /**
     * Increments the quantity of a specific item in the cart.
     *
     * @param menuItemId   the ID of the menu item to increment
     * @param restaurantId the ID of the restaurant to which the item belongs
     */
    void incrementQuantity(Long menuItemId, Long restaurantId);

    /**
     * Decrements the quantity of a specific item in the cart.
     * If the quantity reaches zero, the item is removed from the cart.
     *
     * @param menuItemId   the ID of the menu item to decrement
     * @param restaurantId the ID of the restaurant to which the item belongs
     */
    void decrementQuantity(Long menuItemId, Long restaurantId);

    /**
     * Removes a menu item from the cart.
     *
     * @param menuItemId   the ID of the menu item to remove
     * @param restaurantId the ID of the restaurant to which the item belongs
     */
    void removeFromCart(Long menuItemId, Long restaurantId);

    /**
     * Clears all items from the cart.
     */
    void clearCart();

    /**
     * Retrieves the current list of items in the cart.
     *
     * @return a list of CartItem objects
     */
    List<CartItem> getCartItems();

    /**
     * Calculates the total price of all items in the cart.
     *
     * @return the total price as a BigDecimal
     */
    BigDecimal calculateTotal();
}