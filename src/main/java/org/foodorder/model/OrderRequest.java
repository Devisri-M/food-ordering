package org.foodorder.model;

import java.util.List;

/**
 * for capturing the customer's order request.
 * This class represents the payload structure when a customer places an order.
 * It contains the customer's ID, the list of items to order, and the selection strategy.
 */
public class OrderRequest {

    private Long customerId; // The ID of the customer placing the order
    private List<OrderItemRequest> items; // The list of items and their quantities in the order
    private String strategy; // Strategy for restaurant selection (e.g., "price" or "rating")

    /**
     * Default constructor for deserialization purposes.
     */
    public OrderRequest() {
    }

    /**
     * Parameterized constructor for creating an OrderRequest with customer ID, items, and strategy.
     *
     * @param customerId the ID of the customer
     * @param items the list of items and quantities in the order
     * @param strategy the selection strategy for restaurants (e.g., "price", "rating")
     */
    public OrderRequest(Long customerId, List<OrderItemRequest> items, String strategy) {
        this.customerId = customerId;
        this.items = items;
        this.strategy = strategy;
    }

    /**
     * Gets the customer ID associated with the order.
     *
     * @return the customer ID
     */
    public Long getCustomerId() {
        return customerId;
    }

    /**
     * Sets the customer ID for the order.
     *
     * @param customerId the ID of the customer
     */
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    /**
     * Gets the list of items in the order.
     *
     * @return the list of items and their quantities
     */
    public List<OrderItemRequest> getItems() {
        return items;
    }

    /**
     * Sets the list of items for the order.
     *
     * @param items the list of items and their quantities
     */
    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }

    /**
     * Gets the restaurant selection strategy for the order.
     *
     * @return the strategy (e.g., "price", "rating")
     */
    public String getStrategy() {
        return strategy;
    }

    /**
     * Sets the restaurant selection strategy for the order.
     *
     * @param strategy the selection strategy for restaurants (e.g., "price", "rating")
     */
    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    @Override
    public String toString() {
        return "OrderRequest{" +
                "customerId=" + customerId +
                ", items=" + items +
                ", strategy='" + strategy + '\'' +
                '}';
    }
}