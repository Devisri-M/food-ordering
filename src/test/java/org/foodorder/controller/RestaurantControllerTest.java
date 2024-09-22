package org.foodorder.controller;

import org.foodorder.entity.RestaurantEntity;
import org.foodorder.service.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RestaurantControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RestaurantService restaurantService;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private RestaurantController restaurantController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mocking the view resolver to avoid circular view path issues
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/");
        viewResolver.setSuffix(".jsp");

        mockMvc = MockMvcBuilders.standaloneSetup(restaurantController)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    void testGetAllRestaurants() throws Exception {
        List<RestaurantEntity> restaurantList = new ArrayList<>();
        restaurantList.add(new RestaurantEntity());

        when(restaurantService.findAll()).thenReturn(restaurantList);

        mockMvc.perform(get("/restaurants"))
                .andExpect(status().isOk())
                .andExpect(view().name("restaurants"))
                .andExpect(model().attributeExists("restaurants"));

        verify(restaurantService, times(1)).findAll();
    }

    @Test
    void testAddRestaurantForm() throws Exception {
        mockMvc.perform(get("/restaurants/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("restaurant_form"))
                .andExpect(model().attributeExists("restaurant"))
                .andExpect(model().attributeExists("pageTitle"));
    }

    @Test
    void testSaveRestaurant_Success() throws Exception {
        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setName("Test Restaurant");

        mockMvc.perform(post("/restaurants/save")
                        .flashAttr("restaurant", restaurant))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/restaurants"));

        verify(restaurantService, times(1)).save(any(RestaurantEntity.class));
    }

    @Test
    void testEditRestaurant() throws Exception {
        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");

        when(restaurantService.findById(1L)).thenReturn(Optional.of(restaurant));

        mockMvc.perform(get("/restaurants/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("restaurant_form"))
                .andExpect(model().attributeExists("restaurant"))
                .andExpect(model().attribute("pageTitle", "Edit Restaurant (ID: 1)"));

        verify(restaurantService, times(1)).findById(1L);
    }

    @Test
    void testEditRestaurant_NotFound() throws Exception {
        when(restaurantService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/restaurants/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/restaurants"));

        verify(restaurantService, times(1)).findById(1L);
    }

    @Test
    void testDeleteRestaurant_Success() throws Exception {
        mockMvc.perform(get("/restaurants/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/restaurants"));

        verify(restaurantService, times(1)).deleteById(1L);
    }

    @Test
    void testUpdateRestaurantIsOpenStatus() throws Exception {
        mockMvc.perform(get("/restaurants/1/isOpen/true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/restaurants"));

        verify(restaurantService, times(1)).updateRestaurantIsOpenStatus(1L, true);
    }
}