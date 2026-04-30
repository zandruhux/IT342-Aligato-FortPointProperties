package edu.cit.aligato.fortpointproperties.properties.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * PropertyLimitedDTO - For REGISTERED USERS (authenticated but not admin/agent)
 * Excludes: developer, keySellingPoints, brochurePdfUrl, inventoryLink
 * Includes: units (read-only list of property units)
 */
public class PropertyLimitedDTO {
    private String id;
    private String name;
    private String basicDescription;
    private Double priceRangeMin;
    private Double priceRangeMax;
    private String location;
    private String listingType;
    private Boolean petFriendly;
    private Boolean parkingAvailable;
    private String turnoverDate;
    private String amenities;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<PropertyUnitDTO> units;

    // --- Constructors ---
    public PropertyLimitedDTO() {
    }

    public PropertyLimitedDTO(String id, String name, String basicDescription, Double priceRangeMin,
                             Double priceRangeMax, String location, String listingType) {
        this.id = id;
        this.name = name;
        this.basicDescription = basicDescription;
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

    public List<PropertyUnitDTO> getUnits() {
        return units;
    }

    public void setUnits(List<PropertyUnitDTO> units) {
        this.units = units;
    }
}
