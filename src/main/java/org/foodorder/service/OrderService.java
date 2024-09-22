package org.foodorder.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.foodorder.entity.OrderEntity;
import org.foodorder.entity.OrderItemEntity;
import org.foodorder.model.CartItem;
import org.foodorder.repository.OrderRepository;
import org.foodorder.repository.OrderItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    /**
     * Add a new order.
     *
     * @param order The order entity to be saved.
     */
    @Transactional
    public void saveOrder(OrderEntity order) {
        LOGGER.info("Saving order {}", order.getTotalAmount());
        try {
            LOGGER.info("Adding new order for customer ID: {}", order.getCustomerId());
            orderRepository.save(order);
        } catch (Exception e) {
            LOGGER.error("Error adding new order for customer ID: {}", order.getCustomerId(), e);
            throw new RuntimeException("Failed to add order", e);
        }
    }

    /**
     * Add a new order item.
     *
     * @param orderItem The order item to be added.
     */
    @Transactional
    public void saveOrderItem(OrderItemEntity orderItem) {
        LOGGER.info("Saving order item {}", orderItem);
        try {
            LOGGER.info("Adding new order item for order ID: {}", orderItem.getId());
            orderItemRepository.save(orderItem);
        } catch (Exception e) {
            LOGGER.error("Error adding order item for order ID: {}", orderItem.getId(), e);
            throw new RuntimeException("Failed to add order item", e);
        }
    }

    /**
     * Retrieve an order by its ID.
     *
     * @param orderId The ID of the order.
     * @return Optional containing the found order or empty if not found.
     */
    public Optional<OrderEntity> getOrderById(Long orderId) {
        try {
            LOGGER.info("Fetching order by ID: {}", orderId);
            return orderRepository.findById(orderId);
        } catch (Exception e) {
            LOGGER.error("Error fetching order by ID: {}", orderId, e);
            throw new RuntimeException("Failed to fetch order", e);
        }
    }

    /**
     * Retrieve all order items by order ID.
     *
     * @param orderId The ID of the order.
     * @return List of order items for the given order.
     */
    public List<OrderItemEntity> getOrderItemsByOrderId(Long orderId) {
        try {
            LOGGER.info("Fetching order items for order ID: {}", orderId);
            return orderItemRepository.findByOrderId(orderId);
        } catch (Exception e) {
            LOGGER.error("Error fetching order items for order ID: {}", orderId, e);
            throw new RuntimeException("Failed to fetch order items", e);
        }
    }

    /**
     * Retrieve all orders for a given customer.
     *
     * @param customerId The ID of the customer.
     * @return List of orders for the customer.
     */
    public List<OrderEntity> getOrdersByCustomerId(Long customerId) {
        try {
            LOGGER.info("Fetching orders for customer ID: {}", customerId);
            return orderRepository.findByCustomerId(customerId);
        } catch (Exception e) {
            LOGGER.error("Error fetching orders for customer ID: {}", customerId, e);
            throw new RuntimeException("Failed to fetch orders", e);
        }
    }

    /**
     * Retrieve all orders in the system.
     *
     * @return List of all orders.
     */
    public List<OrderEntity> getAllOrders() {
        try {
            LOGGER.info("Fetching all orders");
            return orderRepository.findAll();
        } catch (Exception e) {
            LOGGER.error("Error fetching all orders", e);
            throw new RuntimeException("Failed to fetch all orders", e);
        }
    }

    /**
     * Saves order and items to orders and orders_items table
     * @param cartItems items in cart
     * @param groupedItems items groups by their restuarents
     */
    public void saveOrdersToDB(List<CartItem> cartItems, Map<Long, List<CartItem>> groupedItems) {
        // Calculate total amount for the order
        BigDecimal totalAmount = cartItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Create and save the order
        OrderEntity order = new OrderEntity();
        order.setCustomerId(1L);
        order.setTotalAmount(totalAmount);
        order.setStatus("PENDING");
        saveOrder(order);

        LOGGER.info("Order successfully saved to orders table");
        // Save the order items
        for (Map.Entry<Long, List<CartItem>> entry : groupedItems.entrySet()) {
            Long restaurantId = entry.getKey();
            for (CartItem item : entry.getValue()) {
                // Create and populate order item entity
                OrderItemEntity orderItem = new OrderItemEntity();
                orderItem.setOrder(order);
                orderItem.setId(order.getId());
                orderItem.setMenuItemId(item.getMenuItemId());
                orderItem.setRestaurantId(restaurantId);
                orderItem.setQuantity(item.getQuantity());
                orderItem.setPrice(item.getPrice());

                saveOrderItem(orderItem);
                LOGGER.info("Item successfully saved to order_items table");
            }
        }
    }
}