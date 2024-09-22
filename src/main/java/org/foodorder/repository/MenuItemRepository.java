package org.foodorder.repository;

import java.util.List;
import org.foodorder.entity.MenuItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing MenuItemEntity persistence.
 * Extends JpaRepository to provide CRUD operations and custom query methods for the MenuItemEntity.
 */
@Repository
public interface MenuItemRepository extends JpaRepository<MenuItemEntity, Long> {

    /**
     * Find all menu items associated with a specific restaurant by the restaurant's ID.
     *
     * @param restaurantId the ID of the restaurant
     * @return a list of menu items belonging to the specified restaurant
     */
    List<MenuItemEntity> findByRestaurantId(Long restaurantId);

    /**
     * Find menu items by a case-insensitive search on the name field.
     *
     * @param keyword the search term used to find menu items
     * @return a list of menu items whose name contains the given keyword, case-insensitively
     */
    List<MenuItemEntity> findByNameContainingIgnoreCase(String keyword);
}