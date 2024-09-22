package org.foodorder.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * Model class representing a cart item.
 * A cart item consists of a menu item selected by the user, along with its price, name, quantity, and associated restaurant.
 */
public class CartItem {

    private static final Logger logger = LoggerFactory.getLogger(CartItem.class);

    private Long menuItemId;  // ID of the menu item
    private String name;      // Name of the menu item
    private BigDecimal price; // Price of the menu item
    private int quantity;     // Quantity of the item in the cart
    private Long restaurantId; // ID of the restaurant to which this menu item belongs

    /**
     * Constructor to initialize the cart item.
     * Validates that the price and quantity are valid values.
     *
     * @param menuItemId the ID of the menu item
     * @param name the name of the menu item
     * @param price the price of the menu item
     * @param quantity the quantity of the menu item
     * @param restaurantId the ID of the restaurant associated with this item
     */
    public CartItem(Long menuItemId, String name, BigDecimal price, int quantity, Long restaurantId) {
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            logger.error("Invalid price: {}", price);
            throw new IllegalArgumentException("Price must be non-negative.");
        }
        if (quantity < 1) {
            logger.error("Invalid quantity: {}", quantity);
            throw new IllegalArgumentException("Quantity must be at least 1.");
        }
        if (restaurantId == null || restaurantId <= 0) {
            logger.error("Invalid restaurant ID: {}", restaurantId);
            throw new IllegalArgumentException("Restaurant ID must be valid.");
        }

        this.menuItemId = menuItemId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.restaurantId = restaurantId;

        logger.info("Created cart item: {}", this);
    }

    // Getter and Setter methods

    public Long getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(Long menuItemId) {
        if (menuItemId == null || menuItemId <= 0) {
            logger.error("Invalid menu item ID: {}", menuItemId);
            throw new IllegalArgumentException("Menu item ID must be a positive number.");
        }
        this.menuItemId = menuItemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            logger.error("Invalid name: {}", name);
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            logger.error("Invalid price: {}", price);
            throw new IllegalArgumentException("Price must be non-negative.");
        }
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 1) {
            logger.error("Invalid quantity: {}", quantity);
            throw new IllegalArgumentException("Quantity must be at least 1.");
        }
        this.quantity = quantity;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        if (restaurantId == null || restaurantId <= 0) {
            logger.error("Invalid restaurant ID: {}", restaurantId);
            throw new IllegalArgumentException("Restaurant ID must be valid.");
        }
        this.restaurantId = restaurantId;
    }

    /**
     * Method to calculate the total price for this cart item (price * quantity).
     *
     * @return the total price as a BigDecimal
     */
    public BigDecimal getTotalPrice() {
        try {
            BigDecimal totalPrice = price.multiply(BigDecimal.valueOf(quantity));
            logger.info("Total price for cart item {}: {}", menuItemId, totalPrice);
            return totalPrice;
        } catch (Exception e) {
            logger.error("Error calculating total price for cart item ID {}: {}", menuItemId, e.getMessage());
            throw new RuntimeException("Failed to calculate total price.", e);
        }
    }

    // ToString method to display cart item details
    @Override
    public String toString() {
        return "CartItem{" +
                "menuItemId=" + menuItemId +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", restaurantId=" + restaurantId +
                '}';
    }
}