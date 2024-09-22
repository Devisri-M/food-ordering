package org.foodorder.controller;

import org.foodorder.model.CartItem;
import org.foodorder.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller responsible for managing cart-related actions such as adding, viewing,
 * incrementing, decrementing, and clearing cart items.
 */
@Controller
@RequestMapping("/cart")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private CartService cartService;

    /**
     * Add an item to the cart. The item is identified by both its menuItemId and restaurantId.
     * If the item already exists in the cart, the quantity is incremented.
     *
     * @param menuItemId  the menu item ID
     * @param name        the menu item name
     * @param price       the menu item price
     * @param quantity    the quantity of the item, defaults to 1 if not provided
     * @param restaurantId the ID of the restaurant
     * @param model       the Spring model to hold any messages or errors
     * @return the cart view page after adding the item or redirects back to the search page on error
     */
    @PostMapping("/add")
    public String addToCart(@RequestParam Long menuItemId,
                            @RequestParam String name,
                            @RequestParam BigDecimal price,
                            @RequestParam(defaultValue = "1") int quantity,
                            @RequestParam Long restaurantId,
                            Model model) {
        try {
            logger.info("Adding item {} with ID {} from restaurant ID {} to the cart, quantity: {}", name, menuItemId, restaurantId, quantity);
            cartService.addToCart(menuItemId, name, price, quantity, restaurantId);
            model.addAttribute("message", "Item added to cart successfully!");
            return "redirect:/cart/view"; // Redirect to the cart view page after adding
        } catch (Exception e) {
            logger.error("Error while adding item to the cart", e);
            model.addAttribute("error", "An error occurred while adding the item to the cart.");
            return "redirect:/search/menu";  // Redirect back to the search page in case of an error
        }
    }

    /**
     * Remove an item from the cart by its ID and restaurantId.
     *
     * @param menuItemId   the ID of the menu item to remove
     * @param restaurantId the ID of the restaurant to which the item belongs
     * @return the cart view after item removal or an error message
     */
    @PostMapping("/remove/{menuItemId}")
    public String removeFromCart(@PathVariable Long menuItemId, @RequestParam Long restaurantId) {
        try {
            logger.info("Removing item with ID {} from restaurant ID {} from the cart", menuItemId, restaurantId);
            cartService.removeFromCart(menuItemId, restaurantId);
            return "redirect:/cart/view";  // Redirect to the cart view after successful removal
        } catch (Exception e) {
            logger.error("Error while removing item from the cart", e);
            return "redirect:/cart/view?error=Unable to remove item";  // Redirect with an error message
        }
    }

    /**
     * Clear all items from the cart.
     *
     * @param model the Spring model to hold messages or errors
     * @return redirect to the cart view page after clearing the cart or return an error message
     */
    @PostMapping("/clear")
    public String clearCart(Model model) {
        try {
            logger.info("Clearing the cart");
            cartService.clearCart();
            model.addAttribute("message", "Cart cleared successfully.");
            return "redirect:/cart/view";  // Redirect to the cart view after clearing the cart
        } catch (Exception e) {
            logger.error("Error while clearing the cart", e);
            model.addAttribute("error", "An error occurred while clearing the cart.");
            return "cart_view";  // Return the view in case of an error
        }
    }

    /**
     * View all items in the cart.
     *
     * @param model the Spring model to hold cart items and total price
     * @return the view name for displaying cart items and the total price
     */
    @GetMapping("/view")
    public String viewCart(Model model) {
        try {
            logger.info("Viewing cart items");
            List<CartItem> cartItems = cartService.getCartItems();
            BigDecimal total = cartService.calculateTotal();
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("total", total);
            return "cart_view";  // Thymeleaf view for displaying cart items
        } catch (Exception e) {
            logger.error("Error while viewing the cart", e);
            model.addAttribute("error", "An error occurred while fetching the cart items.");
            return "cart_view";  // Return the view with an error message
        }
    }

    /**
     * Increment the quantity of a cart item.
     *
     * @param menuItemId   the menu item ID whose quantity is to be increased
     * @param restaurantId the restaurant ID associated with the item
     * @param model        the Spring model to hold any error messages
     * @return redirect to the cart view page after quantity is incremented
     */
    @PostMapping("/increment")
    public String incrementQuantity(@RequestParam Long menuItemId, @RequestParam Long restaurantId, Model model) {
        try {
            logger.info("Incrementing quantity for menu item ID {} from restaurant ID {}", menuItemId, restaurantId);
            cartService.incrementQuantity(menuItemId, restaurantId);
            return "redirect:/cart/view";  // Redirect to the cart view after incrementing quantity
        } catch (Exception e) {
            logger.error("Error while incrementing quantity for menu item ID {} from restaurant ID {}", menuItemId, restaurantId, e);
            model.addAttribute("error", "An error occurred while updating the quantity.");
            return "cart_view";  // Return to cart view in case of an error
        }
    }

    /**
     * Decrement the quantity of a cart item.
     *
     * @param menuItemId   the menu item ID whose quantity is to be decreased
     * @param restaurantId the restaurant ID associated with the item
     * @param model        the Spring model to hold any error messages
     * @return redirect to the cart view page after quantity is decremented
     */
    @PostMapping("/decrement")
    public String decrementQuantity(@RequestParam Long menuItemId, @RequestParam Long restaurantId, Model model) {
        try {
            logger.info("Decrementing quantity for menu item ID {} from restaurant ID {}", menuItemId, restaurantId);
            cartService.decrementQuantity(menuItemId, restaurantId);
            return "redirect:/cart/view";  // Redirect to the cart view after decrementing quantity
        } catch (Exception e) {
            logger.error("Error while decrementing quantity for menu item ID {} from restaurant ID {}", menuItemId, restaurantId, e);
            model.addAttribute("error", "An error occurred while updating the quantity.");
            return "cart_view";  // Return to cart view in case of an error
        }
    }
}