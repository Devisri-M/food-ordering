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
 * Strategy implementation for selecting the best restaurant based on the highest rating.
 * This class implements the {@link RestaurantSelectionStrategy} to select or sort restaurants
 * by rating, offering the customer the highest-rated restaurant for their desired menu item.
 */
@Component
public class HighestRatingStrategy implements RestaurantSelectionStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(HighestRatingStrategy.class);

    /**
     * Selects the highest-rated restaurant from the list of available restaurants for a given menu item.
     * If no restaurant is available, it will log the information and throw a NoSuchElementException.
     *
     * @param restaurants The list of available restaurants.
     * @param menuItem    The menu item for which the restaurant selection is being made.
     * @return The restaurant entity with the highest rating.
     * @throws NoSuchElementException If no restaurant is available.
     */
    @Override
    public RestaurantEntity selectRestaurant(List<RestaurantEntity> restaurants, MenuItemEntity menuItem) {
        try {
            return restaurants.stream()
                    .max(Comparator.comparing(RestaurantEntity::getRating))
                    .orElseThrow(() -> {
                        LOGGER.error("No restaurant found with menu item ID: {}", menuItem.getId());
                        return new NoSuchElementException("No restaurant available with the given menu item.");
                    });
        } catch (Exception e) {
            LOGGER.error("Error selecting highest-rated restaurant for menu item ID: {}", menuItem.getId(), e);
            throw e;
        }
    }

    /**
     * Selects the highest-rated restaurant based on the customer's request.
     * If no restaurant is available, it will log the information and throw a NoSuchElementException.
     *
     * @param restaurants The list of available restaurants.
     * @param menuItem    The cart item for which the restaurant selection is being made.
     * @return The restaurant entity with the highest rating.
     * @throws NoSuchElementException If no restaurant is available.
     */
    @Override
    public RestaurantEntity selectRestaurantAsPerCustomerRequest(List<RestaurantEntity> restaurants, OrderItemRequest menuItem) {
        try {
            return restaurants.stream()
                    .max(Comparator.comparing(RestaurantEntity::getRating))
                    .orElseThrow(() -> {
                        LOGGER.error("No restaurant found for customer request with menu item: {}", menuItem.getName());
                        return new NoSuchElementException("No restaurant available for the given customer request.");
                    });
        } catch (Exception e) {
            LOGGER.error("Error selecting highest-rated restaurant for customer request: {}", menuItem.getName(), e);
            throw e;
        }
    }

    /**
     * Sorts the list of restaurants in descending order based on their ratings.
     * If the list is empty, it will log a warning and return the empty list.
     *
     * @param restaurants The list of restaurants to be sorted.
     * @return The sorted list of restaurants based on rating (high to low).
     */
    @Override
    public List<RestaurantEntity> sortRestaurants(List<RestaurantEntity> restaurants) {
        try {
            if (restaurants.isEmpty()) {
                LOGGER.warn("Attempted to sort an empty list of restaurants.");
                return restaurants;
            }
            return restaurants.stream()
                    .sorted(Comparator.comparing(RestaurantEntity::getRating).reversed())
                    .toList();
        } catch (Exception e) {
            LOGGER.error("Error sorting restaurants by rating.", e);
            throw e;
        }
    }
}