package org.foodorder.controller;

import org.foodorder.entity.OrderEntity;
import org.foodorder.model.CartItem;
import org.foodorder.model.OrderItemRequest;
import org.foodorder.model.OrderRequest;
import org.foodorder.service.OrderService;
import org.foodorder.service.RestaurantSelectorService;
import org.foodorder.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Controller to handle order-related APIs such as checking if an order can be placed,
 * placing an order, and dispatching an order.
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger LOGGER = java.util.logging.Logger.getLogger(OrderController.class.getName());
    private static final String ORDER_SUCCESS = "Order dispatched successfully!";
    private static final String ORDER_FAILURE = "Failed to dispatch order: ";
    private static final String REDIRECT_ORDER_SUCCESS = "order_success";
    private static final String REDIRECT_ORDER_FAILURE = "error";

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RestaurantSelectorService restaurantSelectorService;

    /**
     * API to check if an order can be placed for a restaurant based on its current load and capacity.
     *
     * @param restaurantId The ID of the restaurant.
     * @param items The number of items requested in the order.
     * @return true if the order can be placed, false otherwise.
     */
    @GetMapping("/can-place/{restaurantId}/{items}")
    public boolean canPlaceOrder(@PathVariable Long restaurantId, @PathVariable int items) {
        try {
            LOGGER.info("Checking if restaurant ID {}" + restaurantId + "can place an order for {} items" + items);
            boolean canPlaceOrder = restaurantService.canPlaceOrder(restaurantId, items);
            LOGGER.info("Can place order result for restaurant ID {}" + restaurantId + ": {}" + canPlaceOrder);
            return canPlaceOrder;
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Error while checking if order can be placed for restaurant ID {}: {}" +  restaurantId);
            return false; // Default to false in case of error
        }
    }

    /**
     * Handles the request to place an order.
     * This method checks if each restaurant can handle the requested items in the cart before placing the order.
     *
     * @param cartItems A list of {@link CartItem} objects representing items in the order.
     * @return A {@link ResponseEntity} with success or error messages based on the result.
     */
    @PostMapping(value = "/place", consumes = "application/json")
    public ResponseEntity<?> placeOrder(@RequestBody List<CartItem> cartItems, Long customerId) {
        LOGGER.log(Level.INFO, "----Printing cart items------" + cartItems);
        try {
            LOGGER.info("Received order request with cart items." + cartItems.size());

            // Group the items by restaurantId
            Map<Long, List<CartItem>> groupedItems = cartItems.stream()
                    .collect(Collectors.groupingBy(CartItem::getRestaurantId));

            // Verify each restaurant can handle the order before proceeding
            for (Map.Entry<Long, List<CartItem>> entry : groupedItems.entrySet()) {
                Long restaurantId = entry.getKey();
                int totalItemsForRestaurant = entry.getValue().stream()
                        .mapToInt(CartItem::getQuantity)
                        .sum();

                LOGGER.info("totalItemsForRestaurant: " + totalItemsForRestaurant);
                LOGGER.info("Checking if Restaurant ID " + restaurantId + " can handle " + totalItemsForRestaurant + "items");
                if (!restaurantService.canPlaceOrder(restaurantId, totalItemsForRestaurant)) {
                    String errorMessage = "Restaurant ID " + restaurantId + " cannot handle the order for " + totalItemsForRestaurant + " items.";
                    LOGGER.log(Level.SEVERE, errorMessage);
                    return ResponseEntity.status(500).body(Collections.singletonMap("message", errorMessage));
                }
            }

            // If all restaurants can handle the load, proceed to place the order
            for (Map.Entry<Long, List<CartItem>> entry : groupedItems.entrySet()) {
                Long restaurantId = entry.getKey();
                int totalItemsForRestaurant = entry.getValue().stream()
                        .mapToInt(CartItem::getQuantity)
                        .sum();
                LOGGER.log(Level.INFO, "Placing order for {} items at Restaurant ID: {0}: {1}", new Object[]{totalItemsForRestaurant, restaurantId});
                restaurantService.placeOrder(restaurantId, totalItemsForRestaurant);
            }

            OrderEntity order = orderService.saveOrdersToDB(cartItems, groupedItems, customerId);

            LOGGER.info("Order placed successfully for all restaurants.");
            return ResponseEntity.ok(Map.of("message", "Order placed successfully!", "orderId", order.getId()));

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error occurred while placing the order: {0} {1}", new Object[]{e.getMessage(), e});
            return ResponseEntity.status(500).body(Collections.singletonMap("message", "Failed to place order: " + e.getMessage()));
        }
    }

    /**
     * API to place an order with multiple items and quantities.
     *
     * @param orderRequest The request body containing items, quantities, and strategy.
     * @return ResponseEntity indicating success or failure of the order.
     */
    @PostMapping("/placeOrderByCustomerRequest")
    public ResponseEntity<?> placeOrderByCustomerRequest(@RequestBody OrderRequest orderRequest) {
        try {
            // Validate input
            if (orderRequest.getItems().isEmpty()) {
                return ResponseEntity.badRequest().body("No items provided for order placement.");
            }

            List<OrderItemRequest> items = orderRequest.getItems();
            Long customerId = orderRequest.getCustomerId();
            String strategy = orderRequest.getStrategy();

            // If the customer doesn't provide a strategy, default to "highest rating"
            if (strategy == null || strategy.isEmpty()) {
                strategy = "rating";  // Default to highest rating strategy
                LOGGER.info("No strategy provided, defaulting to highest rating.");
            }

            LOGGER.log(Level.INFO, "Received order request with items for customer ID: {0}, strategy: {1}",
                    new Object[]{customerId, strategy});

            // Group the items by restaurant based on the selection strategy
            Map<Long, List<OrderItemRequest>> groupedItems = restaurantSelectorService.groupItemsByRestaurant(items, strategy);

            // Verify each restaurant can handle the order before proceeding
            for (Map.Entry<Long, List<OrderItemRequest>> entry : groupedItems.entrySet()) {
                Long restaurantId = entry.getKey();
                int totalItemsForRestaurant = entry.getValue().stream()
                        .mapToInt(OrderItemRequest::getQuantity)
                        .sum();

                LOGGER.info("Checking if Restaurant ID " + restaurantId + " can handle " + totalItemsForRestaurant + " items");
                if (!restaurantService.canPlaceOrder(restaurantId, totalItemsForRestaurant)) {
                    String errorMessage = "Restaurant ID " + restaurantId + " cannot handle the order for " + totalItemsForRestaurant + " items.";
                    LOGGER.log(Level.SEVERE, errorMessage);
                    return ResponseEntity.status(500).body(Collections.singletonMap("message", errorMessage));
                }
            }

            // Place the order for each restaurant
            for (Map.Entry<Long, List<OrderItemRequest>> entry : groupedItems.entrySet()) {
                Long restaurantId = entry.getKey();
                int totalItemsForRestaurant = entry.getValue().stream()
                        .mapToInt(OrderItemRequest::getQuantity)
                        .sum();

                LOGGER.log(Level.INFO, "Placing order for {0} items at Restaurant ID {1}", new Object[]{totalItemsForRestaurant, restaurantId});
                restaurantService.placeOrder(restaurantId, totalItemsForRestaurant);
            }

            // Save the order to the database
            OrderEntity order = orderService.saveCustomerOrdersToDB(items, groupedItems, customerId);

            LOGGER.log(Level.INFO, "Order placed successfully for customer ID: {0}", customerId);
            return ResponseEntity.ok(Map.of("message", "Order placed successfully!", "orderId", order.getId()));

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error occurred while placing the order: {0} {1}", new Object[]{e.getMessage(), e});
            return ResponseEntity.status(500).body(Collections.singletonMap("message", "Failed to place order: " + e.getMessage()));
        }
    }

    // =========== UI Methods to handle items ==============

    @GetMapping("/order_success")
    public String orderSuccess() {
        return REDIRECT_ORDER_SUCCESS;
    }

    /**
     * Handles the request to place an order.
     * This method checks if each restaurant can handle the requested items in the cart before placing the order.
     *
     * @param cartItems A list of {@link CartItem} objects representing items in the order.
     * @return A {@link ModelAndView} with a success or error view based on the result.
     */
    @PostMapping(value = "/placeui", consumes = "application/json")
    public String placeOrder(@RequestBody List<CartItem> cartItems, Model model) {
        LOGGER.log(Level.INFO, "----Printing cart items------" + cartItems);
        try {
            LOGGER.info("Received order request with cart items." + cartItems.size());

            // Group the items by restaurantId
            Map<Long, List<CartItem>> groupedItems = cartItems.stream()
                    .collect(Collectors.groupingBy(CartItem::getRestaurantId));

            // Verify each restaurant can handle the order before proceeding
            for (Map.Entry<Long, List<CartItem>> entry : groupedItems.entrySet()) {
                Long restaurantId = entry.getKey();
                int totalItemsForRestaurant = entry.getValue().stream()
                        .mapToInt(CartItem::getQuantity)
                        .sum();

                LOGGER.info("Checking if Restaurant ID " + restaurantId + " can handle " + totalItemsForRestaurant + "items");
                if (!restaurantService.canPlaceOrder(restaurantId, totalItemsForRestaurant)) {
                    String errorMessage = "Restaurant ID " + restaurantId + " cannot handle the order for " + totalItemsForRestaurant + " items.";
                    LOGGER.log(Level.SEVERE, errorMessage);
                    // Add error message to model for the error page
                    model.addAttribute("error", errorMessage);
                    return REDIRECT_ORDER_FAILURE; // Return the error page if the order cannot be placed
                }
            }

            // If all restaurants can handle the load, proceed to place the order
            for (Map.Entry<Long, List<CartItem>> entry : groupedItems.entrySet()) {
                Long restaurantId = entry.getKey();
                int totalItemsForRestaurant = entry.getValue().stream()
                        .mapToInt(CartItem::getQuantity)
                        .sum();
                LOGGER.log(Level.INFO, "Placing order for {} items at Restaurant ID: {0}: {1}", new Object[]{totalItemsForRestaurant, restaurantId});
                restaurantService.placeOrder(restaurantId, totalItemsForRestaurant);
            }

            OrderEntity order = orderService.saveOrdersToDB(cartItems, groupedItems, 1L);

            LOGGER.info("Order placed successfully for all restaurants.");
            // Add success message to model for the success page
            model.addAttribute("message", "Order placed successfully!");
            return REDIRECT_ORDER_SUCCESS; // Return the success page

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error occurred while placing the order: {0} {1}", new Object[]{e.getMessage(), e});
            // Add error message to model for the error page
            model.addAttribute("error", "Failed to place order: " + e.getMessage());
            return REDIRECT_ORDER_FAILURE; // Return the error page on failure
        }
    }

    /**
     * API to dispatch an order from a restaurant and release the processing load.
     * This updates the restaurant's current processing load after dispatch.
     *
     * @param restaurantId The ID of the restaurant.
     * @param items The number of items being dispatched.
     * @return Success message or failure message depending on the outcome of dispatching.
     */
    @PostMapping("/dispatch/{restaurantId}/{items}")
    public String dispatchOrder(@PathVariable Long restaurantId, @PathVariable int items) {
        try {
            LOGGER.info("Dispatching order for restaurant ID {} " + restaurantId);
            restaurantService.dispatchOrder(restaurantId, items);
            LOGGER.info("Order dispatched successfully for restaurant ID {}" + restaurantId);
            return ORDER_SUCCESS;
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Error dispatching order for restaurant ID {}: {}" + restaurantId, e.getMessage());
            return ORDER_FAILURE + e.getMessage();
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderEntity>> getOrdersByCustomer(@PathVariable Long customerId) {
        try {
            List<OrderEntity> orders = orderService.getOrdersByCustomerId(customerId);
            if (orders.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping
    public List<OrderEntity> getAllOrders() {
        return orderService.getAllOrders();
    }

    // API to fetch order by ID
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderEntity> getOrderById(@PathVariable Long orderId) {
        try {
            Optional<OrderEntity> order = orderService.getOrderById(orderId);
            return order.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(404).body(null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}