package org.foodorder.controller;

import org.foodorder.entity.OrderEntity;
import org.foodorder.model.CartItem;
import org.foodorder.model.OrderItemRequest;
import org.foodorder.model.OrderRequest;
import org.foodorder.service.OrderService;
import org.foodorder.service.RestaurantSelectorService;
import org.foodorder.service.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RestaurantService restaurantService;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @Mock
    private RestaurantSelectorService restaurantSelectorService;

    private OrderRequest validOrderRequest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mocking the view resolver to avoid circular view path issues
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/");
        viewResolver.setSuffix(".jsp");

        List<OrderItemRequest> orderItems = new ArrayList<>();
        orderItems.add(new OrderItemRequest("Pizza", 1));
        orderItems.add(new OrderItemRequest("Burger", 1));

        validOrderRequest = new OrderRequest();
        validOrderRequest.setCustomerId(1L);
        validOrderRequest.setItems(orderItems);
        validOrderRequest.setStrategy("rating");

        mockMvc = MockMvcBuilders.standaloneSetup(orderController)
                .setViewResolvers(viewResolver)
                .build();
    }

    // Test for checking if an order can be placed
    @Test
    void testCanPlaceOrder() throws Exception {
        when(restaurantService.canPlaceOrder(1L, 3)).thenReturn(true);

        mockMvc.perform(get("/orders/can-place/1/3"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(restaurantService, times(1)).canPlaceOrder(1L, 3);
    }

    // Test for successfully placing an order
    @Test
    void testPlaceOrder_Success() throws Exception {
        // Mock a valid cart item
        CartItem cartItem = new CartItem(1L, "Pizza", new BigDecimal("10.00"), 2, 1L);
        List<CartItem> cartItems = Collections.singletonList(cartItem);

        // Mock the customerId that should be passed in the request
        Long customerId = 1L;

        // Mock the restaurant service to return true for the canPlaceOrder check
        when(restaurantService.canPlaceOrder(1L, 2)).thenReturn(true);

        // Mock the restaurant service placeOrder method to do nothing (simulate success)
        doNothing().when(restaurantService).placeOrder(1L, 2);

        // Mock the saved order entity
        OrderEntity mockOrder = new OrderEntity();
        mockOrder.setId(1L);  // Ensure the order ID is set

        // Mock the order service to return the mock order entity
        when(orderService.saveOrdersToDB(anyList(), anyMap(), eq(customerId))).thenReturn(mockOrder);

        // Perform the mock request, passing customerId in the request body as well
        mockMvc.perform(post("/orders/place")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"menuItemId\":1,\"name\":\"Pizza\",\"price\":10.00,\"quantity\":2,\"restaurantId\":1}]")
                        .param("customerId", customerId.toString()))  // Pass customerId in the request
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Order placed successfully!"))
                .andExpect(jsonPath("$.orderId").value(1L));

        // Verify interactions with the service
        verify(restaurantService, times(1)).canPlaceOrder(1L, 2);
        verify(restaurantService, times(1)).placeOrder(1L, 2);
        verify(orderService, times(1)).saveOrdersToDB(anyList(), anyMap(), eq(customerId));
    }

    @Test
    void testPlaceOrder_Failure_RestaurantCapacityExceeded() throws Exception {
        // Mock a valid cart item
        CartItem cartItem = new CartItem(1L, "Pizza", new BigDecimal("10.00"), 2, 1L);
        List<CartItem> cartItems = Collections.singletonList(cartItem);

        // Mock restaurant service behavior for capacity exceeded
        when(restaurantService.canPlaceOrder(1L, 2)).thenReturn(false);

        // Perform the mock request
        mockMvc.perform(post("/orders/place")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"menuItemId\":1,\"name\":\"Pizza\",\"price\":10.00,\"quantity\":2,\"restaurantId\":1}]"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Restaurant ID 1 cannot handle the order for 2 items."));

        verify(restaurantService, times(1)).canPlaceOrder(1L, 2);
        verify(orderService, never()).saveOrdersToDB(anyList(), anyMap(), anyLong());
    }

    // Test for dispatching an order successfully
    @Test
    void testDispatchOrder_Success() throws Exception {
        doNothing().when(restaurantService).dispatchOrder(1L, 3);

        mockMvc.perform(post("/orders/dispatch/1/3"))
                .andExpect(status().isOk())
                .andExpect(content().string("Order dispatched successfully!"));

        verify(restaurantService, times(1)).dispatchOrder(1L, 3);
    }

    // Test for failure in dispatching an order due to error
    @Test
    void testDispatchOrder_Failure() throws Exception {
        doThrow(new RuntimeException("Error occurred")).when(restaurantService).dispatchOrder(1L, 3);

        mockMvc.perform(post("/orders/dispatch/1/3"))
                .andExpect(content().string("Failed to dispatch order: Error occurred"));

        verify(restaurantService, times(1)).dispatchOrder(1L, 3);
    }

    // Test for fetching orders by customer ID
    @Test
    void testGetOrdersByCustomer_Success() throws Exception {
        OrderEntity mockOrder = new OrderEntity();
        mockOrder.setId(1L);
        when(orderService.getOrdersByCustomerId(1L)).thenReturn(Collections.singletonList(mockOrder));

        mockMvc.perform(get("/orders/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(orderService, times(1)).getOrdersByCustomerId(1L);
    }

    // Test for fetching orders by customer ID with no content
    @Test
    void testGetOrdersByCustomer_NoContent() throws Exception {
        when(orderService.getOrdersByCustomerId(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/orders/customer/1"))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).getOrdersByCustomerId(1L);
    }

//    @Test
//    public void testPlaceOrderByCustomerRequest_Success() throws Exception {
//        // Mock service calls
//        when(restaurantSelectorService.groupItemsByRestaurant(anyList(), anyString())).thenReturn(getGroupedItems());
//        when(restaurantService.canPlaceOrder(anyLong(), anyInt())).thenReturn(true);
//        when(orderService.saveCustomerOrdersToDB(anyList(), anyMap(), anyLong())).thenReturn(getMockOrder());
//
//        // Perform the POST request and validate
//        mockMvc.perform(post("/orders/placeOrderByCustomerRequest")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(asJsonString(validOrderRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("Order placed successfully!"))
//                .andExpect(jsonPath("$.orderId").value(1L));
//    }

    @Test
    public void testPlaceOrderByCustomerRequest_NoItems() throws Exception {
        // Empty items scenario
        validOrderRequest.setItems(Collections.emptyList());

        mockMvc.perform(post("/orders/placeOrderByCustomerRequest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(validOrderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").doesNotExist());
    }

//    @Test
//    public void testPlaceOrderByCustomerRequest_RestaurantCannotHandle() throws Exception {
//        // Mock restaurant capacity failure and return 1 item for restaurant ID 1
//        Map<Long, List<OrderItemRequest>> groupedItems = new HashMap<>();
//        List<OrderItemRequest> itemsForRestaurant = Collections.singletonList(
//                new OrderItemRequest("Pizza", 1)
//        );
//        itemsForRestaurant.forEach(item -> item.setPrice(BigDecimal.valueOf(10.99)));
//        groupedItems.put(1L, itemsForRestaurant);
//
//
//        // Mock service responses
//        when(restaurantSelectorService.groupItemsByRestaurant(anyList(), anyString())).thenReturn(groupedItems);
//        when(restaurantService.canPlaceOrder(1L, 1)).thenReturn(false); // Simulate restaurant cannot handle the order
//
//        // Perform the mock request
//        mockMvc.perform(post("/orders/placeOrderByCustomerRequest")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(asJsonString(validOrderRequest)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message").value("Restaurant ID 1 cannot handle the order for 1 items."));
//    }

    // Helper Methods
    private Map<Long, List<OrderItemRequest>> getGroupedItems() {
        Map<Long, List<OrderItemRequest>> groupedItems = new HashMap<>();
        List<OrderItemRequest> items = Arrays.asList(
                new OrderItemRequest("Pizza", 1),
                new OrderItemRequest("Burger", 1)
        );
        groupedItems.put(1L, items);
        return groupedItems;
    }

    private OrderEntity getMockOrder() {
        OrderEntity order = new OrderEntity();
        order.setId(1L);
        order.setCustomerId(1L);
        order.setTotalAmount(BigDecimal.valueOf(18.50));
        order.setStatus("PENDING");
        return order;
    }

    // JSON Helper Method
    private static String asJsonString(final Object obj) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    // Test for placing an order through the UI
//    @Test
//    void testPlaceOrderUI_Success() throws Exception {
//        CartItem cartItem = new CartItem(1L, "Pizza", null, 2, 1L);
//        when(restaurantService.canPlaceOrder(1L, 2)).thenReturn(true);
//        doNothing().when(restaurantService).placeOrder(1L, 2);
//
//        mockMvc.perform(post("/orders/placeui")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("[{\"menuItemId\":1,\"name\":\"Pizza\",\"quantity\":2,\"restaurantId\":1}]"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("order_success"))
//                .andExpect(model().attributeExists("message"));
//
//        verify(restaurantService, times(1)).placeOrder(1L, 2);
//    }

    // Test for failing to place an order through the UI when restaurant cannot handle the items
//    @Test
//    void testPlaceOrderUI_Failure() throws Exception {
//        CartItem cartItem = new CartItem(1L, "Pizza", new BigDecimal("10.00"), 10, 1L);
//        when(restaurantService.canPlaceOrder(1L, 10)).thenReturn(false); // Simulate that the restaurant cannot handle the order
//
//        mockMvc.perform(post("/orders/placeui")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("[{\"menuItemId\":1,\"name\":\"Pizza\",\"price\":10.00,\"quantity\":10,\"restaurantId\":1}]"))
//                .andExpect(status().isOk())  // Ensure it's an OK status
//                .andExpect(view().name("error")) // Expecting an "error" view
//                .andExpect(model().attributeExists("error")); // Check if the error attribute exists in the model
//
//        verify(restaurantService, times(1)).canPlaceOrder(1L, 10);
//    }
}