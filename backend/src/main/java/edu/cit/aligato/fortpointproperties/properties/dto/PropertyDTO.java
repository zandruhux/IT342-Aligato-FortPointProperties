package edu.cit.aligato.fortpointproperties.properties.dto;

import java.time.LocalDateTime;

public class PropertyDTO {
    private String id;
    private String propertyName;
    private String description;
    private String developerName;
    private Double priceRangeMin;
    private Double priceRangeMax;
    private String location;
    private String propertyType;
    private String unitType;
    private String listingType;
    private Boolean petFriendly;
    private Boolean parkingAvailable;
    private String turnoverDate;
    private String amenities;
    private String priceComputations;
    private String developerLinks;
    private String pitchReadyPhrases;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;

    // --- Constructors ---
    public PropertyDTO() {
    }

    public PropertyDTO(String id, String propertyName, String developerName, Double priceRangeMin, 
                      Double priceRangeMax, String location, String listingType) {
        this.id = id;
        this.propertyName = propertyName;
        this.developerName = developerName;
        this.priceRangeMin = priceRangeMin;
        this.priceRangeMax = priceRangeMax;
        this.location = location;
        this.listingType = listingType;
    }

    // --- Getters and Setters ---
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDeveloperName() {
        return developerName;
    }

    public void setDeveloperName(String developerName) {
        this.developerName = developerName;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public String getListingType() {
        return listingType;
    }

    public void setListingType(String listingType) {
        this.listingType = listingType;
    }

    public Boolean getPetFriendly() {
        return petFriendly;
    }

    public void setPetFriendly(Boolean petFriendly) {
        this.petFriendly = petFriendly;
    }

    public Boolean getParkingAvailable() {
        return parkingAvailable;
    }

    public void setParkingAvailable(Boolean parkingAvailable) {
        this.parkingAvailable = parkingAvailable;
    }

    public String getTurnoverDate() {
        return turnoverDate;
    }

    public void setTurnoverDate(String turnoverDate) {
        this.turnoverDate = turnoverDate;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    public String getPriceComputations() {
        return priceComputations;
    }

    public void setPriceComputations(String priceComputations) {
        this.priceComputations = priceComputations;
    }

    public String getDeveloperLinks() {
        return developerLinks;
    }

    public void setDeveloperLinks(String developerLinks) {
        this.developerLinks = developerLinks;
    }

    public String getPitchReadyPhrases() {
        return pitchReadyPhrases;
    }

    public void setPitchReadyPhrases(String pitchReadyPhrases) {
        this.pitchReadyPhrases = pitchReadyPhrases;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
