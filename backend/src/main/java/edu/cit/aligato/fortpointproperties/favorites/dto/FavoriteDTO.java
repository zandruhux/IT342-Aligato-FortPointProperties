package edu.cit.aligato.fortpointproperties.favorites.dto;

import java.time.LocalDateTime;

public class FavoriteDTO {
    public String id;
    public String propertyId;
    public String propertyName;
    public String description;
    public String location;
    public Double priceRangeMin;
    public Double priceRangeMax;
    public Boolean hasPromo;
    public String coverPhotoUrl;
    public LocalDateTime createdAt;

    public FavoriteDTO() {
    }

    public FavoriteDTO(String id, String propertyId, String propertyName, String description,
            String location, Double priceRangeMin, Double priceRangeMax, Boolean hasPromo,
            String coverPhotoUrl, LocalDateTime createdAt) {
        this.id = id;
        this.propertyId = propertyId;
        this.propertyName = propertyName;
        this.description = description;
        this.location = location;
        this.priceRangeMin = priceRangeMin;
        this.priceRangeMax = priceRangeMax;
        this.hasPromo = hasPromo;
        this.coverPhotoUrl = coverPhotoUrl;
        this.createdAt = createdAt;
    }

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
