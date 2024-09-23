package org.foodorder.service;

import org.foodorder.entity.CustomerEntity;

/**
 * Service interface for managing customer-related operations such as registration and login.
 * This interface defines the core methods required to register new customers and authenticate
 * existing customers in the system.
 */
public interface CustomerService {

    /**
     * Registers a new customer in the system.
     * This method is responsible for ensuring that the customer does not already exist and
     * securely handles customer data before saving.
     *
     * @param customer The customer entity containing the registration details.
     * @return The saved customer entity.
     * @throws Exception If the customer with the same email already exists or an error occurs during registration.
     */
    CustomerEntity registerCustomer(CustomerEntity customer) throws Exception;

    /**
     * Authenticates an existing customer based on the provided email and password.
     * The method checks whether the customer exists and verifies the provided password
     * against the stored hashed password.
     *
     * @param email    The email of the customer attempting to log in.
     * @param password The password provided by the customer.
     * @return The customer entity if authentication is successful.
     * @throws Exception If the email is not found or the password is incorrect.
     */
    CustomerEntity loginCustomer(String email, String password) throws Exception;
}