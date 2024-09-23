package org.foodorder.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;
import org.foodorder.entity.OrderEntity;
import org.foodorder.entity.OrderItemEntity;
import org.foodorder.entity.RestaurantEntity;
import org.foodorder.model.CartItem;
import org.foodorder.model.OrderItemRequest;
import org.foodorder.repository.OrderRepository;
import org.foodorder.repository.OrderItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private RestaurantSelectorService restaurantSelectorService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private RestaurantExecutorService restaurantExecutorService;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);
    private static final int DEFAULT_PREPARATION_TIME = 3000; // 3 seconds for example

    /**
     * Add a new order.
     *
     * @param order The order entity to be saved.
     */
    @Override
    @Transactional
    public void saveOrder(OrderEntity order) {
        LOGGER.info("Saving order with total amount: {}", order.getTotalAmount());
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
    @Override
    @Transactional
    public void saveOrderItem(OrderItemEntity orderItem) {
        LOGGER.info("Saving order item: {}", orderItem);
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
     * @param groupedItems items groups by their restaurants
     */
    @Override
    public OrderEntity saveOrdersToDB(List<CartItem> cartItems, Map<Long, List<CartItem>> groupedItems, Long customerId) {
        BigDecimal totalAmount = cartItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        OrderEntity order = new OrderEntity();
        order.setCustomerId(customerId);
        order.setTotalAmount(totalAmount);
        order.setStatus("PENDING");
        saveOrder(order);

        LOGGER.info("Order successfully saved to orders table");
        for (Map.Entry<Long, List<CartItem>> entry : groupedItems.entrySet()) {
            Long restaurantId = entry.getKey();
            for (CartItem item : entry.getValue()) {
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
        return order;
    }

    /**
     * Saves order and items to orders and orders_items table
     * @param cartItems items in cart
     * @param groupedItems items groups by their restaurants
     */
    @Override
    public OrderEntity saveCustomerOrdersToDB(List<OrderItemRequest> cartItems, Map<Long, List<OrderItemRequest>> groupedItems, Long customerId) {
        BigDecimal totalAmount = cartItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LOGGER.info("Total amount for order: {}", totalAmount);
        OrderEntity order = new OrderEntity();
        order.setCustomerId(customerId);
        order.setTotalAmount(totalAmount);
        order.setStatus("PENDING");
        saveOrder(order);

        LOGGER.info("Order successfully saved to orders table");
        for (Map.Entry<Long, List<OrderItemRequest>> entry : groupedItems.entrySet()) {
            Long restaurantId = entry.getKey();
            for (OrderItemRequest item : entry.getValue()) {
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
        return order;
    }

    /**
     * Places an order by grouping items by restaurant and submitting each restaurant's order processing to an ExecutorService.
     *
     * @param items      the items in the order
     * @param customerId the ID of the customer placing the order
     * @param strategy   the strategy to select restaurants (e.g., "lowest cost", "highest rating")
     * @return a Future representing the result of the order placement
     */
    @Override
    public Future<OrderEntity> placeOrderUponCustomerRequest(List<OrderItemRequest> items, Long customerId, String strategy) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Group items by restaurant
                Map<Long, List<OrderItemRequest>> groupedItems = restaurantSelectorService.groupItemsByRestaurant(items, strategy);

                for (Map.Entry<Long, List<OrderItemRequest>> entry : groupedItems.entrySet()) {
                    Long restaurantId = entry.getKey();
                    List<OrderItemRequest> restaurantItems = entry.getValue();
                    int totalItemsForRestaurant = restaurantItems.stream()
                            .mapToInt(OrderItemRequest::getQuantity)
                            .sum();

                    // Fetch restaurant entity and check if it can accept the order
                    Optional<RestaurantEntity> restaurantOpt = restaurantService.getRestaurantById(restaurantId);
                    if (restaurantOpt.isEmpty()) {
                        throw new RuntimeException("Restaurant not found with ID: " + restaurantId);
                    }
                    RestaurantEntity restaurant = restaurantOpt.get();

                    if (!restaurantService.canPlaceOrder(restaurantId, totalItemsForRestaurant)) {
                        throw new RuntimeException("Restaurant ID " + restaurantId + " cannot handle the order.");
                    }

                    // Increment the processing load for the restaurant
                    LOGGER.info("Current processing load before increment: {}", restaurant.getCurrentProcessingLoad());
                    restaurantService.incrementRestaurantLoad(restaurantId, totalItemsForRestaurant);
                    LOGGER.info("Current processing load after increment: {}", restaurant.getCurrentProcessingLoad());

                    // Submit task to the restaurant executor
                    ExecutorService restaurantExecutor = restaurantExecutorService.getExecutorServiceForRestaurant(restaurantId, restaurant.getMaxCapacity());
                    restaurantExecutor.submit(() -> {
                        try {
                            // Process the order
                            processOrder(restaurantId, restaurantItems);

                            // Simulate dispatch after preparation (3-second preparation time)
                            Thread.sleep(3000);  // Simulating food preparation
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            LOGGER.warn("Thread interrupted during order preparation: {}", e.getMessage());
                        } finally {
                            // Decrement the processing load no matter what (even in case of failure)
                            restaurantService.decrementRestaurantLoad(restaurantId, totalItemsForRestaurant);
                            LOGGER.info("Order processing completed for restaurant ID: {}. Load decremented.", restaurantId);
                        }
                    });

                    // Automatically release the load after a timeout (even if something goes wrong)
                    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                    scheduler.schedule(() -> {
                        restaurantService.decrementRestaurantLoad(restaurantId, totalItemsForRestaurant);
                        LOGGER.info("Automatically released load for restaurant ID: {} after timeout.", restaurantId);
                    }, DEFAULT_PREPARATION_TIME, TimeUnit.MILLISECONDS);
                }
//
//                    // Simulate dispatch after preparation
//                    CompletableFuture.runAsync(() -> {
//                        try {
//                            Thread.sleep(DEFAULT_PREPARATION_TIME);  // Simulate food preparation
//                            restaurantService.decrementRestaurantLoad(restaurantId, totalItemsForRestaurant);  // Release capacity
//                        } catch (InterruptedException e) {
//                            Thread.currentThread().interrupt();
//                            LOGGER.info("Thread interrupted during order preparation" + e);
//                        }
//                    });

                // Save the order to the DB
                return saveCustomerOrdersToDB(items, groupedItems, customerId);
            } catch (Exception e) {
                throw new RuntimeException("Failed to place order: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Processes the order for a specific restaurant.
     *
     * @param restaurantId   the ID of the restaurant processing the order
     * @param restaurantItems the items being processed by the restaurant
     */
    private void processOrder(Long restaurantId, List<OrderItemRequest> restaurantItems) {
        restaurantItems.forEach(item -> {
            LOGGER.info("Processing item: " + item.getName() + " for restaurant ID: " + restaurantId);
        });
    }
}