package edu.cit.aligato.fortpointproperties.properties.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import edu.cit.aligato.fortpointproperties.auth.entity.User;
import edu.cit.aligato.fortpointproperties.properties.enums.FinancingType;
import edu.cit.aligato.fortpointproperties.properties.enums.ListingType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
    private String location;

    @ElementCollection(targetClass = ListingType.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "property_listing_types", joinColumns = @JoinColumn(name = "property_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "listing_type", nullable = false)
    private Set<ListingType> listingTypes = new HashSet<>();

    @ElementCollection(targetClass = FinancingType.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "property_financing_types", joinColumns = @JoinColumn(name = "property_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "financing_type", nullable = false)
    private Set<FinancingType> financingTypes = new HashSet<>();

    @Column(nullable = false)
    private Boolean petFriendly = false;

    @Column(nullable = false)
    private Boolean parkingAvailable = false;

    @Column(nullable = false)
    private Boolean hasPromo = false;

    @Column(nullable = false)
    private Boolean featured = false;

    @Column(nullable = false)
    private Boolean visible = true;

    @Column(nullable = false)
    private String turnoverDate;

    @ManyToMany
    @JoinTable(
            name = "property_amenities",
            joinColumns = @JoinColumn(name = "property_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id"))
    private Set<Amenity> amenities = new HashSet<>();

    @Column(columnDefinition = "TEXT")
    private String keySellingPoints;

    @Column(columnDefinition = "TEXT")
    private String brochurePdfUrl;

    @Column(columnDefinition = "TEXT")
    private String inventoryLink;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "id",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User createdBy;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PropertyUnit> units = new ArrayList<>();

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PropertyPhoto> photos = new ArrayList<>();

    public Property() {
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Set<ListingType> getListingTypes() {
        return listingTypes;
    }

    public void setListingTypes(Set<ListingType> listingTypes) {
        this.listingTypes = listingTypes;
    }

    public Set<FinancingType> getFinancingTypes() {
        return financingTypes;
    }

    public void setFinancingTypes(Set<FinancingType> financingTypes) {
        this.financingTypes = financingTypes;
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

    public Boolean getHasPromo() {
        return hasPromo;
    }

    public void setHasPromo(Boolean hasPromo) {
        this.hasPromo = hasPromo;
    }

    public Boolean getFeatured() {
        return featured;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public String getTurnoverDate() {
        return turnoverDate;
    }

    public void setTurnoverDate(String turnoverDate) {
        this.turnoverDate = turnoverDate;
    }

    public Set<Amenity> getAmenities() {
        return amenities;
    }

    public void setAmenities(Set<Amenity> amenities) {
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

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public List<PropertyUnit> getUnits() {
        return units;
    }

    public void setUnits(List<PropertyUnit> units) {
        this.units = units;
    }

    public List<PropertyPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PropertyPhoto> photos) {
        this.photos = photos;
    }
}
