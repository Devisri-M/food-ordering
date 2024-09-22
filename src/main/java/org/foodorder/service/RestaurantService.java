package org.foodorder.service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.foodorder.entity.MenuItemEntity;
import org.foodorder.entity.RestaurantEntity;
import org.foodorder.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for managing Restaurant-related operations.
 */
@Service
public class RestaurantService {

  private static final Logger LOGGER = Logger.getLogger(RestaurantService.class.getName());

  @Autowired
  private RestaurantRepository restaurantRepository;

  /**
   * Get a list of all restaurants.
   *
   * @return a list of all RestaurantEntity objects
   */
  public List<RestaurantEntity> getAllRestaurants() {
    LOGGER.info("Fetching all restaurants from the database");
    return restaurantRepository.findAll();
  }

  /**
   * Get a restaurant by its ID.
   *
   * @param id the ID of the restaurant
   * @return an Optional containing the RestaurantEntity if found, else empty
   */
  public Optional<RestaurantEntity> getRestaurantById(Long id) {
    LOGGER.log(Level.INFO, "Fetching restaurant with ID: {0}", id);
    return restaurantRepository.findById(id);
  }

  /**
   * Add a new restaurant.
   *
   * @param restaurant the RestaurantEntity to be added
   * @return the saved RestaurantEntity
   */
  public RestaurantEntity addRestaurant(RestaurantEntity restaurant) {
    LOGGER.log(Level.INFO, "Adding a new restaurant: {0}", restaurant.getName());
    return restaurantRepository.save(restaurant);
  }

  /**
   * Update an existing restaurant by its ID.
   *
   * @param id the ID of the restaurant to be updated
   * @param restaurant the new RestaurantEntity data to update
   * @return the updated RestaurantEntity or null if the restaurant was not found
   */
  public RestaurantEntity updateRestaurant(Long id, RestaurantEntity restaurant) {
    LOGGER.log(Level.INFO, "Updating restaurant with ID: {0}", id);
    Optional<RestaurantEntity> existingRestaurant = restaurantRepository.findById(id);
    if (existingRestaurant.isPresent()) {
      RestaurantEntity updatedRestaurant = existingRestaurant.get();
      updatedRestaurant.setName(restaurant.getName());
      updatedRestaurant.setAddress(restaurant.getAddress());
      // Update other fields as necessary
      return restaurantRepository.save(updatedRestaurant);
    }
    LOGGER.log(Level.WARNING, "Restaurant with ID: {0} not found", id);
    return null; // Or throw an exception
  }

  /**
   * Delete a restaurant by its ID.
   *
   * @param id the ID of the restaurant to delete
   */
  public void deleteRestaurant(Long id) {
    try {
      LOGGER.log(Level.INFO, "Deleting restaurant with ID: {0}", id);
      restaurantRepository.deleteById(id);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error deleting restaurant with ID: " + id, e);
      throw new RuntimeException("Failed to delete restaurant with ID: " + id);
    }
  }

  /**
   * Find all restaurants.
   *
   * @return a list of all RestaurantEntity objects
   */
  public List<RestaurantEntity> findAll() {
    LOGGER.info("Fetching all restaurants from the database");
    return restaurantRepository.findAll();
  }

  /**
   * Find restaurants by name containing the given keyword (case-insensitive).
   *
   * @param keyword the keyword to search for
   * @return a list of restaurants matching the keyword
   */
  public List<RestaurantEntity> findByNameContainingIgnoreCase(String keyword) {
    LOGGER.log(Level.INFO, "Searching restaurants with name containing (case-insensitive): {0}", keyword);
    return restaurantRepository.findByNameContainingIgnoreCase(keyword);
  }

  /**
   * Save a restaurant entity.
   *
   * @param restaurant the RestaurantEntity to save
   */
  public void save(RestaurantEntity restaurant) {
    LOGGER.log(Level.INFO, "Saving restaurant: {0}", restaurant.getName());
    restaurantRepository.save(restaurant);
  }

  /**
   * Find a restaurant by its ID.
   *
   * @param id the ID of the restaurant
   * @return an Optional containing the RestaurantEntity if found, else empty
   */
  public Optional<RestaurantEntity> findById(Long id) {
    LOGGER.log(Level.INFO, "Finding restaurant by ID: {0}", id);
    return restaurantRepository.findById(id);
  }

