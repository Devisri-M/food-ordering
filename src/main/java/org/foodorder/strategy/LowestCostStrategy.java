package org.foodorder.strategy;

import org.foodorder.entity.RestaurantEntity;
import org.foodorder.entity.MenuItemEntity;
import org.foodorder.model.OrderItemRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Strategy implementation for selecting the restaurant offering the lowest cost.
 * This class implements the {@link RestaurantSelectionStrategy} to select or sort restaurants
 * based on the price of menu items, providing the customer with the lowest-cost option.
 */
@Component
public class LowestCostStrategy implements RestaurantSelectionStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(LowestCostStrategy.class);

    /**
     * Selects the restaurant offering the lowest price for the given menu item.
     * If no restaurant is found, it logs an error and returns null.
     *
     * @param restaurants The list of available restaurants.
     * @param menuItem    The menu item for which the restaurant selection is being made.
     * @return The restaurant offering the lowest cost, or null if no restaurant is available.
     */
    @Override
    public RestaurantEntity selectRestaurant(List<RestaurantEntity> restaurants, MenuItemEntity menuItem) {
        try {
            return restaurants.stream()
                    .min(Comparator.comparing(restaurant -> menuItem.getPrice()))
                    .orElseThrow(() -> {
                        LOGGER.error("No restaurant found offering the menu item with ID: {}", menuItem.getId());
                        return new NoSuchElementException("No restaurant available with the given menu item.");
                    });
        } catch (Exception e) {
            LOGGER.error("Error selecting lowest-cost restaurant for menu item ID: {}", menuItem.getId(), e);
            throw e;
        }
    }

    /**
     * Selects the restaurant offering the lowest price based on the customer's request.
     * If no restaurant is found, it logs an error and returns null.
     *
     * @param restaurants The list of available restaurants.
     * @param menuItem    The cart item for which the restaurant selection is being made.
     * @return The restaurant offering the lowest cost, or null if no restaurant is available.
     */
    @Override
    public RestaurantEntity selectRestaurantAsPerCustomerRequest(List<RestaurantEntity> restaurants, OrderItemRequest menuItem) {
        try {
            return restaurants.stream()
                    .min(Comparator.comparing(restaurant -> menuItem.getPrice()))
                    .orElseThrow(() -> {
                        LOGGER.error("No restaurant found for customer request with menu item: {}", menuItem.getName());
                        return new NoSuchElementException("No restaurant available for the given customer request.");
                    });
        } catch (Exception e) {
            LOGGER.error("Error selecting lowest-cost restaurant for customer request: {}", menuItem.getName(), e);
            throw e;
        }
    }

    /**
     * Sorts the list of restaurants in ascending order based on the lowest price of menu items.
     * If the list is empty or no prices are available, it logs a warning and returns the unsorted list.
     *
     * @param restaurants The list of restaurants to be sorted.
     * @return The sorted list of restaurants based on the lowest menu item price.
     */
    @Override
    public List<RestaurantEntity> sortRestaurants(List<RestaurantEntity> restaurants) {
        try {
            if (restaurants.isEmpty()) {
                LOGGER.warn("Attempted to sort an empty list of restaurants.");
                return restaurants;
            }
            return restaurants.stream()
                    .sorted(Comparator.comparing(restaurant -> restaurant.getMenuItems().stream()
                            .mapToDouble(menuItem -> menuItem.getPrice().doubleValue())
                            .min().orElse(Double.MAX_VALUE)))
                    .toList();
        } catch (Exception e) {
            LOGGER.error("Error sorting restaurants by lowest cost.", e);
            throw e;
        }
    }
}