package org.foodorder.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service that manages concurrency for restaurant operations by providing an
 * ExecutorService for each restaurant. Each restaurant is assigned a separate
 * ExecutorService to handle orders concurrently, with a pool size limited to
 * the restaurant's maximum processing capacity.
 */
@Service
public class RestaurantExecutorService {

    // A thread-safe map to store ExecutorServices for each restaurant by their ID.
    private final ConcurrentHashMap<Long, ExecutorService> restaurantExecutors = new ConcurrentHashMap<>();

    /**
     * Returns an ExecutorService for the given restaurant ID. If an ExecutorService
     * does not exist for the restaurant, a new one is created with a thread pool size
     * equivalent to the restaurant's maximum processing capacity.
     *
     * @param restaurantId The unique ID of the restaurant.
     * @param maxCapacity  The maximum processing capacity (thread pool size) for the restaurant.
     * @return The ExecutorService associated with the restaurant, which manages concurrency
     *         for processing its orders.
     */
    public ExecutorService getExecutorServiceForRestaurant(Long restaurantId, int maxCapacity) {
        return restaurantExecutors.computeIfAbsent(restaurantId, id -> Executors.newFixedThreadPool(maxCapacity));
    }

    /**
     * Shuts down all ExecutorServices for all restaurants. This method can be
     * used during application shutdown to properly close all ongoing tasks.
     * It ensures that each restaurant's ExecutorService is shut down gracefully.
     */
    public void shutdownAllExecutors() {
        restaurantExecutors.forEach((id, executor) -> executor.shutdown());
    }
}