  /**
   * Delete a restaurant by its ID.
   *
   * @param id the ID of the restaurant to delete
   */
  public void deleteById(Long id) {
    try {
      LOGGER.log(Level.INFO, "Deleting restaurant by ID: {0}", id);
      restaurantRepository.deleteById(id);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error deleting restaurant with ID: " + id, e);
      throw new RuntimeException("Failed to delete restaurant with ID: " + id);
    }
  }

  /**
   * Update the open/closed status of a restaurant.
   *
   * @param id the ID of the restaurant
   * @param openStatus the new open/closed status
   */
  public void updateRestaurantIsOpenStatus(Long id, boolean openStatus) {
    try {
      LOGGER.log(Level.INFO, "Updating open status for restaurant with ID: {0} to: {1}", new Object[]{id, openStatus});
      restaurantRepository.updateRestaurantIsOpenStatus(id, openStatus);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error updating open status for restaurant with ID: " + id, e);
      throw new RuntimeException("Failed to update open status for restaurant with ID: " + id);
    }
  }

  /**
   * Find restaurants by menu items.
   *
   * @param menuItems the list of menu items
   * @return a list of restaurants that offer these menu items
   */
  public List<RestaurantEntity> findRestaurantsByMenuItems(List<MenuItemEntity> menuItems) {
    LOGGER.info("Finding restaurants offering the specified menu items.");
    return menuItems.stream()
            .map(MenuItemEntity::getRestaurantId)
            .distinct()
            .map(restaurantRepository::findById)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
  }

  /**
   * Checks if a restaurant can accept an order based on its current processing load and
   * maximum capacity.
   *
   * @param restaurantId The ID of the restaurant.
   * @param itemCount    The number of items in the order.
   * @return true if the restaurant can accept the order, false otherwise.
   */
  public boolean canRestaurantAcceptOrder(Long restaurantId, int itemCount) {
    try {
      RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
              .orElseThrow(() -> new RuntimeException("Restaurant with ID " + restaurantId + " not found"));

      boolean canAccept = restaurant.getCurrentProcessingLoad() + itemCount <= restaurant.getMaxCapacity();
      LOGGER.log(Level.INFO, "Restaurant ID {0} can accept more items: {1}", new Object[]{restaurantId, canAccept});
      return canAccept;
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error checking if restaurant with ID {0} can accept order", restaurantId);
      throw new RuntimeException("Failed to check if the restaurant can accept the order", e);
    }
  }

