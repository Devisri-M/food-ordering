package org.foodorder.service;

import org.foodorder.entity.RestaurantEntity;
import org.foodorder.entity.MenuItemEntity;
import org.foodorder.model.CartItem;
import org.foodorder.model.OrderItemRequest;

import java.util.List;
import java.util.Map;

/**
 * Interface for selecting and sorting restaurants based on various strategies like
 * lowest cost or highest rating.
 */
public interface RestaurantSelectorService {

    /**
     * Selects the best restaurant based on the given strategy (either "price" or "rating").
     * If the strategy is not recognized, it defaults to the highest rating strategy.
     *
     * @param restaurants  the list of restaurants to select from
     * @param menuItem     the menu item to be considered for the selection
     * @param strategyType the strategy to use ("price" for lowest cost, otherwise highest rating)
     * @return the selected RestaurantEntity based on the strategy
     */
    RestaurantEntity selectBestRestaurant(List<RestaurantEntity> restaurants, MenuItemEntity menuItem, String strategyType);

    /**
     * Group items by the best restaurant based on the selection strategy.
     *
     * @param items    The list of items to be grouped.
     * @param strategy The strategy to use for selection (e.g., "price" or "rating").
     * @return A map of restaurant IDs to the list of items that each restaurant will fulfill.
     */
    Map<Long, List<OrderItemRequest>> groupItemsByRestaurant(List<OrderItemRequest> items, String strategy);

    /**
     * Get a list of restaurants that serve the specified menu item.
     *
     * @param itemName The name of the item.
     * @return A list of restaurants serving the menu item.
     */
    List<RestaurantEntity> getRestaurantsByMenuItemName(String itemName);

    /**
     * Selects the best restaurant for a specific cart item based on the strategy provided by the customer.
     * The strategy can either be "price" for the lowest cost or "rating" for the highest rating.
     *
     * @param restaurants The list of restaurants available for selection.
     * @param item        The cart item for which the restaurant is being selected.
     * @param strategy    The selection strategy, either "price" or "rating".
     * @return The restaurant entity that best fits the given strategy.
     * @throws RuntimeException if an error occurs during selection.
     */
    RestaurantEntity selectBestRestaurantAsPerCustomerRequest(List<RestaurantEntity> restaurants, OrderItemRequest item, String strategy);

    /**
     * Sorts a list of restaurants based on the given strategy (either "price" or "rating").
     * If the strategy is not recognized, it defaults to sorting by rating.
     *
     * @param restaurants  the list of restaurants to sort
     * @param strategyType the strategy to use for sorting ("price" for lowest cost, otherwise highest rating)
     * @return the sorted list of restaurants based on the strategy
     */
    List<RestaurantEntity> sortRestaurants(List<RestaurantEntity> restaurants, String strategyType);
}