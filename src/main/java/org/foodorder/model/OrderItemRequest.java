package org.foodorder.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * Model class representing an item in a customer's order request.
 * This class is designed for use in the new API where the customer provides only item name, price, and quantity.
 * The system will later determine the appropriate restaurant for each item.
 */
public class OrderItemRequest {

    private static final Logger logger = LoggerFactory.getLogger(OrderItemRequest.class);

    private String name;      // Name of the menu item
    private BigDecimal price; // Price of the menu item
    private int quantity;     // Quantity of the item in the order
    private Long menuItemId; // ID of the menu item

    /**
     * Constructor to initialize the order item request.
     * Validates that the price and quantity are valid values.
     *
     * @param name the name of the menu item
     * @param price the price of the menu item
     * @param quantity the quantity of the menu item
     */
    public OrderItemRequest(String name, BigDecimal price, int quantity) {
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            logger.error("Invalid price: {}", price);
            throw new IllegalArgumentException("Price must be non-negative.");
        }
        if (quantity < 1) {
            logger.error("Invalid quantity: {}", quantity);
            throw new IllegalArgumentException("Quantity must be at least 1.");
        }
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        logger.info("Created order item request: {}", this);
    }

    // Getter and Setter methods

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

    /**
     * Method to calculate the total price for this item (price * quantity).
     *
     * @return the total price as a BigDecimal
     */
    public BigDecimal getTotalPrice() {
        try {
            BigDecimal totalPrice = price.multiply(BigDecimal.valueOf(quantity));
            logger.info("Total price for order item {}: {}", name, totalPrice);
            return totalPrice;
        } catch (Exception e) {
            logger.error("Error calculating total price for order item {}: {}", name, e.getMessage());
            throw new RuntimeException("Failed to calculate total price.", e);
        }
    }

    public Long getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(Long menuItemId) {
        this.menuItemId = menuItemId;
    }

    // ToString method to display order item request details
    @Override
    public String toString() {
        return "OrderItemRequest{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}