  /**
   * Increments the current processing load of the restaurant when an order is placed.
   * Ensures that the order does not exceed the restaurant's maximum capacity.
   *
   * @param restaurantId The ID of the restaurant.
   * @param itemCount    The number of items in the order.
   * @throws RuntimeException If the restaurant's processing load exceeds the maximum capacity.
   */
  public void incrementRestaurantLoad(Long restaurantId, int itemCount) {
    try {
      RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
              .orElseThrow(() -> new RuntimeException("Restaurant with ID " + restaurantId + " not found"));

      if (canRestaurantAcceptOrder(restaurantId, itemCount)) {
        restaurant.setCurrentProcessingLoad(restaurant.getCurrentProcessingLoad() + itemCount);
        restaurantRepository.save(restaurant);
        LOGGER.log(Level.INFO, "Incremented processing load for restaurant ID {0} by {1}", new Object[]{restaurantId, itemCount});
      } else {
        LOGGER.log(Level.WARNING, "Restaurant ID {0} exceeded capacity with item count {1}", new Object[]{restaurantId, itemCount});
        throw new RuntimeException("Restaurant capacity exceeded");
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error incrementing processing load for restaurant with ID {0}", restaurantId);
      throw new RuntimeException("Failed to increment restaurant processing load", e);
    }
  }

  /**
   * Decrements the current processing load of the restaurant when an order is dispatched.
   * Ensures the processing load does not fall below zero.
   *
   * @param restaurantId The ID of the restaurant.
   * @param itemCount    The number of items in the order that were dispatched.
   */
  public void decrementRestaurantLoad(Long restaurantId, int itemCount) {
    try {
      RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
              .orElseThrow(() -> new RuntimeException("Restaurant with ID " + restaurantId + " not found"));

      int newProcessingLoad = Math.max(0, restaurant.getCurrentProcessingLoad() - itemCount);
      restaurant.setCurrentProcessingLoad(newProcessingLoad);
      restaurantRepository.save(restaurant);

      LOGGER.log(Level.INFO, "Decremented processing load for restaurant ID {0} by {1}", new Object[]{restaurantId, itemCount});
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error decrementing processing load for restaurant with ID {0}", new Object[]{restaurantId, e});
      throw new RuntimeException("Failed to decrement restaurant processing load", e);
    }
  }

  /**
   * Fetches the current processing load of a restaurant.
   *
   * @param restaurantId The ID of the restaurant.
   * @return The current processing load of the restaurant.
   */
  public int getCurrentProcessingLoad(Long restaurantId) {
    try {
      RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
              .orElseThrow(() -> new RuntimeException("Restaurant with ID " + restaurantId + " not found"));

      int currentLoad = restaurant.getCurrentProcessingLoad();
      LOGGER.log(Level.INFO, "Current processing load for restaurant ID {0} is {1}", new Object[]{restaurantId, currentLoad});
      return currentLoad;
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error fetching current processing load for restaurant with ID {0}", new Object[]{restaurantId, e});
      throw new RuntimeException("Failed to get current processing load", e);
    }
  }

//  /**
//   * Check if the restaurant can accept an order based on its current processing load and max capacity.
//   *
//   * @param restaurantId   The ID of the restaurant.
//   * @param itemsRequested The number of items in the order.
//   * @return true if the restaurant can accept the order, false otherwise.
//   */
//  public boolean canPlaceOrder(Long restaurantId, int itemsRequested) {
//    try {
//      LOGGER.info("We are in canPlaceOrder of Restuarent");
//      RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
//              .orElseThrow(() -> new RuntimeException("Restaurant not found with ID: " + restaurantId));
//
//      LOGGER.info("-----itemsRequested--------" + itemsRequested);
//      LOGGER.info("restuarent currentProcessingLoad: " + restaurant.getCurrentProcessingLoad());
//      LOGGER.info("-----max Capacity--------" + restaurant.getMaxCapacity());
//      boolean canPlaceOrder = restaurant.getCurrentProcessingLoad() + itemsRequested <= restaurant.getMaxCapacity();
//      LOGGER.info("------------canPlaceOrder-----------" + canPlaceOrder);
//      LOGGER.log(Level.INFO, "Can place order for restaurant ID {0}: {1}", new Object[]{restaurantId, canPlaceOrder});
//      return canPlaceOrder;
//    } catch (RuntimeException e) {
//      LOGGER.log(Level.SEVERE, "Error checking if restaurant can place order: {0}", e.getMessage());
//      throw e;
//    } catch (Exception e) {
//      LOGGER.log(Level.SEVERE, "Unexpected error while checking if restaurant can place order for ID {0}: {1}", new Object[]{restaurantId, e.getMessage()});
//      throw new RuntimeException("Failed to check if order can be placed.");
//    }
//  }

  /**
   * Checks if the restaurant can accept an order based on its current load and maximum capacity.
   * This method first attempts to retrieve the capacity status from the cache. If the cache is empty,
   * the method queries the database and updates the cache.
   *
   * @param restaurantId   The ID of the restaurant to check.
   * @param itemsRequested The number of items requested in the order.
   * @return true if the restaurant can process the requested items, false otherwise.
   */
  //@Cacheable(value = "restaurantCapacity", key = "#restaurantId", unless = "#result == false")
  public boolean canPlaceOrder(Long restaurantId, int itemsRequested) {
    try {
      LOGGER.log(Level.INFO, "Checking if Restaurant ID {0} can process {1} items.", new Object[]{restaurantId, itemsRequested});

      // Attempt to fetch the restaurant entity from the database
      RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
              .orElseThrow(() -> new RuntimeException("Restaurant not found with ID: " + restaurantId));

      // Calculate available capacity
      int availableCapacity = restaurant.getMaxCapacity() - restaurant.getCurrentProcessingLoad();
      boolean canPlaceOrder = itemsRequested <= availableCapacity;

      LOGGER.log(Level.INFO, "Restaurant ID {0} has available capacity of {1} items. Can Place Order: {2}",
              new Object[]{restaurantId, availableCapacity, canPlaceOrder});

      return canPlaceOrder;

    } catch (RuntimeException e) {
      // Log and propagate the exception
      LOGGER.log(Level.SEVERE, "Error occurred while checking capacity for Restaurant ID {0}: {1}",
              new Object[]{restaurantId, e.getMessage(), e});
      throw e;
    }
  }

//  /**
//   * Places an order by updating the restaurant's current processing load.
//   *
//   * @param restaurantId   The ID of the restaurant.
//   * @param itemsRequested The number of items in the order.
//   * @throws RuntimeException if the order cannot be placed due to exceeding the restaurant's capacity.
//   */
//  public void placeOrder(Long restaurantId, int itemsRequested) {
//    try {
//      RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
//              .orElseThrow(() -> new RuntimeException("Restaurant not found with ID: " + restaurantId));
//
//      if (canPlaceOrder(restaurantId, itemsRequested)) {
//        restaurant.setCurrentProcessingLoad(restaurant.getCurrentProcessingLoad() + itemsRequested);
//        restaurantRepository.save(restaurant);
//        LOGGER.log(Level.INFO, "Order placed for restaurant ID {0}. Current load: {1}", new Object[]{restaurantId, restaurant.getCurrentProcessingLoad()});
//      } else {
//        throw new RuntimeException("Cannot place order, restaurant capacity exceeded");
//      }
//    } catch (RuntimeException e) {
//      LOGGER.log(Level.SEVERE, "Error placing order for restaurant ID {0}: {1}", new Object[]{restaurantId, e.getMessage()});
//      throw e;
//    } catch (Exception e) {
//      LOGGER.log(Level.SEVERE, "Unexpected error placing order for restaurant ID {0}: {1}", new Object[]{restaurantId, e.getMessage()});
//      throw new RuntimeException("Failed to place order.");
//    }
//  }

  /**
   * Places an order by updating the restaurant's current processing load if the restaurant has the capacity.
   * If the restaurant cannot process the requested number of items, the order is not placed.
   *
   * @param restaurantId   The ID of the restaurant where the order is being placed.
   * @param itemsRequested The number of items requested for the restaurant.
   * @throws RuntimeException If the restaurant cannot handle the order or other errors occur.
   */
  //@CacheEvict(value = "restaurantCapacity", key = "#restaurantId") // Evict cache after placing an order
  public void placeOrder(Long restaurantId, int itemsRequested) {
    try {
      LOGGER.log(Level.INFO, "Attempting to place order for {0} items at Restaurant ID {1}", new Object[]{itemsRequested, restaurantId});

      // Retrieve restaurant data from the repository
      RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
              .orElseThrow(() -> new RuntimeException("Restaurant not found with ID: " + restaurantId));

      // Check if the restaurant can accept the order
      if (canPlaceOrder(restaurantId, itemsRequested)) {
        // Update the restaurant's processing load
        restaurant.setCurrentProcessingLoad(restaurant.getCurrentProcessingLoad() + itemsRequested);
        restaurantRepository.save(restaurant);

        LOGGER.log(Level.INFO, "Successfully placed order for {0} items at Restaurant ID {1}. Current Processing Load: {2}",
                new Object[]{itemsRequested, restaurantId, restaurant.getCurrentProcessingLoad()});
      } else {
        // Throw an exception if the restaurant cannot handle the order
        throw new RuntimeException("Cannot place order. Restaurant ID " + restaurantId + " exceeds processing capacity.");
      }
    } catch (RuntimeException e) {
      LOGGER.log(Level.SEVERE, "Error occurred while placing order for Restaurant ID {0}: {1} {2}",
              new Object[]{restaurantId, e.getMessage(), e});
      throw e;
    }
  }

  /**
   * Simulates dispatching an order and releasing the processing capacity.
   * This updates the current processing load of the restaurant by decrementing the dispatched items.
   *
   * @param restaurantId   The ID of the restaurant.
   * @param itemsDispatched The number of items being dispatched.
   */
  //@CacheEvict(value = "restaurantCapacity", key = "#restaurantId") // Evict cache after dispatching an order
  public void dispatchOrder(Long restaurantId, int itemsDispatched) {
    try {
      LOGGER.log(Level.INFO, "Dispatching {0} items for Restaurant ID {1}", new Object[]{itemsDispatched, restaurantId});

      // Retrieve the restaurant entity from the repository
      RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
              .orElseThrow(() -> new RuntimeException("Restaurant not found with ID: " + restaurantId));

      // Calculate the new processing load after dispatch
      int newLoad = Math.max(restaurant.getCurrentProcessingLoad() - itemsDispatched, 0);
      restaurant.setCurrentProcessingLoad(newLoad);
      restaurantRepository.save(restaurant);

      LOGGER.log(Level.INFO, "Order dispatched for Restaurant ID {0}. Updated Processing Load: {1}",
              new Object[]{restaurantId, newLoad});
    } catch (RuntimeException e) {
      LOGGER.log(Level.SEVERE, "Error dispatching order for Restaurant ID {0}: {1}", new Object[]{restaurantId, e.getMessage()});
      throw e;
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Unexpected error dispatching order for Restaurant ID {0}: {1}", new Object[]{restaurantId, e.getMessage()});
      throw new RuntimeException("Failed to dispatch order.");
    }
  }
}