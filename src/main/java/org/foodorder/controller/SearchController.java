package org.foodorder.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.foodorder.entity.MenuItemEntity;
import org.foodorder.entity.RestaurantEntity;
import org.foodorder.service.MenuService;
import org.foodorder.service.RestaurantSelectorService;
import org.foodorder.service.RestaurantService;
import org.foodorder.strategy.HighestRatingStrategy;
import org.foodorder.strategy.LowestCostStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for handling search requests related to restaurants offering specific menu items.
 */
@Controller
@RequestMapping("/search")
public class SearchController {

    private static final Logger LOGGER = Logger.getLogger(SearchController.class.getName());

    @Autowired
    private MenuService menuService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private RestaurantSelectorService restaurantSelectorService;

    @Autowired
    private HighestRatingStrategy highestRatingStrategy;

    @Autowired
    private LowestCostStrategy lowestCostStrategy;

    /**
     * Search for restaurants offering a specific menu item.
     *
     * @param keyword The name of the menu item being searched for
     * @param sort The sorting criteria, either 'price' or 'rating' (defaults to 'rating')
     * @param model The model to store data for rendering the view
     * @return The view name for displaying search results
     */
    @GetMapping("/menu")
    @Operation(summary = "Search for restaurants offering a specific menu item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search successful"),
            @ApiResponse(responseCode = "404", description = "No restaurants found for the given item")
    })
    public String searchRestaurantsByItem(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "sort", required = false, defaultValue = "rating") String sort,
            Model model) {

        LOGGER.info("Searching for restaurants offering the item: " + keyword);

        try {
            // Find menu items based on the keyword
            List<MenuItemEntity> menuItems = menuService.searchMenuItems(keyword);

            if (menuItems.isEmpty()) {
                LOGGER.warning("No items found for keyword: " + keyword);
                model.addAttribute("message", "No restaurants found offering the item: " + keyword);
                return "search_results";  // Display a message to the user
            }

            // Find restaurants associated with these menu items
            List<RestaurantEntity> restaurants = restaurantService.findRestaurantsByMenuItems(menuItems);

            if (restaurants.isEmpty()) {
                LOGGER.warning("No restaurants found for the given menu items.");
                model.addAttribute("message", "No restaurants found for the item: " + keyword);
                return "search_results";  // No restaurants found
            }

            // Filter each restaurant's menu items to include only those that match the keyword
            for (RestaurantEntity restaurant : restaurants) {
                List<MenuItemEntity> filteredMenuItems = restaurant.getMenuItems().stream()
                        .filter(menuItem -> menuItem.getName().toLowerCase().contains(keyword.toLowerCase()))
                        .collect(Collectors.toList());

                restaurant.setMenuItems(filteredMenuItems);  // Set the filtered menu items back to the restaurant
            }

            // Remove any restaurants that have no matching menu items after filtering
            List<RestaurantEntity> filteredRestaurants = restaurants.stream()
                    .filter(restaurant -> !restaurant.getMenuItems().isEmpty())
                    .collect(Collectors.toList());

            if (filteredRestaurants.isEmpty()) {
                LOGGER.warning("No matching menu items found for keyword: " + keyword);
                model.addAttribute("message", "No restaurants found offering the item: " + keyword);
                return "search_results";
            }

            // Sort restaurants based on the user's selection (price or rating)
            if ("price".equals(sort)) {
                filteredRestaurants.sort(Comparator.comparing(r -> r.getMenuItems().get(0).getPrice())); // Assumes at least 1 menu item
                LOGGER.info("Sorting restaurants by price (Low to High)");
            } else {
                filteredRestaurants.sort(Comparator.comparing(RestaurantEntity::getRating).reversed()); // Rating: High to Low
                LOGGER.info("Sorting restaurants by rating (High to Low)");
            }

            // Log the restaurants and their filtered menu items
            LOGGER.info("Keyword: " + keyword);
            LOGGER.info("Restaurants found: " + filteredRestaurants.size());
            for (RestaurantEntity restaurant : filteredRestaurants) {
                LOGGER.info("Restaurant: " + restaurant.getName() + ", MenuItems: " + restaurant.getMenuItems());
            }

            // Add the filtered restaurants and keyword to the model
            model.addAttribute("restaurants", filteredRestaurants);
            model.addAttribute("keyword", keyword);
            return "search_results";  // Display the search results in Thymeleaf

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error while searching for restaurants offering the item: " + keyword, e);
            model.addAttribute("message", "An error occurred while searching for restaurants.");
            return "error";  // Return an error page in case of failure
        }
    }
}