package edu.cit.aligato.fortpointproperties.favorites.dto;

import java.time.LocalDateTime;

/**
 * FavoriteDTO - For returning favorite properties to registered users
 * Includes property details with createdAt timestamp for sorting
 */
public class FavoriteDTO {
    private String id;
    private String propertyId;
    private String propertyName;
    private String description;
    private String location;
    private Double priceRangeMin;
    private Double priceRangeMax;
    private LocalDateTime createdAt;

    // --- Constructors ---
    public FavoriteDTO() {
    }

    public FavoriteDTO(String id, String propertyId, String propertyName, String description,
            String location, Double priceRangeMin, Double priceRangeMax, LocalDateTime createdAt) {
        this.id = id;
        this.propertyId = propertyId;
        this.propertyName = propertyName;
        this.description = description;
        this.location = location;
        this.priceRangeMin = priceRangeMin;
        this.priceRangeMax = priceRangeMax;
        this.createdAt = createdAt;
    }

    // --- Getters and Setters ---
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getPriceRangeMin() {
        return priceRangeMin;
    }

    public void setPriceRangeMin(Double priceRangeMin) {
        this.priceRangeMin = priceRangeMin;
    }

    public Double getPriceRangeMax() {
        return priceRangeMax;
    }

    public void setPriceRangeMax(Double priceRangeMax) {
        this.priceRangeMax = priceRangeMax;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
