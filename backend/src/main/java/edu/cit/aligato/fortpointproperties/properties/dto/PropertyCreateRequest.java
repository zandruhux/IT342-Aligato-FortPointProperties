package edu.cit.aligato.fortpointproperties.properties.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PropertyCreateRequest {
    
    @NotBlank(message = "Property name is required")
    private String name;

    private String basicDescription;

    @NotBlank(message = "Developer name is required")
    private String developer;

    @NotNull(message = "Minimum price is required")
    @Positive(message = "Minimum price must be positive")
    private Double priceRangeMin;

    @NotNull(message = "Maximum price is required")
    @Positive(message = "Maximum price must be positive")
    private Double priceRangeMax;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Listing type is required")
    private List<String> listingType; // Format: ["Pre-Selling", "RFO", "Rent-To-Own"]

    private Boolean petFriendly = false;

    private Boolean parkingAvailable = false;

    @NotBlank(message = "Turnover date is required")
    private String turnoverDate;

    private String amenities;

    private String keySellingPoints;

    private String brochurePdfUrl;

    private String inventoryLink;

    /**
     * Optional list of units to create with this property
     * If not provided or empty, property is created without units (can add later)
     * Each unit must have unitType, reservationFee, equityPeriodMonths, monthlyEquity, 
     * totalSellingPrice, and financingTypes
     */
    private List<PropertyUnitCreateRequest> units;

    // --- Constructors ---
    public PropertyCreateRequest() {
    }

    // --- Getters and Setters ---
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBasicDescription() {
        return basicDescription;
    }

    public void setBasicDescription(String basicDescription) {
        this.basicDescription = basicDescription;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
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

    public List<String> getListingType() {
        return listingType;
    }

    public void setListingType(List<String> listingType) {
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

    public String getKeySellingPoints() {
        return keySellingPoints;
    }

    public void setKeySellingPoints(String keySellingPoints) {
        this.keySellingPoints = keySellingPoints;
    }

    public String getBrochurePdfUrl() {
        return brochurePdfUrl;
    }

    public void setBrochurePdfUrl(String brochurePdfUrl) {
        this.brochurePdfUrl = brochurePdfUrl;
    }

    public String getInventoryLink() {
        return inventoryLink;
    }

    public void setInventoryLink(String inventoryLink) {
        this.inventoryLink = inventoryLink;
    }

    public List<PropertyUnitCreateRequest> getUnits() {
        return units;
    }

    public void setUnits(List<PropertyUnitCreateRequest> units) {
        this.units = units;
    }
}
