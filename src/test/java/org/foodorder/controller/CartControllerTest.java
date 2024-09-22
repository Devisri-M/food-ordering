package org.foodorder.controller;

import org.foodorder.model.CartItem;
import org.foodorder.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CartControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CartService cartService;

    @Mock
    private Model model;

    @InjectMocks
    private CartController cartController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mocking the view resolver to avoid circular view path issues
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/");
        viewResolver.setSuffix(".jsp");

        mockMvc = MockMvcBuilders.standaloneSetup(cartController)
                .setViewResolvers(viewResolver)
                .build();
    }

    // Test for adding an item to the cart
    @Test
    void testAddToCart() throws Exception {
        mockMvc.perform(post("/cart/add")
                        .param("menuItemId", "1")
                        .param("name", "Pizza")
                        .param("price", "12.99")
                        .param("quantity", "2")
                        .param("restaurantId", "1001"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart/view"));

        verify(cartService, times(1)).addToCart(1L, "Pizza", new BigDecimal("12.99"), 2, 1001L);
    }

    // Test for removing an item from the cart
    @Test
    void testRemoveFromCart() throws Exception {
        mockMvc.perform(post("/cart/remove/1")
                        .param("restaurantId", "1001"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart/view"));

        verify(cartService, times(1)).removeFromCart(1L, 1001L);
    }

    // Test for clearing the cart
    @Test
    void testClearCart() throws Exception {
        mockMvc.perform(post("/cart/clear"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart/view"));

        verify(cartService, times(1)).clearCart();
    }

    // Test for viewing the cart
    @Test
    void testViewCart() throws Exception {
        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(new CartItem(1L, "Pizza", new BigDecimal("12.99"), 2, 1001L));

        when(cartService.getCartItems()).thenReturn(cartItems);
        when(cartService.calculateTotal()).thenReturn(new BigDecimal("25.98"));

        mockMvc.perform(get("/cart/view"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart_view"))
                .andExpect(model().attributeExists("cartItems"))
                .andExpect(model().attributeExists("total"));

        verify(cartService, times(1)).getCartItems();
        verify(cartService, times(1)).calculateTotal();
    }

    // Test for incrementing the quantity of a cart item
    @Test
    void testIncrementQuantity() throws Exception {
        mockMvc.perform(post("/cart/increment")
                        .param("menuItemId", "1")
                        .param("restaurantId", "1001"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart/view"));

        verify(cartService, times(1)).incrementQuantity(1L, 1001L);
    }

    // Test for decrementing the quantity of a cart item
    @Test
    void testDecrementQuantity() throws Exception {
        mockMvc.perform(post("/cart/decrement")
                        .param("menuItemId", "1")
                        .param("restaurantId", "1001"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart/view"));

        verify(cartService, times(1)).decrementQuantity(1L, 1001L);
    }

    // Test for handling an error while adding to the cart
    @Test
    void testAddToCart_Error() throws Exception {
        doThrow(new RuntimeException("Error adding to cart")).when(cartService).addToCart(anyLong(), anyString(), any(BigDecimal.class), anyInt(), anyLong());

        mockMvc.perform(post("/cart/add")
                        .param("menuItemId", "1")
                        .param("name", "Pizza")
                        .param("price", "12.99")
                        .param("quantity", "2")
                        .param("restaurantId", "1001"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/search/menu"));

        verify(cartService, times(1)).addToCart(anyLong(), anyString(), any(BigDecimal.class), anyInt(), anyLong());
    }
}