package org.foodorder.repository;

import org.foodorder.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    /**
     * Find all orders by customer ID.
     *
     * @param customerId The ID of the customer whose orders to retrieve.
     * @return A list of orders made by the specified customer.
     */
    List<OrderEntity> findByCustomerId(Long customerId);

    /**
     * Find all orders with a specific status.
     *
     * @param status The status of the orders to retrieve.
     * @return A list of orders with the specified status.
     */
    List<OrderEntity> findByStatus(String status);

    /**
     * Find all orders where the total amount exceeds a specified value.
     *
     * @param amount The amount to compare against.
     * @return A list of orders where the total amount is greater than the specified value.
     */
    @Query("SELECT o FROM OrderEntity o WHERE o.totalAmount > :amount")
    List<OrderEntity> findOrdersWithAmountGreaterThan(@Param("amount") Double amount);

    /**
     * Find all orders for a specific customer where the total amount exceeds a specified value.
     *
     * @param customerId The ID of the customer.
     * @param amount The amount to compare against.
     * @return A list of orders for the customer where the total amount is greater than the specified value.
     */
    @Query("SELECT o FROM OrderEntity o WHERE o.customerId = :customerId AND o.totalAmount > :amount")
    List<OrderEntity> findOrdersByCustomerWithAmountGreaterThan(@Param("customerId") Long customerId, @Param("amount") Double amount);
}