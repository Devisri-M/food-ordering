package org.foodorder.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foodorder.entity.RestaurantEntity;
import org.foodorder.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Tag(name = "Restaurant", description = "Restaurant management APIs")
public class RestaurantController {

  private static final Logger LOGGER = Logger.getLogger(RestaurantController.class.getName());

  @Autowired
  private RestaurantService restaurantService;

  /**
   * Retrieve a list of all restaurants or search by name (case-insensitive).
   *
   * @param model the Spring model
   * @param keyword the optional keyword for searching by restaurant name
   * @return the view name to display the list of restaurants
   */
  @GetMapping("/restaurants")
  @Operation(summary = "Get all restaurants", description = "Retrieve all restaurants or search by name", tags = {"findAll", "get"})
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Restaurants retrieved successfully", content = @Content(schema = @Schema(implementation = RestaurantEntity.class))),
          @ApiResponse(responseCode = "400", description = "Invalid request"),
          @ApiResponse(responseCode = "500", description = "Internal Server Error")
  })
  public String getAll(Model model, @Param("keyword") String keyword) {
    try {
      List<RestaurantEntity> restaurants = new ArrayList<>();
      if (keyword == null) {
        restaurants.addAll(restaurantService.findAll());
      } else {
        restaurants.addAll(restaurantService.findByNameContainingIgnoreCase(keyword));
        model.addAttribute("keyword", keyword);
      }

      model.addAttribute("restaurants", restaurants);
      LOGGER.info("Restaurants retrieved successfully");
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error retrieving restaurants", e);
      model.addAttribute("message", "Error retrieving restaurants: " + e.getMessage());
    }

    return "restaurants";
  }

  /**
   * Display the form for adding a new restaurant.
   *
   * @param model the Spring model
   * @return the view name for the restaurant creation form
   */
  @GetMapping("/restaurants/new")
  public String addRestaurant(Model model) {
    RestaurantEntity restaurant = new RestaurantEntity();
    restaurant.setIsOpen(true);

    model.addAttribute("restaurant", restaurant);
    model.addAttribute("pageTitle", "Create new Restaurant");
    LOGGER.info("Displaying form to create a new restaurant");

    return "restaurant_form";
  }

  /**
   * Save or update a restaurant.
   *
   * @param restaurant the restaurant entity to be saved
   * @param redirectAttributes redirect attributes to store feedback messages
   * @return a redirect to the restaurant list
   */
  @PostMapping("/restaurants/save")
  @Operation(summary = "Register a new restaurant", description = "Registers or updates a restaurant in the system", tags = {"register", "post"})
  public String saveRestaurant(RestaurantEntity restaurant, RedirectAttributes redirectAttributes) {
    try {
      restaurantService.save(restaurant);
      redirectAttributes.addFlashAttribute("message", "The Restaurant has been saved successfully!");
      LOGGER.info("Restaurant saved successfully: " + restaurant.getName());
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error saving restaurant", e);
      redirectAttributes.addFlashAttribute("message", "Error saving restaurant: " + e.getMessage());
    }

    return "redirect:/restaurants";
  }

  /**
   * Display the form for editing an existing restaurant.
   *
   * @param id the ID of the restaurant to edit
   * @param model the Spring model
   * @param redirectAttributes redirect attributes for feedback messages
   * @return the view name for the restaurant edit form
   */
  @GetMapping("/restaurants/{id}")
  public String editRestaurant(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
    try {
      Optional<RestaurantEntity> restaurantOpt = restaurantService.findById(id);
      if (restaurantOpt.isPresent()) {
        model.addAttribute("restaurant", restaurantOpt.get());
        model.addAttribute("pageTitle", "Edit Restaurant (ID: " + id + ")");
        LOGGER.info("Displaying form to edit restaurant ID: " + id);
        return "restaurant_form";
      } else {
        redirectAttributes.addFlashAttribute("message", "Restaurant not found.");
        return "redirect:/restaurants";
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error retrieving restaurant for editing", e);
      redirectAttributes.addFlashAttribute("message", "Error retrieving restaurant: " + e.getMessage());
      return "redirect:/restaurants";
    }
  }

  /**
   * Delete a restaurant by ID.
   *
   * @param id the ID of the restaurant to delete
   * @param redirectAttributes redirect attributes for feedback messages
   * @return a redirect to the restaurant list
   */
  @GetMapping("/restaurants/delete/{id}")
  public String deleteRestaurant(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
    try {
      restaurantService.deleteById(id);
      redirectAttributes.addFlashAttribute("message", "The Restaurant with id=" + id + " has been deleted successfully!");
      LOGGER.info("Restaurant with ID: " + id + " deleted successfully");
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error deleting restaurant", e);
      redirectAttributes.addFlashAttribute("message", "Error deleting restaurant: " + e.getMessage());
    }

    return "redirect:/restaurants";
  }

  /**
   * Update the open/closed status of a restaurant.
   *
   * @param id the ID of the restaurant
   * @param isOpen the new open status
   * @param redirectAttributes redirect attributes for feedback messages
   * @return a redirect to the restaurant list
   */
  @GetMapping("/restaurants/{id}/isOpen/{isOpen}")
  public String updateRestaurantIsOpenStatus(@PathVariable("id") Long id, @PathVariable("isOpen") boolean isOpen,
                                             RedirectAttributes redirectAttributes) {
    try {
      restaurantService.updateRestaurantIsOpenStatus(id, isOpen);
      String openStatus = isOpen ? "opened" : "closed";
      redirectAttributes.addFlashAttribute("message", "The Restaurant id=" + id + " has been " + openStatus);
      LOGGER.info("Restaurant with ID: " + id + " has been " + openStatus);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error updating restaurant open status", e);
      redirectAttributes.addFlashAttribute("message", "Error updating restaurant status: " + e.getMessage());
    }

    return "redirect:/restaurants";
  }
}