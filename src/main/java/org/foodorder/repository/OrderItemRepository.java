package org.foodorder.repository;

import org.foodorder.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {

    /**
     * Retrieve all order items for a given order ID.
     *
     * @param orderId The ID of the order.
     * @return List of order items belonging to the given order.
     */
    List<OrderItemEntity> findByOrderId(Long orderId);
}