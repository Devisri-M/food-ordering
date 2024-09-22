package org.foodorder.strategy;

import org.foodorder.entity.RestaurantEntity;
import org.foodorder.entity.MenuItemEntity;
import org.springframework.stereotype.Component;
import java.util.Comparator;
import java.util.List;

@Component
public class HighestRatingStrategy implements RestaurantSelectionStrategy {

    @Override
    public RestaurantEntity selectRestaurant(List<RestaurantEntity> restaurants, MenuItemEntity menuItem) {
        return restaurants.stream()
                .max(Comparator.comparing(RestaurantEntity::getRating))
                .orElse(null);
    }

    @Override
    public List<RestaurantEntity> sortRestaurants(List<RestaurantEntity> restaurants) {
        return restaurants.stream()
                .sorted(Comparator.comparing(RestaurantEntity::getRating).reversed())
                .toList();
    }
}