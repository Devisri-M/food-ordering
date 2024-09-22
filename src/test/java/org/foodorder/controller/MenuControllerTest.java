package org.foodorder.controller;

import org.foodorder.entity.MenuItemEntity;
import org.foodorder.entity.RestaurantEntity;
import org.foodorder.service.MenuService;
import org.foodorder.service.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MenuControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MenuService menuService;

    @Mock
    private RestaurantService restaurantService;

    @InjectMocks
    private MenuController menuController;

    @Mock
    private Model model;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/");
        viewResolver.setSuffix(".jsp");

        mockMvc = MockMvcBuilders.standaloneSetup(menuController)
                .setViewResolvers(viewResolver)
                .build();
    }

    // Test API: Add Menu Item
    @Test
    void testAddMenuItemViaApi_Success() throws Exception {
        MenuItemEntity menuItem = new MenuItemEntity();
        menuItem.setId(1L);
        menuItem.setName("Pizza");

        when(menuService.addMenuItem(eq(1L), any(MenuItemEntity.class))).thenReturn(menuItem);

        mockMvc.perform(post("/restaurants/1/menu/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Pizza\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Pizza"));

        verify(menuService, times(1)).addMenuItem(eq(1L), any(MenuItemEntity.class));
    }

    // Test API: Get Menu Items
    @Test
    void testGetMenuItemsViaApi_Success() throws Exception {
        List<MenuItemEntity> menuItems = new ArrayList<>();
        MenuItemEntity menuItem = new MenuItemEntity();
        menuItem.setId(1L);
        menuItem.setName("Burger");
        menuItems.add(menuItem);

        when(menuService.getMenuItemsByRestaurant(1L)).thenReturn(menuItems);

        mockMvc.perform(get("/restaurants/1/menu/api"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Burger"));

        verify(menuService, times(1)).getMenuItemsByRestaurant(1L);
    }

    // Test API: Update Menu Item
    @Test
    void testUpdateMenuItemViaApi_Success() throws Exception {
        MenuItemEntity updatedMenuItem = new MenuItemEntity();
        updatedMenuItem.setId(1L);
        updatedMenuItem.setName("Updated Pizza");

        when(menuService.updateMenuItem(eq(1L), eq(1L), any(MenuItemEntity.class))).thenReturn(updatedMenuItem);

        mockMvc.perform(put("/restaurants/1/menu/api/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Pizza\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Pizza"));

        verify(menuService, times(1)).updateMenuItem(eq(1L), eq(1L), any(MenuItemEntity.class));
    }

    // Test API: Delete Menu Item
    @Test
    void testDeleteMenuItemViaApi_Success() throws Exception {
        mockMvc.perform(delete("/restaurants/1/menu/api/1"))
                .andExpect(status().isNoContent());

        verify(menuService, times(1)).deleteMenuItem(1L, 1L);
    }

    // Test UI: Show Add Menu Item Form
    @Test
    void testShowAddMenuItemForm_Success() throws Exception {
        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");

        when(restaurantService.getRestaurantById(1L)).thenReturn(Optional.of(restaurant));

        mockMvc.perform(get("/restaurants/1/menu/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("menu_form"))
                .andExpect(model().attributeExists("restaurant"))
                .andExpect(model().attributeExists("menuItem"));

        verify(restaurantService, times(1)).getRestaurantById(1L);
    }

    // Test UI: Show Edit Menu Item Form
    @Test
    void testShowEditMenuItemForm_Success() throws Exception {
        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");

        MenuItemEntity menuItem = new MenuItemEntity();
        menuItem.setId(1L);
        menuItem.setName("Burger");

        when(restaurantService.getRestaurantById(1L)).thenReturn(Optional.of(restaurant));
        when(menuService.findMenuItemById(1L)).thenReturn(Optional.of(menuItem));

        mockMvc.perform(get("/restaurants/1/menu/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("menu_form"))
                .andExpect(model().attributeExists("restaurant"))
                .andExpect(model().attributeExists("menuItem"));

        verify(restaurantService, times(1)).getRestaurantById(1L);
        verify(menuService, times(1)).findMenuItemById(1L);
    }

    // Test UI: Show Menu for Restaurant
    @Test
    void testShowMenuForRestaurant_Success() throws Exception {
        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");

        List<MenuItemEntity> menuItems = new ArrayList<>();
        MenuItemEntity menuItem = new MenuItemEntity();
        menuItem.setName("Pizza");
        menuItems.add(menuItem);

        when(restaurantService.getRestaurantById(1L)).thenReturn(Optional.of(restaurant));
        when(menuService.getMenuItemsByRestaurant(1L)).thenReturn(menuItems);

        mockMvc.perform(get("/restaurants/1/menu"))
                .andExpect(status().isOk())
                .andExpect(view().name("menu_form"))
                .andExpect(model().attributeExists("restaurant"))
                .andExpect(model().attributeExists("menuItems"))
                .andExpect(model().attributeExists("menuItem"));

        verify(restaurantService, times(1)).getRestaurantById(1L);
        verify(menuService, times(1)).getMenuItemsByRestaurant(1L);
    }

    // Test UI: Delete Menu Item
    @Test
    void testDeleteMenuItem_Success() throws Exception {
        mockMvc.perform(get("/restaurants/1/menu/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/restaurants/1/menu"));

        verify(menuService, times(1)).deleteMenuItem(1L, 1L);
    }
}