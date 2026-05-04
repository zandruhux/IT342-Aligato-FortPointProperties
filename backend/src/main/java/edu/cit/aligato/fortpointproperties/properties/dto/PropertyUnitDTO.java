package edu.cit.aligato.fortpointproperties.properties.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * PropertyUnitDTO - Data Transfer Object for PropertyUnit
 * 
 * Represents a single unit within a property.
 * Used in PropertyDTO as nested units list.
 */
public class PropertyUnitDTO {
    private String id;
    private String unitType;
    private Double floorArea;
    private Double lotArea;
    private Double reservationFee;
    private Integer equityPeriodMonths;
    private Double monthlyEquity;
    private Double totalSellingPrice;
    private List<String> financingTypes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // --- Constructors ---
    public PropertyUnitDTO() {
    }

    public PropertyUnitDTO(String id, String unitType, Double floorArea, Double lotArea,
                          Double reservationFee, Integer equityPeriodMonths, Double monthlyEquity,
                          Double totalSellingPrice, List<String> financingTypes) {
        this.id = id;
        this.unitType = unitType;
        this.floorArea = floorArea;
        this.lotArea = lotArea;
        this.reservationFee = reservationFee;
        this.equityPeriodMonths = equityPeriodMonths;
        this.monthlyEquity = monthlyEquity;
        this.totalSellingPrice = totalSellingPrice;
        this.financingTypes = financingTypes;
    }

    // --- Getters and Setters ---
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
