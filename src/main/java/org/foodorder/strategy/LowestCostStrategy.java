package org.foodorder.strategy;

import org.foodorder.entity.RestaurantEntity;
import org.foodorder.entity.MenuItemEntity;
import org.springframework.stereotype.Component;
import java.util.Comparator;
import java.util.List;

@Component
public class LowestCostStrategy implements RestaurantSelectionStrategy {

    @Override
    public RestaurantEntity selectRestaurant(List<RestaurantEntity> restaurants, MenuItemEntity menuItem) {
        return restaurants.stream()
                .min(Comparator.comparing(restaurant -> menuItem.getPrice()))
                .orElse(null);
    }

    @Override
    public List<RestaurantEntity> sortRestaurants(List<RestaurantEntity> restaurants) {
        return restaurants.stream()
                .sorted(Comparator.comparing(restaurant -> restaurant.getMenuItems().stream()
                        .mapToDouble(menuItem -> menuItem.getPrice().doubleValue())
                        .min().orElse(Double.MAX_VALUE)))
                .toList();
    }
}