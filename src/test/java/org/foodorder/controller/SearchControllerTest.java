package org.foodorder.controller;

import org.foodorder.entity.MenuItemEntity;
import org.foodorder.entity.RestaurantEntity;
import org.foodorder.service.MenuService;
import org.foodorder.service.RestaurantSelectorService;
import org.foodorder.service.RestaurantService;
import org.foodorder.strategy.HighestRatingStrategy;
import org.foodorder.strategy.LowestCostStrategy;
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
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SearchControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MenuService menuService;

    @Mock
    private RestaurantService restaurantService;

    @Mock
    private RestaurantSelectorService restaurantSelectorService;

    @Mock
    private HighestRatingStrategy highestRatingStrategy;

    @Mock
    private LowestCostStrategy lowestCostStrategy;

    @InjectMocks
    private SearchController searchController;

    @Mock
    private Model model;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/");
        viewResolver.setSuffix(".jsp");

        mockMvc = MockMvcBuilders.standaloneSetup(searchController)
                .setViewResolvers(viewResolver)
                .build();
    }

    // Test for a successful search with results sorted by rating
    @Test
    void testSearchRestaurantsByItem_RatingSort() throws Exception {
        List<MenuItemEntity> menuItems = new ArrayList<>();
        MenuItemEntity menuItem = new MenuItemEntity();
        menuItem.setId(1L);
        menuItem.setName("Pizza");
        menuItems.add(menuItem);

        List<RestaurantEntity> restaurants = new ArrayList<>();
        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");
        restaurant.setMenuItems(menuItems);
        restaurant.setRating(BigDecimal.valueOf(4.5));
        restaurants.add(restaurant);

        when(menuService.searchMenuItems("Pizza")).thenReturn(menuItems);
        when(restaurantService.findRestaurantsByMenuItems(menuItems)).thenReturn(restaurants);

        mockMvc.perform(get("/search/menu")
                        .param("keyword", "Pizza")
                        .param("sort", "rating"))
                .andExpect(status().isOk())
                .andExpect(view().name("search_results"))
                .andExpect(model().attributeExists("restaurants"))
                .andExpect(model().attribute("keyword", "Pizza"));

        verify(menuService, times(1)).searchMenuItems("Pizza");
        verify(restaurantService, times(1)).findRestaurantsByMenuItems(menuItems);
    }

    // Test for a successful search with results sorted by price
    @Test
    void testSearchRestaurantsByItem_PriceSort() throws Exception {
        List<MenuItemEntity> menuItems = new ArrayList<>();
        MenuItemEntity menuItem = new MenuItemEntity();
        menuItem.setId(1L);
        menuItem.setName("Pizza");
        menuItem.setPrice(BigDecimal.valueOf(9.99));
        menuItems.add(menuItem);

        List<RestaurantEntity> restaurants = new ArrayList<>();
        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");
        restaurant.setMenuItems(menuItems);
        restaurants.add(restaurant);

        when(menuService.searchMenuItems("Pizza")).thenReturn(menuItems);
        when(restaurantService.findRestaurantsByMenuItems(menuItems)).thenReturn(restaurants);

        mockMvc.perform(get("/search/menu")
                        .param("keyword", "Pizza")
                        .param("sort", "price"))
                .andExpect(status().isOk())
                .andExpect(view().name("search_results"))
                .andExpect(model().attributeExists("restaurants"))
                .andExpect(model().attribute("keyword", "Pizza"));

        verify(menuService, times(1)).searchMenuItems("Pizza");
        verify(restaurantService, times(1)).findRestaurantsByMenuItems(menuItems);
    }

    // Test for no results found scenario
    @Test
    void testSearchRestaurantsByItem_NoResults() throws Exception {
        when(menuService.searchMenuItems("Burger")).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/search/menu")
                        .param("keyword", "Burger"))
                .andExpect(status().isOk())
                .andExpect(view().name("search_results"))
                .andExpect(model().attribute("message", "No restaurants found offering the item: Burger"));

        verify(menuService, times(1)).searchMenuItems("Burger");
        verify(restaurantService, never()).findRestaurantsByMenuItems(anyList());
    }

    // Test for internal server error during search
    @Test
    void testSearchRestaurantsByItem_Error() throws Exception {
        when(menuService.searchMenuItems("Pasta")).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/search/menu")
                        .param("keyword", "Pasta"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("message", "An error occurred while searching for restaurants."));

        verify(menuService, times(1)).searchMenuItems("Pasta");
        verify(restaurantService, never()).findRestaurantsByMenuItems(anyList());
    }
}