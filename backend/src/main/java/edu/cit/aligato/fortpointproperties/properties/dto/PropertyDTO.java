package edu.cit.aligato.fortpointproperties.properties.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PropertyDTO {
    private String id;
    private String name;
    private String basicDescription;
    private String developer;
    private Double priceRangeMin;
    private Double priceRangeMax;
    private String location;
    private String listingType;
    private Boolean petFriendly;
    private Boolean parkingAvailable;
    private String turnoverDate;
    private String amenities;
    private String keySellingPoints;
    private String brochurePdfUrl;
    private String inventoryLink;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private List<PropertyUnitDTO> units;

    // --- Constructors ---
    public PropertyDTO() {
    }

    public PropertyDTO(String id, String name, String developer, Double priceRangeMin, 
                      Double priceRangeMax, String location, String listingType) {
        this.id = id;
        this.name = name;
        this.developer = developer;
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

    public List<PropertyUnitDTO> getUnits() {
        return units;
    }

    public void setUnits(List<PropertyUnitDTO> units) {
        this.units = units;
    }


}
