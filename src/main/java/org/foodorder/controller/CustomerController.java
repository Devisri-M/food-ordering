package org.foodorder.controller;

import org.foodorder.entity.CustomerEntity;
import org.foodorder.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private static final Logger LOGGER = Logger.getLogger(CustomerController.class.getName());

    @Autowired
    private CustomerService customerService;

    /**
     * Endpoint for customer registration.
     *
     * @param customer The customer details for registration.
     * @return ResponseEntity containing success or failure message.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerCustomer(@RequestBody CustomerEntity customer) {
        try {
            LOGGER.log(Level.INFO, "Registering new customer with email: {0}", customer.getEmail());
            CustomerEntity registeredCustomer = customerService.registerCustomer(customer);
            return ResponseEntity.ok(Map.of("message", "Customer registered successfully!", "customerId", registeredCustomer.getId()));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error occurred during customer registration: {0}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("message", "Failed to register customer: " + e.getMessage()));
        }
    }

    /**
     * Endpoint for customer login.
     *
     * @param loginDetails A map containing 'email' and 'password'.
     * @return ResponseEntity containing success or failure message.
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginCustomer(@RequestBody Map<String, String> loginDetails) {
        try {
            String email = loginDetails.get("email");
            String password = loginDetails.get("password");

            LOGGER.log(Level.INFO, "Customer login attempt with email: {0}", email);
            CustomerEntity loggedInCustomer = customerService.loginCustomer(email, password);
            return ResponseEntity.ok(Map.of("message", "Login successful!", "customerId", loggedInCustomer.getId()));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error occurred during customer login: {0}", e.getMessage());
            return ResponseEntity.status(401).body(Map.of("message", "Login failed: " + e.getMessage()));
        }
    }
}