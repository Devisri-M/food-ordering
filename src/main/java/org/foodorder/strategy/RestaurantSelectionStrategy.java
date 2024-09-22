package org.foodorder.strategy;

import org.foodorder.entity.RestaurantEntity;
import org.foodorder.entity.MenuItemEntity;
import java.util.List;

public interface RestaurantSelectionStrategy {
    RestaurantEntity selectRestaurant(List<RestaurantEntity> restaurants, MenuItemEntity menuItem);
    List<RestaurantEntity> sortRestaurants(List<RestaurantEntity> restaurants);
}