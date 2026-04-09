package edu.cit.aligato.fortpointproperties.properties.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PropertyCreateRequest {
    
    @NotBlank(message = "Property name is required")
    private String propertyName;

    private String description;

    @NotBlank(message = "Developer name is required")
    private String developerName;

    @NotNull(message = "Minimum price is required")
    @Positive(message = "Minimum price must be positive")
    private Double priceRangeMin;

    @NotNull(message = "Maximum price is required")
    @Positive(message = "Maximum price must be positive")
    private Double priceRangeMax;

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Property type is required")
    private String propertyType;

    @NotBlank(message = "Unit type is required")
    private String unitType;

    @NotBlank(message = "Listing type is required")
    private String listingType;

    private Boolean petFriendly = false;

    private Boolean parkingAvailable = false;

    @NotBlank(message = "Turnover date is required")
    private String turnoverDate;

    private String amenities;

    private String priceComputations;

    private String developerLinks;

    private String pitchReadyPhrases;

    // --- Constructors ---
    public PropertyCreateRequest() {
    }

    // --- Getters and Setters ---
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
}
