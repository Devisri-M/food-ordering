package org.foodorder.repository;

import jakarta.transaction.Transactional;
import java.util.List;
import org.foodorder.entity.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing RestaurantEntity persistence.
 * Extends JpaRepository to provide CRUD operations and custom query methods for the RestaurantEntity.
 */
@Repository
@Transactional
public interface RestaurantRepository extends JpaRepository<RestaurantEntity, Long> {

  /**
   * Find restaurants by a case-insensitive search on the name field.
   *
   * @param keyword the search term used to find restaurants
   * @return a list of restaurants whose name contains the given keyword, case-insensitively
   */
  List<RestaurantEntity> findByNameContainingIgnoreCase(String keyword);

  /**
   * Update the 'isOpen' status of a restaurant by its ID.
   *
   * @param id the ID of the restaurant to update
   * @param isOpen the new status to set (true for open, false for closed)
   */
  @Query("UPDATE RestaurantEntity t SET t.isOpen = :isOpen WHERE t.id = :id")
  @Modifying
  public void updateRestaurantIsOpenStatus(Long id, boolean isOpen);

  /**
   * Find all restaurants offering a specific menu item by its ID.
   *
   * @param menuItemId the ID of the menu item
   * @return a list of restaurants that offer the specified menu item
   */
  @Query("SELECT r FROM RestaurantEntity r WHERE r.id IN (SELECT m.restaurantId FROM MenuItemEntity m WHERE m.id = :menuItemId)")
  List<RestaurantEntity> findRestaurantsByMenuItemId(Long menuItemId);
}