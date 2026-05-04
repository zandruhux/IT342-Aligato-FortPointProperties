package edu.cit.aligato.fortpointproperties.properties.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * PropertyUnit Entity - Represents individual units within a property
 * 
 * KEY DESIGN DECISIONS:
 * - @ElementCollection is used for financingTypes to support flexible multi-values without creating separate entity
 * - monthlyEquity and totalSellingPrice are MANUALLY ENTERED (not auto-calculated)
 *   Future formula (commented): totalSellingPrice = reservationFee + (monthlyEquity * equityPeriodMonths)
 * - All unit relationships are cascaded from Property side (CascadeType.ALL)
 * - Timestamps are auto-generated for audit trail
 */
@Entity
@Table(name = "property_units")
public class PropertyUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // ========== Relationship ==========
    @ManyToOne(optional = false)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    // ========== Unit Details ==========
    @Column(nullable = false)
    private String unitType; // e.g., "Studio", "1-Bedroom", "2-Bedroom", "Penthouse"

    @Column(name = "floor_area")
    private Double floorArea; // in square meters

    @Column(name = "lot_area")
    private Double lotArea; // nullable for condominiums

    // ========== Pricing ==========
    @Column(nullable = false)
    private Double reservationFee;

    @Column(name = "equity_period_months", nullable = false)
    private Integer equityPeriodMonths;

    /**
     * Monthly equity payment amount (MANUALLY ENTERED)
     * Future calculation: totalSellingPrice / equityPeriodMonths if needed
     */
    @Column(name = "monthly_equity", nullable = false)
    private Double monthlyEquity;

    /**
     * Total selling price (MANUALLY ENTERED)
     * Future calculation: reservationFee + (monthlyEquity * equityPeriodMonths)
     * Validation: must be > reservationFee
     */
    @Column(name = "total_selling_price", nullable = false)
    private Double totalSellingPrice;

    // ========== Financing Options ==========
    /**
     * @ElementCollection for flexible array storage without creating separate table/entity
     * Stores values like ["Cash Only", "Bank Financing", "In-House"]
     */
    @ElementCollection
    @CollectionTable(name = "property_unit_financing", joinColumns = @JoinColumn(name = "unit_id"))
    @Column(name = "financing_type")
    private List<String> financingTypes;

    // ========== Audit Timestamps ==========
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ========== Constructors ==========
    public PropertyUnit() {
    }

    public PropertyUnit(String unitType, Double reservationFee, Integer equityPeriodMonths,
                        Double monthlyEquity, Double totalSellingPrice) {
        this.unitType = unitType;
        this.reservationFee = reservationFee;
        this.equityPeriodMonths = equityPeriodMonths;
        this.monthlyEquity = monthlyEquity;
        this.totalSellingPrice = totalSellingPrice;
    }

    // ========== Getters and Setters ==========
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public Double getFloorArea() {
        return floorArea;
    }

    public void setFloorArea(Double floorArea) {
        this.floorArea = floorArea;
    }

    public Double getLotArea() {
        return lotArea;
    }

    public void setLotArea(Double lotArea) {
        this.lotArea = lotArea;
    }

    public Double getReservationFee() {
        return reservationFee;
    }

    public void setReservationFee(Double reservationFee) {
        this.reservationFee = reservationFee;
    }

    public Integer getEquityPeriodMonths() {
        return equityPeriodMonths;
    }

    public void setEquityPeriodMonths(Integer equityPeriodMonths) {
        this.equityPeriodMonths = equityPeriodMonths;
    }

    public Double getMonthlyEquity() {
        return monthlyEquity;
    }

    public void setMonthlyEquity(Double monthlyEquity) {
        this.monthlyEquity = monthlyEquity;
    }

    public Double getTotalSellingPrice() {
        return totalSellingPrice;
    }

    public void setTotalSellingPrice(Double totalSellingPrice) {
        this.totalSellingPrice = totalSellingPrice;
    }

    public List<String> getFinancingTypes() {
        return financingTypes;
    }

    public void setFinancingTypes(List<String> financingTypes) {
        this.financingTypes = financingTypes;
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
