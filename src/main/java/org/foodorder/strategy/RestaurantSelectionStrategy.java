package org.foodorder.strategy;

import org.foodorder.entity.RestaurantEntity;
import org.foodorder.entity.MenuItemEntity;
import org.foodorder.model.OrderItemRequest;

import java.util.List;

/**
 * Strategy interface for selecting and sorting restaurants based on different criteria such as price, rating, or customer-specific requests.
 * This interface provides the methods for selecting the best restaurant and sorting restaurants according to the strategy.
 */
public interface RestaurantSelectionStrategy {

    /**
     * Selects the best restaurant from a list of available restaurants for a given menu item.
     * The selection is based on the strategy's implementation, which could be lowest price, highest rating, etc.
     *
     * @param restaurants The list of available restaurants to select from.
     * @param menuItem The menu item that the customer is interested in ordering.
     * @return The selected RestaurantEntity that best fits the strategy's criteria.
     */
    RestaurantEntity selectRestaurant(List<RestaurantEntity> restaurants, MenuItemEntity menuItem);

    /**
     * Sorts a list of restaurants based on the strategy's sorting criteria.
     * The sorting could be done by price, rating, distance, or any other parameter based on the strategy's implementation.
     *
     * @param restaurants The list of restaurants to be sorted.
     * @return A sorted list of RestaurantEntity objects based on the strategy's criteria.
     */
    List<RestaurantEntity> sortRestaurants(List<RestaurantEntity> restaurants);

    /**
     * Selects the best restaurant from a list of available restaurants based on a customer-specific request.
     * This method allows for custom selection logic where the restaurant is selected as per the customer's preferences, such as specific items or requirements.
     *
     * @param restaurants The list of available restaurants to select from.
     * @param menuItem The cart item that the customer is ordering.
     * @return The selected RestaurantEntity based on customer-specific request criteria.
     */
    RestaurantEntity selectRestaurantAsPerCustomerRequest(List<RestaurantEntity> restaurants, OrderItemRequest menuItem);
}