package org.foodorder.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity representing a restaurant, including its details, current processing capacity,
 * and menu items. This entity is responsible for managing the restaurant's ability to
 * accept and process orders based on its capacity.
 */
@Entity
@Table(name = "restaurant")
@NoArgsConstructor
@ToString
@Getter
@Setter
public class RestaurantEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String address;

  @Column(nullable = false)
  private String city;

  @Column(nullable = false)
  private String state;

  @Column(nullable = false, name = "zip_code")
  private String zipCode;

  @Column(name = "cuisine_type")
  private String cuisineType;

  @Column(precision = 3, scale = 2)
  private BigDecimal rating;

  @Column(name = "opening_hours")
  private String openingHours;

  @Column(name = "phone_number")
  private String phoneNumber;

  @Column(nullable = false)
  private String website;

  @Column(name = "is_open", nullable = false)
  private Boolean isOpen = true;

  @Column(nullable = false)
  private int maxCapacity; // Maximum processing capacity of the restaurant

  @Column(nullable = false)
  private int currentProcessingLoad = 0; // Current load of processing orders

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<MenuItemEntity> menuItems = new ArrayList<>();

  /**
   * Constructor to initialize a restaurant entity with specific values.
   *
   * @param name                  the name of the restaurant
   * @param address               the address of the restaurant
   * @param city                  the city of the restaurant
   * @param state                 the state of the restaurant
   * @param zipCode               the ZIP code of the restaurant
   * @param cuisineType           the type of cuisine the restaurant offers
   * @param rating                the rating of the restaurant
   * @param openingHours          the opening hours of the restaurant
   * @param phoneNumber           the phone number of the restaurant
   * @param website               the website of the restaurant
   * @param isOpen                whether the restaurant is open
   * @param maxCapacity           the maximum processing capacity of the restaurant
   * @param currentProcessingLoad the current processing load
   */
  public RestaurantEntity(String name, String address, String city, String state, String zipCode, String cuisineType,
                          BigDecimal rating, String openingHours, String phoneNumber, String website, Boolean isOpen,
                          Integer maxCapacity, int currentProcessingLoad) {
    this.name = name;
    this.address = address;
    this.city = city;
    this.state = state;
    this.zipCode = zipCode;
    this.cuisineType = cuisineType;
    this.rating = rating;
    this.openingHours = openingHours;
    this.phoneNumber = phoneNumber;
    this.website = website;
    this.isOpen = isOpen;
    this.maxCapacity = maxCapacity;
    this.currentProcessingLoad = currentProcessingLoad;
  }

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  /**
   * Checks if the restaurant can accept a new order based on the current processing load
   * and maximum capacity.
   *
   * @param itemCount the number of items in the order
   * @return true if the restaurant can accept the order, false otherwise
   */
  public boolean canAcceptOrder(int itemCount) {
    return currentProcessingLoad + itemCount <= maxCapacity;
  }

  /**
   * Increments the current processing load of the restaurant if the order can be accepted.
   *
   * @param itemCount the number of items in the new order
   * @throws RuntimeException if the restaurant cannot accept the order due to capacity constraints
   */
  public void incrementProcessingLoad(int itemCount) {
    if (canAcceptOrder(itemCount)) {
      this.currentProcessingLoad += itemCount;
    } else {
      throw new RuntimeException("Restaurant capacity exceeded. Cannot accept the order.");
    }
  }

  /**
   * Decrements the current processing load of the restaurant when an order is dispatched.
   * Ensures that the processing load does not go below zero.
   *
   * @param itemCount the number of items dispatched
   */
  public void decrementProcessingLoad(int itemCount) {
    this.currentProcessingLoad = Math.max(0, this.currentProcessingLoad - itemCount);
  }

  /**
   * Returns whether the restaurant has available capacity for new orders.
   *
   * @return true if the restaurant is under its max capacity, false otherwise
   */
  public boolean hasAvailableCapacity() {
    return currentProcessingLoad < maxCapacity;
  }
}