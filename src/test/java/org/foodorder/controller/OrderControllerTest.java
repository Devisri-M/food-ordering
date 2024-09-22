package org.foodorder.controller;

import org.foodorder.model.CartItem;
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
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RestaurantService restaurantService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mocking the view resolver to avoid circular view path issues
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/");
        viewResolver.setSuffix(".jsp");

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
        CartItem cartItem = new CartItem(1L, "Pizza", new BigDecimal("10.00"), 2, 1L); // Set a valid price
        when(restaurantService.canPlaceOrder(1L, 2)).thenReturn(true);
        doNothing().when(restaurantService).placeOrder(1L, 2);

        mockMvc.perform(post("/orders/place")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"menuItemId\":1,\"name\":\"Pizza\",\"price\":10.00,\"quantity\":2,\"restaurantId\":1}]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Order placed successfully!"));

        verify(restaurantService, times(1)).placeOrder(1L, 2);
    }

    // Test for failing to place an order when restaurant cannot handle the items
    @Test
    void testPlaceOrder_Failure() throws Exception {
        CartItem cartItem = new CartItem(1L, "Pizza", new BigDecimal("10.00"), 10, 1L); // Set a valid price
        when(restaurantService.canPlaceOrder(1L, 10)).thenReturn(false);

        mockMvc.perform(post("/orders/place")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"menuItemId\":1,\"name\":\"Pizza\",\"price\":10.00,\"quantity\":10,\"restaurantId\":1}]"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Restaurant ID 1 cannot handle the order for 10 items."));

        verify(restaurantService, times(1)).canPlaceOrder(1L, 10);
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
                .andExpect(status().isOk())
                .andExpect(content().string("Failed to dispatch order: Error occurred"));

        verify(restaurantService, times(1)).dispatchOrder(1L, 3);
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