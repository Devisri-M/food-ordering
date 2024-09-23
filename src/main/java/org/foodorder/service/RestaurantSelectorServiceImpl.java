package org.foodorder.service;

import org.foodorder.entity.RestaurantEntity;
import org.foodorder.entity.MenuItemEntity;
import org.foodorder.model.OrderItemRequest;
import org.foodorder.repository.MenuItemRepository;
import org.foodorder.repository.RestaurantRepository;
import org.foodorder.strategy.HighestRatingStrategy;
import org.foodorder.strategy.LowestCostStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class to handle restaurant selection based on different strategies such as
 * lowest cost or highest rating.
 */
@Service
public class RestaurantSelectorServiceImpl implements RestaurantSelectorService {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantSelectorServiceImpl.class);

    private final HighestRatingStrategy highestRatingStrategy;
    private final LowestCostStrategy lowestCostStrategy;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;

    @Autowired
    private MenuService menuService;

    public RestaurantSelectorServiceImpl(
            HighestRatingStrategy highestRatingStrategy, LowestCostStrategy lowestCostStrategy,
            RestaurantRepository restaurantRepository, MenuItemRepository menuItemRepository
    ) {
        this.highestRatingStrategy = highestRatingStrategy;
        this.lowestCostStrategy = lowestCostStrategy;
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
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

    /**
     * Group items by the best restaurant based on the selection strategy.
     *
     * @param items The list of items to be grouped.
     * @param strategy The strategy to use for selection (e.g., "price" or "rating").
     * @return A map of restaurant IDs to the list of items that each restaurant will fulfill.
     */
    @Override
    public Map<Long, List<OrderItemRequest>> groupItemsByRestaurant(List<OrderItemRequest> items, String strategy) {
        Map<Long, List<OrderItemRequest>> groupedItems = new HashMap<>();

        for (OrderItemRequest item : items) {
            // fetch menu items to get it's id by name
            List<MenuItemEntity> availableMenuItems = menuItemRepository.findByNameContainingIgnoreCase(item.getName());

            // Fetch available restaurants by item name
            List<RestaurantEntity> availableRestaurants = getRestaurantsByMenuItemName(item.getName());

            if (availableRestaurants.isEmpty()) {
                throw new RuntimeException("No restaurants available for menu item: " + item.getName());
            }

            // Select the best restaurant according to the strategy
            RestaurantEntity selectedRestaurant = selectBestRestaurantAsPerCustomerRequest(availableRestaurants, item, strategy);

            // Fetch the menuItemId from the availableMenuItems
            MenuItemEntity selectedMenuItem = availableMenuItems.stream()
                    .filter(menuItem -> menuItem.getRestaurantId().equals(selectedRestaurant.getId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Menu item not found for restaurant ID: " + selectedRestaurant.getId()));

            // Set the menuItemId in the OrderItemRequest
            item.setMenuItemId(selectedMenuItem.getId());
            // Group items by the selected restaurant
            groupedItems.computeIfAbsent(selectedRestaurant.getId(), k -> new ArrayList<>()).add(item);
        }

        return groupedItems;
    }

    /**
     * Get a list of restaurants that serve the specified menu item.
     *
     * @param itemName name of the item.
     * @return A list of restaurants serving the menu item.
     */
    @Override
    public List<RestaurantEntity> getRestaurantsByMenuItemName(String itemName) {
        logger.info("Fetching restaurants that serve menu item: {}", itemName);

        try {
            // Find menu items by their name
            List<MenuItemEntity> menuItems = menuItemRepository.findByNameContainingIgnoreCase(itemName);

            // Extract unique restaurant IDs and fetch corresponding restaurants
            List<Long> restaurantIds = menuItems.stream()
                    .map(MenuItemEntity::getRestaurantId)
                    .distinct()
                    .collect(Collectors.toList());

            List<RestaurantEntity> restaurants = restaurantRepository.findAllById(restaurantIds);

            logger.info("Found {} restaurants serving menu item: {}", restaurants.size(), itemName);
            return restaurants;
        } catch (Exception e) {
            logger.error("Error fetching restaurants for menu item: {}", itemName, e);
            throw new RuntimeException("Failed to fetch restaurants for menu item: " + itemName, e);
        }
    }

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
    @Override
    public RestaurantEntity selectBestRestaurantAsPerCustomerRequest(List<RestaurantEntity> restaurants, OrderItemRequest item, String strategy) {
        try {
            logger.info("Selecting best restaurant for item {} using strategy: {}", item.getName(), strategy);
            if ("price".equalsIgnoreCase(strategy)) {
                return lowestCostStrategy.selectRestaurantAsPerCustomerRequest(restaurants, item);
            } else {
                return highestRatingStrategy.selectRestaurantAsPerCustomerRequest(restaurants, item);
            }
        } catch (Exception e) {
            logger.error("Error selecting restaurant for item {}: {}", item.getName(), e.getMessage());
            throw new RuntimeException("Failed to select best restaurant based on strategy.", e);
        }
    }
}