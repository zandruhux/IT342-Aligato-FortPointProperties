package edu.cit.aligato.fortpointproperties.properties.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import edu.cit.aligato.fortpointproperties.auth.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "properties")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String basicDescription;

    @Column(nullable = false)
    private String developer;

    @Column(nullable = false)
    private Double priceRangeMin;

    @Column(nullable = false)
    private Double priceRangeMax;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String listingType; // Format: "Pre-Selling,RFO" (comma-separated)

    @Column(nullable = false)
    private Boolean petFriendly;

    @Column(nullable = false)
    private Boolean parkingAvailable;

    @Column(nullable = false)
    private String turnoverDate; // Format: YYYY-MM

    @Column(columnDefinition = "TEXT")
    private String amenities; // JSON or comma-separated list

    @Column(columnDefinition = "TEXT")
    private String keySellingPoints; // Marketing copy

    @Column(columnDefinition = "TEXT")
    private String brochurePdfUrl; // URL to property brochure PDF

    @Column(columnDefinition = "TEXT")
    private String inventoryLink; // URL to inventory/floor plan

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    /**
     * One-to-Many relationship with PropertyUnit
     * CascadeType.ALL ensures units are saved/updated/deleted with property
     * orphanRemoval = true removes units when they are removed from the list
     */
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PropertyUnit> units;

    // --- Constructors ---
    public Property() {
        this.units = new ArrayList<>();
    }

    public Property(String name, String developer, Double priceRangeMin, Double priceRangeMax,
                    String location, String listingType) {
        this.name = name;
        this.developer = developer;
        this.priceRangeMin = priceRangeMin;
        this.priceRangeMax = priceRangeMax;
        this.location = location;
        this.listingType = listingType;
        this.petFriendly = false;
        this.parkingAvailable = false;
        this.units = new ArrayList<>();
    }

    // --- Lifecycle Hooks ---
    @PrePersist
    protected void onPrePersist() {
        // If listingType is stored as a transient list, convert to comma-separated string
        // This method can be extended if needed for list handling
    }

    // --- Getters and Setters ---
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

    public List<PropertyUnit> getUnits() {
        return units;
    }

    public void setUnits(List<PropertyUnit> units) {
        this.units = units;
    }
}
