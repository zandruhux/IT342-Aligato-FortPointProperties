package edu.cit.aligato.fortpointproperties.properties.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import edu.cit.aligato.fortpointproperties.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "properties")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String propertyName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String developerName;

    @Column(nullable = false)
    private Double priceRangeMin;

    @Column(nullable = false)
    private Double priceRangeMax;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String propertyType; // e.g., "Condo", "House", "Townhouse"

    @Column(nullable = false)
    private String unitType; // e.g., "Studio", "1-Bedroom", "2-Bedroom"

    @Column(nullable = false)
    private String listingType; // "Pre-Selling" or "Ready-For-Occupancy"

    @Column(nullable = false)
    private Boolean petFriendly;

    @Column(nullable = false)
    private Boolean parkingAvailable;

    @Column(nullable = false)
    private String turnoverDate; // Format: YYYY-MM

    @Column(columnDefinition = "TEXT")
    private String amenities; // JSON or comma-separated list

    @Column(columnDefinition = "TEXT")
    private String priceComputations; // Detailed pricing breakdown

    @Column(columnDefinition = "TEXT")
    private String developerLinks; // JSON with links and PDFs

    @Column(columnDefinition = "TEXT")
    private String pitchReadyPhrases; // Marketing copy

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;



    // --- Constructors ---
    public Property() {
    }

    public Property(String propertyName, String developerName, Double priceRangeMin, Double priceRangeMax,
                    String location, String propertyType, String unitType, String listingType) {
        this.propertyName = propertyName;
        this.developerName = developerName;
        this.priceRangeMin = priceRangeMin;
        this.priceRangeMax = priceRangeMax;
        this.location = location;
        this.propertyType = propertyType;
        this.unitType = unitType;
        this.listingType = listingType;
        this.petFriendly = false;
        this.parkingAvailable = false;
    }

    // --- Getters and Setters ---4
    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
    
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
}
