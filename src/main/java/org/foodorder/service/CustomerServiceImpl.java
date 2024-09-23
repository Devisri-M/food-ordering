package org.foodorder.service;

import org.foodorder.entity.CustomerEntity;
import org.foodorder.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of the CustomerService interface that handles the business logic for customer registration and login.
 * This service interacts with the CustomerRepository to persist and retrieve customer data, ensuring that
 * passwords are securely hashed using the BCrypt algorithm.
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServiceImpl.class);
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Registers a new customer in the system. The method ensures that the customer does not already exist in the database
     * and securely hashes the password before saving the customer entity to the database.
     *
     * @param customer The customer entity containing the registration details.
     * @return The saved customer entity with a hashed password.
     * @throws Exception If the customer with the same email already exists or any other error occurs during registration.
     */
    @Override
    public CustomerEntity registerCustomer(CustomerEntity customer) throws Exception {
        LOGGER.info("Attempting to register customer with email: {}", customer.getEmail());

        // Check if the email is already registered
        if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            LOGGER.error("Customer with email {} already exists", customer.getEmail());
            throw new Exception("Customer with email already exists.");
        }

        // Hash the customer's password before saving
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        CustomerEntity savedCustomer = customerRepository.save(customer);
        LOGGER.info("Successfully registered customer with email: {}", customer.getEmail());
        return savedCustomer;
    }

    /**
     * Authenticates a customer by verifying the email and password combination.
     * If the customer exists and the password matches, the method returns the customer entity.
     *
     * @param email    The email of the customer attempting to log in.
     * @param password The password provided by the customer.
     * @return The customer entity if authentication is successful.
     * @throws Exception If the customer is not found or the password does not match.
     */
    @Override
    public CustomerEntity loginCustomer(String email, String password) throws Exception {
        LOGGER.info("Attempting to login customer with email: {}", email);

        // Find the customer by email
        Optional<CustomerEntity> optionalCustomer = customerRepository.findByEmail(email);

        // If the customer is found, verify the password
        if (optionalCustomer.isPresent()) {
            CustomerEntity customer = optionalCustomer.get();
            if (passwordEncoder.matches(password, customer.getPassword())) {
                LOGGER.info("Customer with email {} logged in successfully", email);
                return customer;
            } else {
                LOGGER.error("Invalid password for customer with email: {}", email);
                throw new Exception("Invalid credentials.");
            }
        } else {
            LOGGER.error("No customer found with email: {}", email);
            throw new Exception("No customer found with provided email.");
        }
    }
}