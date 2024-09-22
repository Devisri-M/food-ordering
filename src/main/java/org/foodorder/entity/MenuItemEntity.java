package org.foodorder.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "menu_item")
public class MenuItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Boolean available = true;

    // Retain the restaurantId field for compatibility
    @Column(name = "restaurant_id", insertable = false, updatable = false)
    private Long restaurantId;

    // Establish a ManyToOne relationship with RestaurantEntity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private RestaurantEntity restaurant;

    // Add a reference to the selected restaurant (This will not be persisted in the database)
    @Transient
    private RestaurantEntity selectedRestaurant;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    // Getter and setter for restaurant (ManyToOne relationship)
    public RestaurantEntity getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(RestaurantEntity restaurant) {
        this.restaurant = restaurant;
        // Ensure consistency between the entity and the restaurantId field
        if (restaurant != null) {
            this.restaurantId = restaurant.getId();
        }
    }

    // Retain compatibility with existing code that uses restaurantId directly
    public Long getRestaurantId() {
        return restaurant != null ? restaurant.getId() : null;
    }

    public void setRestaurantId(Long restaurantId) {
        if (this.restaurant == null) {
            this.restaurant = new RestaurantEntity();
        }
        this.restaurant.setId(restaurantId);
    }

    // Getter and setter for selected restaurant (transient field)
    public RestaurantEntity getSelectedRestaurant() {
        return selectedRestaurant;
    }

    public void setSelectedRestaurant(RestaurantEntity selectedRestaurant) {
        this.selectedRestaurant = selectedRestaurant;
    }
}