package org.foodorder.service;

import org.foodorder.entity.MenuItemEntity;
import org.foodorder.entity.RestaurantEntity;

import java.util.List;
import java.util.Optional;

/**
 * Interface for managing Restaurant-related operations such as adding, updating,
 * and deleting restaurants, as well as handling restaurant orders.
 */
public interface RestaurantService {

  /**
   * Get a list of all restaurants.
   *
   * @return a list of all RestaurantEntity objects
   */
  List<RestaurantEntity> getAllRestaurants();

  /**
   * Get a restaurant by its ID.
   *
   * @param id the ID of the restaurant
   * @return an Optional containing the RestaurantEntity if found, else empty
   */
  Optional<RestaurantEntity> getRestaurantById(Long id);

  /**
   * Add a new restaurant.
   *
   * @param restaurant the RestaurantEntity to be added
   * @return the saved RestaurantEntity
   */
  RestaurantEntity addRestaurant(RestaurantEntity restaurant);

  /**
   * Update an existing restaurant by its ID.
   *
   * @param id         the ID of the restaurant to be updated
   * @param restaurant the new RestaurantEntity data to update
   * @return the updated RestaurantEntity or null if the restaurant was not found
   */
  RestaurantEntity updateRestaurant(Long id, RestaurantEntity restaurant);

  /**
   * Delete a restaurant by its ID.
   *
   * @param id the ID of the restaurant to delete
   */
  void deleteRestaurant(Long id);

  /**
   * Find restaurants by name containing the given keyword (case-insensitive).
   *
   * @param keyword the keyword to search for
   * @return a list of restaurants matching the keyword
   */
  List<RestaurantEntity> findByNameContainingIgnoreCase(String keyword);

  /**
   * Checks if a restaurant can accept an order based on its current processing load and maximum capacity.
   *
   * @param restaurantId The ID of the restaurant.
   * @param itemCount    The number of items in the order.
   * @return true if the restaurant can accept the order, false otherwise.
   */
  boolean canRestaurantAcceptOrder(Long restaurantId, int itemCount);

  /**
   * Increments the current processing load of the restaurant when an order is placed.
   * Ensures that the order does not exceed the restaurant's maximum capacity.
   *
   * @param restaurantId The ID of the restaurant.
   * @param itemCount    The number of items in the order.
   */
  void incrementRestaurantLoad(Long restaurantId, int itemCount);

  /**
   * Decrements the current processing load of the restaurant when an order is dispatched.
   * Ensures the processing load does not fall below zero.
   *
   * @param restaurantId The ID of the restaurant.
   * @param itemCount    The number of items in the order that were dispatched.
   */
  void decrementRestaurantLoad(Long restaurantId, int itemCount);

  /**
   * Places an order by updating the restaurant's current processing load if the restaurant has the capacity.
   * If the restaurant cannot process the requested number of items, the order is not placed.
   *
   * @param restaurantId   The ID of the restaurant where the order is being placed.
   * @param itemsRequested The number of items requested for the restaurant.
   */
  void placeOrder(Long restaurantId, int itemsRequested);

  /**
   * Simulates dispatching an order and releasing the processing capacity.
   * This updates the current processing load of the restaurant by decrementing the dispatched items.
   *
   * @param restaurantId   The ID of the restaurant.
   * @param itemsDispatched The number of items being dispatched.
   */
  void dispatchOrder(Long restaurantId, int itemsDispatched);

  /**
   * Find restaurants by menu items.
   *
   * @param menuItems the list of menu items
   * @return a list of restaurants that offer these menu items
   */
  List<RestaurantEntity> findRestaurantsByMenuItems(List<MenuItemEntity> menuItems);

  /**
   * Checks if the restaurant can accept an order based on its current load and maximum capacity.
   * This method first attempts to retrieve the capacity status from the cache. If the cache is empty,
   * the method queries the database and updates the cache.
   *
   * @param restaurantId   The ID of the restaurant to check.
   * @param itemsRequested The number of items requested in the order.
   * @return true if the restaurant can process the requested items, false otherwise.
   */
  boolean canPlaceOrder(Long restaurantId, int itemsRequested);

  /**
   * Get a list of all restaurants.
   *
   * @return a list of all RestaurantEntity objects
   */
  List<RestaurantEntity> findAll();

  /**
   * Save a restaurant entity to the database.
   *
   * @param restaurant the {@link RestaurantEntity} to be saved.
   */
  void save(RestaurantEntity restaurant);

  /**
   * Find a restaurant by its ID.
   *
   * @param id the ID of the restaurant.
   * @return an {@link Optional} containing the found {@link RestaurantEntity},
   *         or an empty {@link Optional} if not found.
   */
  Optional<RestaurantEntity> findById(Long id);

  /**
   * Delete a restaurant from the database by its ID.
   *
   * @param id the ID of the restaurant to delete.
   * @throws RuntimeException if the deletion fails.
   */
  void deleteById(Long id);

  /**
   * Update the open/closed status of a restaurant.
   *
   * @param id        the ID of the restaurant.
   * @param openStatus the new open/closed status (true for open, false for closed).
   * @throws RuntimeException if the update operation fails.
   */
  void updateRestaurantIsOpenStatus(Long id, boolean openStatus);

  /**
   * Fetches the current processing load of a restaurant.
   *
   * @param restaurantId The ID of the restaurant.
   * @return The current processing load of the restaurant.
   */
  int getCurrentProcessingLoad(Long restaurantId);
}