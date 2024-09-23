package org.foodorder.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;

import org.foodorder.entity.OrderEntity;
import org.foodorder.entity.OrderItemEntity;
import org.foodorder.model.CartItem;
import org.foodorder.model.OrderItemRequest;

/**
 * Interface for managing orders and order items.
 * Provides methods to save orders, retrieve order details, and manage order items.
 */
public interface OrderService {

    /**
     * Add a new order.
     *
     * @param order The order entity to be saved.
     */
    void saveOrder(OrderEntity order);

    /**
     * Add a new order item.
     *
     * @param orderItem The order item to be added.
     */
    void saveOrderItem(OrderItemEntity orderItem);

    /**
     * Retrieve an order by its ID.
     *
     * @param orderId The ID of the order.
     * @return Optional containing the found order or empty if not found.
     */
    Optional<OrderEntity> getOrderById(Long orderId);

    /**
     * Retrieve all order items by order ID.
     *
     * @param orderId The ID of the order.
     * @return List of order items for the given order.
     */
    List<OrderItemEntity> getOrderItemsByOrderId(Long orderId);

    /**
     * Retrieve all orders for a given customer.
     *
     * @param customerId The ID of the customer.
     * @return List of orders for the customer.
     */
    List<OrderEntity> getOrdersByCustomerId(Long customerId);

    /**
     * Retrieve all orders in the system.
     *
     * @return List of all orders.
     */
    List<OrderEntity> getAllOrders();

    /**
     * Saves order and its items to the database.
     *
     * @param cartItems    List of items in the cart.
     * @param groupedItems Items grouped by their respective restaurants.
     */
    OrderEntity saveOrdersToDB(List<CartItem> cartItems, Map<Long, List<CartItem>> groupedItems, Long customerId);

    /**
     * Saves customer order requests and its items to the database.
     *
     * @param orderItemRequests    List of items in the customer ordered.
     * @param groupedItems Items grouped by their respective restaurants.
     */
    OrderEntity saveCustomerOrdersToDB(List<OrderItemRequest> orderItemRequests, Map<Long, List<OrderItemRequest>> groupedItems, Long customerId);

    /**
     * Places an order by grouping items by restaurant and submitting each restaurant's order processing to an ExecutorService.
     *
     * @param items      the items in the order
     * @param customerId the ID of the customer placing the order
     * @param strategy   the strategy to select restaurants (e.g., "lowest cost", "highest rating")
     * @return a Future representing the result of the order placement
     */
    Future<OrderEntity> placeOrderUponCustomerRequest(List<OrderItemRequest> items, Long customerId, String strategy);
}