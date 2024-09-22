package org.foodorder.model;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class Cart {

    private final List<CartItem> items = new ArrayList<>();

    // Add item to the cart
    public void addItem(CartItem item) {
        items.add(item);
    }

    // Remove item from the cart
    public void removeItem(Long menuItemId) {
        items.removeIf(item -> item.getMenuItemId().equals(menuItemId));
    }

    // Clear the entire cart
    public void clear() {
        items.clear();
    }

    // Get the list of items in the cart
    public List<CartItem> getItems() {
        return items;
    }

    // Calculate the total price of items in the cart
    public BigDecimal getTotalPrice() {
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}