package org.foodorder.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foodorder.entity.MenuItemEntity;
import org.foodorder.entity.RestaurantEntity;
import org.foodorder.service.MenuService;
import org.foodorder.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller to manage Menu-related operations.
 */
@Controller
@RequestMapping("/restaurants/{restaurantId}/menu")
@Tag(name = "Menu", description = "Menu management APIs for restaurants")
public class MenuController {

    private static final Logger LOGGER = Logger.getLogger(MenuController.class.getName());
    private static final String ERROR = "error";
    private static final String RESTUARENT_MENU = "restaurant_menu";
    private static final String MENU_FORM = "menu_form";
    private static final String REDIRECT_RESTUARENTS = "redirect:/restaurants/";
    private static final String MENU = "/menu";

    @Autowired
    private MenuService menuService;

    @Autowired
    private RestaurantService restaurantService;

    /**
     * Adds a new menu item for a restaurant via API.
     *
     * @param restaurantId The ID of the restaurant
     * @param menuItem The menu item details to add
     * @return ResponseEntity with the created menu item or error message
     */
    @PostMapping("/api")
    @Operation(summary = "Add menu item", description = "Add a new menu item to a restaurant via API", tags = {"create", "post"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Menu item created", content = @Content(schema = @Schema(implementation = MenuItemEntity.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<MenuItemEntity> addMenuItemViaApi(@PathVariable Long restaurantId, @RequestBody MenuItemEntity menuItem) {
        try {
            LOGGER.info("Adding a new menu item to restaurant ID: " + restaurantId);
            menuItem.setRestaurantId(restaurantId);
            MenuItemEntity createdMenuItem = menuService.addMenuItem(restaurantId, menuItem);
            return ResponseEntity.status(201).body(createdMenuItem);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding menu item to restaurant ID: " + restaurantId, e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Retrieves all menu items for a restaurant via API.
     *
     * @param restaurantId The ID of the restaurant
     * @return List of menu items or an error message
     */
    @GetMapping("/api")
    @Operation(summary = "Get menu items", description = "Retrieve all menu items for a specific restaurant via API", tags = {"get"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Menu items retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<List<MenuItemEntity>> getMenuItemsViaApi(@PathVariable Long restaurantId) {
        try {
            LOGGER.info("Fetching menu items for restaurant ID: " + restaurantId);
            List<MenuItemEntity> menuItems = menuService.getMenuItemsByRestaurant(restaurantId);
            return ResponseEntity.ok(menuItems);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching menu items for restaurant ID: " + restaurantId, e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Updates an existing menu item for a restaurant via API.
     *
     * @param restaurantId The ID of the restaurant
     * @param menuItemId The ID of the menu item to update
     * @param updatedMenuItem The updated details of the menu item
     * @return The updated menu item or an error message
     */
    @PutMapping("/api/{menuItemId}")
    @Operation(summary = "Update menu item", description = "Update an existing menu item for a restaurant via API", tags = {"update", "put"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Menu item updated successfully"),
            @ApiResponse(responseCode = "404", description = "Menu item or restaurant not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<MenuItemEntity> updateMenuItemViaApi(@PathVariable Long restaurantId, @PathVariable Long menuItemId, @RequestBody MenuItemEntity updatedMenuItem) {
        try {
            LOGGER.info("Updating menu item ID: " + menuItemId + " for restaurant ID: " + restaurantId);
            MenuItemEntity updatedItem = menuService.updateMenuItem(restaurantId, menuItemId, updatedMenuItem);
            return ResponseEntity.ok(updatedItem);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating menu item for restaurant ID: " + restaurantId, e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Deletes a menu item for a restaurant via API.
     *
     * @param restaurantId The ID of the restaurant
     * @param menuItemId The ID of the menu item to delete
     * @return ResponseEntity with status 204 or error
     */
    @DeleteMapping("/api/{menuItemId}")
    @Operation(summary = "Delete menu item", description = "Delete a menu item from the restaurant's menu via API", tags = {"delete", "delete"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Menu item deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Menu item or restaurant not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<Void> deleteMenuItemViaApi(@PathVariable Long restaurantId, @PathVariable Long menuItemId) {
        try {
            LOGGER.info("Deleting menu item ID: " + menuItemId + " for restaurant ID: " + restaurantId);
            menuService.deleteMenuItem(restaurantId, menuItemId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting menu item for restaurant ID: " + restaurantId, e);
            return ResponseEntity.status(500).build();
        }
    }

    // ===== UI METHODS (Thymeleaf support) =====

    /**
     * Displays the form for creating a new menu item (UI).
     *
     * @param restaurantId the ID of the restaurant
     * @param model the model to hold form data
     * @return the Thymeleaf view for the menu item form
     */
    @GetMapping("/new")
    public String showAddMenuItemForm(@PathVariable Long restaurantId, Model model) {
        try {
            RestaurantEntity restaurant = restaurantService.getRestaurantById(restaurantId)
                    .orElseThrow(() -> new RuntimeException("Restaurant not found"));

            model.addAttribute("restaurant", restaurant);
            model.addAttribute("menuItem", new MenuItemEntity()); // Add new empty menu item object
            return MENU_FORM;  // Thymeleaf view
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching restaurant with ID: " + restaurantId, e);
            model.addAttribute("message", "Unable to load restaurant details.");
            return ERROR; // Redirect to an error page in case of failure
        }
    }

    /**
     * Displays the form for editing an existing menu item (UI).
     *
     * @param restaurantId the ID of the restaurant
     * @param menuItemId the ID of the menu item to be edited
     * @param model the model to hold form data
     * @return the Thymeleaf view for the menu item form
     */
    @GetMapping("/{menuItemId}")
    public String showEditMenuItemForm(@PathVariable Long restaurantId, @PathVariable Long menuItemId, Model model) {
        try {
            RestaurantEntity restaurant = restaurantService.getRestaurantById(restaurantId)
                    .orElseThrow(() -> new RuntimeException("Restaurant not found"));

            MenuItemEntity menuItem = menuService.findMenuItemById(menuItemId)
                    .orElseThrow(() -> new RuntimeException("Menu item not found"));

            model.addAttribute("restaurant", restaurant);
            model.addAttribute("menuItem", menuItem);  // Add the menu item to be edited
            return MENU_FORM;  // Thymeleaf view for editing
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching restaurant or menu item with IDs: " + restaurantId + ", " + menuItemId, e);
            model.addAttribute("message", "Unable to load restaurant or menu item details.");
            return ERROR;  // Redirect to an error page in case of failure
        }
    }

    /**
     * Saves or updates a menu item (for UI form submission).
     *
     * @param restaurantId the ID of the restaurant
     * @param menuItem the menu item to save or update
     * @return redirect to the menu list after saving
     */
    @PostMapping("/save")
    public String saveMenuItem(@PathVariable Long restaurantId, @ModelAttribute MenuItemEntity menuItem) {
        try {
            if (menuItem.getId() != null) {
                // If the ID is not null, update the existing menu item
                menuService.updateMenuItem(restaurantId, menuItem.getId(), menuItem);
                LOGGER.info("Updated menu item ID: " + menuItem.getId() + " for restaurant ID: " + restaurantId);
            } else {
                // Otherwise, create a new menu item
                menuService.addMenuItem(restaurantId, menuItem);
                LOGGER.info("Created new menu item for restaurant ID: " + restaurantId);
            }
            return REDIRECT_RESTUARENTS + restaurantId + MENU;  // Redirect to the menu list after saving
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error saving menu item for restaurant ID: " + restaurantId, e);
            return ERROR;  // Redirect to an error page in case of failure
        }
    }

    /**
     * Deletes a menu item (UI).
     *
     * @param restaurantId the ID of the restaurant
     * @param menuItemId the ID of the menu item to delete
     * @return redirect to the menu page after deletion
     */
    @GetMapping("/delete/{menuItemId}")
    public String deleteMenuItem(@PathVariable Long restaurantId, @PathVariable Long menuItemId) {
        try {
            menuService.deleteMenuItem(restaurantId, menuItemId);
            LOGGER.info("Deleted menu item ID: " + menuItemId + " for restaurant ID: " + restaurantId);
            return REDIRECT_RESTUARENTS + restaurantId + MENU;  // Redirect to the menu page after deletion
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting menu item ID: " + menuItemId + " for restaurant ID: " + restaurantId, e);
            return ERROR;  // Redirect to an error page in case of failure
        }
    }

    /**
     * Shows the menu for a restaurant (UI).
     *
     * @param restaurantId the ID of the restaurant
     * @param model the model to hold the list of menu items
     * @return the Thymeleaf view for showing the menu
     */
    @GetMapping
    public String showMenuForRestaurant(@PathVariable Long restaurantId, Model model) {
        try {
            RestaurantEntity restaurant = restaurantService.getRestaurantById(restaurantId)
                    .orElseThrow(() -> new RuntimeException("Restaurant not found"));

            List<MenuItemEntity> menuItems = menuService.getMenuItemsByRestaurant(restaurantId);

            model.addAttribute("restaurant", restaurant);
            model.addAttribute("menuItems", menuItems);  // List of menu items to display
            model.addAttribute("menuItem", new MenuItemEntity());  // Add a blank menu item object to the model
            return MENU_FORM;  // Thymeleaf view for showing the menu
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching menu items for restaurant ID: " + restaurantId, e);
            model.addAttribute("message", "Unable to load menu items.");
            return ERROR;  // Redirect to an error page in case of failure
        }
    }

    /**
     * Displays menu items for a restaurant with options to add to the cart.
     *
     * @param restaurantId the restaurant ID
     * @param model the Spring model
     * @return the restaurant menu page
     */
    @GetMapping("/view-menu")
    public String showMenuForRestaurantCard(@PathVariable Long restaurantId, Model model) {
        try {
            LOGGER.info("Fetching menu items for restaurant ID: " + restaurantId);
            RestaurantEntity restaurant = restaurantService.getRestaurantById(restaurantId)
                    .orElseThrow(() -> new RuntimeException("Restaurant not found"));

            List<MenuItemEntity> menuItems = menuService.getMenuItemsByRestaurant(restaurantId);

            model.addAttribute("restaurant", restaurant);
            model.addAttribute("menuItems", menuItems);

            return RESTUARENT_MENU; // The new Thymeleaf template for viewing menu and adding to cart
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error while fetching menu items for restaurant ID: " + restaurantId, e);
            model.addAttribute("message", "Unable to fetch menu items.");
            return ERROR; // Return an error page if something goes wrong
        }
    }
}