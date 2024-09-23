package org.foodorder.service;

import org.foodorder.model.CartItem;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service class for managing the shopping cart.
 * The cart is session-scoped, meaning it persists for the duration of a user session.
 */
@Service
@SessionScope // The cart is session-based
public class CartServiceImpl implements CartService {

    private final List<CartItem> cartItems = new ArrayList<>();  // List of cart items
    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);  // Logger for logging operations

    /**
     * Adds an item to the cart. If the item already exists (same menuItemId and restaurantId), it increments the quantity.
     *
     * @param menuItemId  the ID of the menu item
     * @param name        the name of the menu item
     * @param price       the price of the menu item
     * @param quantity    the quantity of the item to add
     * @param restaurantId the ID of the restaurant to which the item belongs
     */
    @Override
    public void addToCart(Long menuItemId, String name, BigDecimal price, int quantity, Long restaurantId) {
        try {
            for (CartItem item : cartItems) {
                if (item.getMenuItemId().equals(menuItemId) && item.getRestaurantId().equals(restaurantId)) {
                    item.setQuantity(item.getQuantity() + quantity); // Increment if already present
                    logger.info("Incremented quantity for item {} with ID {} from restaurant ID {}", name, menuItemId, restaurantId);
                    return;
                }
            }
            // Add new item to the cart if not found
            CartItem newItem = new CartItem(menuItemId, name, price, quantity, restaurantId);
            cartItems.add(newItem);
            logger.info("Added new item {} with ID {} to the cart from restaurant ID {}", name, menuItemId, restaurantId);
        } catch (Exception e) {
            logger.error("Error adding item with ID {} from restaurant ID {} to the cart", menuItemId, restaurantId, e);
            throw new RuntimeException("Failed to add item to cart");
        }
    }

    /**
     * Increments the quantity of a specific item in the cart.
     *
     * @param menuItemId the ID of the menu item to increment
     * @param restaurantId the ID of the restaurant to which the item belongs
     */
    @Override
    public void incrementQuantity(Long menuItemId, Long restaurantId) {
        try {
            for (CartItem item : cartItems) {
                if (item.getMenuItemId().equals(menuItemId) && item.getRestaurantId().equals(restaurantId)) {
                    item.setQuantity(item.getQuantity() + 1);
                    logger.info("Incremented quantity for item with ID {} from restaurant ID {}", menuItemId, restaurantId);
                    return;
                }
            }
        } catch (Exception e) {
            logger.error("Error incrementing quantity for item with ID {} from restaurant ID {}", menuItemId, restaurantId, e);
            throw new RuntimeException("Failed to increment item quantity");
        }
    }

    /**
     * Decrements the quantity of a specific item in the cart.
     * If the quantity reaches zero, the item is removed from the cart.
     *
     * @param menuItemId the ID of the menu item to decrement
     * @param restaurantId the ID of the restaurant to which the item belongs
     */
    @Override
    public void decrementQuantity(Long menuItemId, Long restaurantId) {
        try {
            for (CartItem item : cartItems) {
                if (item.getMenuItemId().equals(menuItemId) && item.getRestaurantId().equals(restaurantId)) {
                    if (item.getQuantity() > 1) {
                        item.setQuantity(item.getQuantity() - 1);
                        logger.info("Decremented quantity for item with ID {} from restaurant ID {}", menuItemId, restaurantId);
                    } else {
                        cartItems.remove(item);
                        logger.info("Removed item with ID {} from restaurant ID {} from the cart", menuItemId, restaurantId);
                    }
                    return;
                }
            }
        } catch (Exception e) {
            logger.error("Error decrementing quantity for item with ID {} from restaurant ID {}", menuItemId, restaurantId, e);
            throw new RuntimeException("Failed to decrement item quantity");
        }
    }

    /**
     * Removes a menu item from the cart.
     *
     * @param menuItemId the ID of the menu item to remove
     * @param restaurantId the ID of the restaurant to which the item belongs
     */
    @Override
    public void removeFromCart(Long menuItemId, Long restaurantId) {
        try {
            boolean removed = cartItems.removeIf(item -> item.getMenuItemId().equals(menuItemId) && item.getRestaurantId().equals(restaurantId));
            if (!removed) {
                logger.error("Failed to remove item with ID {} from restaurant ID {} from the cart", menuItemId, restaurantId);
                throw new RuntimeException("Item not found in the cart");
            }
            logger.info("Item with ID {} from restaurant ID {} removed from the cart", menuItemId, restaurantId);
        } catch (Exception e) {
            logger.error("Error removing item with ID {} from restaurant ID {} from the cart", menuItemId, restaurantId, e);
            throw new RuntimeException("Failed to remove item from cart");
        }
    }

    /**
     * Clears all items from the cart.
     */
    @Override
    public void clearCart() {
        try {
            cartItems.clear();
            logger.info("Cart cleared successfully");
        } catch (Exception e) {
            logger.error("Error clearing the cart", e);
            throw new RuntimeException("Failed to clear cart");
        }
    }

    /**
     * Retrieves the current list of items in the cart.
     *
     * @return a list of CartItem objects
     */
    @Override
    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    /**
     * Calculates the total price of all items in the cart.
     *
     * @return the total price as a BigDecimal
     */
    @Override
    public BigDecimal calculateTotal() {
        try {
            BigDecimal total = cartItems.stream()
                    .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            logger.info("Calculated total cart value: {}", total);
            return total;
        } catch (Exception e) {
            logger.error("Error calculating total cart value", e);
            throw new RuntimeException("Failed to calculate total cart value");
        }
    }
}