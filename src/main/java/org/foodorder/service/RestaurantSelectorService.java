package org.foodorder.service;

import org.foodorder.entity.RestaurantEntity;
import org.foodorder.entity.MenuItemEntity;
import org.foodorder.strategy.HighestRatingStrategy;
import org.foodorder.strategy.LowestCostStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class to handle restaurant selection based on different strategies such as
 * lowest cost or highest rating.
 */
@Service
public class RestaurantSelectorService {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantSelectorService.class);

    private final HighestRatingStrategy highestRatingStrategy;
    private final LowestCostStrategy lowestCostStrategy;

    /**
     * Constructor for RestaurantSelectorService that takes the two strategy beans for highest rating
     * and lowest cost.
     *
     * @param highestRatingStrategy the strategy for selecting the restaurant with the highest rating
     * @param lowestCostStrategy the strategy for selecting the restaurant with the lowest cost
     */
    @Autowired
    public RestaurantSelectorService(HighestRatingStrategy highestRatingStrategy, LowestCostStrategy lowestCostStrategy) {
        this.highestRatingStrategy = highestRatingStrategy;
        this.lowestCostStrategy = lowestCostStrategy;
    }

    /**
     * Selects the best restaurant based on the given strategy (either "price" or "rating").
     * If the strategy is not recognized, it defaults to the highest rating strategy.
     *
     * @param restaurants the list of restaurants to select from
     * @param menuItem the menu item to be considered for the selection
     * @param strategyType the strategy to use ("price" for lowest cost, otherwise highest rating)
     * @return the selected RestaurantEntity based on the strategy
     */
    public RestaurantEntity selectBestRestaurant(List<RestaurantEntity> restaurants, MenuItemEntity menuItem, String strategyType) {
        try {
            if ("price".equals(strategyType)) {
                logger.info("Selecting restaurant based on lowest price strategy for menu item: {}", menuItem.getName());
                return lowestCostStrategy.selectRestaurant(restaurants, menuItem);
            } else {
                logger.info("Selecting restaurant based on highest rating strategy for menu item: {}", menuItem.getName());
                return highestRatingStrategy.selectRestaurant(restaurants, menuItem);
            }
        } catch (Exception e) {
            logger.error("Error occurred while selecting the best restaurant for menu item: {}", menuItem.getName(), e);
            throw new RuntimeException("Failed to select the best restaurant based on the strategy.");
        }
    }

    /**
     * Sorts a list of restaurants based on the given strategy (either "price" or "rating").
     * If the strategy is not recognized, it defaults to sorting by rating.
     *
     * @param restaurants the list of restaurants to sort
     * @param strategyType the strategy to use for sorting ("price" for lowest cost, otherwise highest rating)
     * @return the sorted list of restaurants based on the strategy
     */
    public List<RestaurantEntity> sortRestaurants(List<RestaurantEntity> restaurants, String strategyType) {
        try {
            if ("price".equals(strategyType)) {
                logger.info("Sorting restaurants by price (Low to High).");
                return lowestCostStrategy.sortRestaurants(restaurants);
            } else {
                logger.info("Sorting restaurants by rating (High to Low).");
                return highestRatingStrategy.sortRestaurants(restaurants);
            }
        } catch (Exception e) {
            logger.error("Error occurred while sorting restaurants using strategy: {}", strategyType, e);
            throw new RuntimeException("Failed to sort restaurants based on the strategy.");
        }
    }
}