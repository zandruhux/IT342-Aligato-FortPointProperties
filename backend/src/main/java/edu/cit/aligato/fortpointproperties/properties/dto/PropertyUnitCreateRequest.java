package edu.cit.aligato.fortpointproperties.properties.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * PropertyUnitCreateRequest - Input DTO for creating/updating PropertyUnit
 * 
 * All fields except id and timestamps are included.
 * Validation ensures required fields are present and values are valid.
 */
public class PropertyUnitCreateRequest {

    @NotBlank(message = "Unit type is required")
    private String unitType;

    private Double floorArea; // optional, for condos floor area

    private Double lotArea; // optional, nullable for condominiums

    @NotNull(message = "Reservation fee is required")
    @Positive(message = "Reservation fee must be positive or zero")
    private Double reservationFee;

    @NotNull(message = "Equity period (months) is required")
    @Positive(message = "Equity period must be positive")
    private Integer equityPeriodMonths;

    @NotNull(message = "Monthly equity is required")
    @Positive(message = "Monthly equity must be positive or zero")
    private Double monthlyEquity;

    @NotNull(message = "Total selling price is required")
    @Positive(message = "Total selling price must be positive")
    private Double totalSellingPrice;

    @NotNull(message = "Financing types are required")
    private List<String> financingTypes; // e.g., ["Cash Only", "Bank Financing", "In-House"]

    // --- Constructors ---
    public PropertyUnitCreateRequest() {
    }

    public PropertyUnitCreateRequest(String unitType, Double reservationFee, 
                                    Integer equityPeriodMonths, Double monthlyEquity,
                                    Double totalSellingPrice, List<String> financingTypes) {
        this.unitType = unitType;
        this.reservationFee = reservationFee;
        this.equityPeriodMonths = equityPeriodMonths;
        this.monthlyEquity = monthlyEquity;
        this.totalSellingPrice = totalSellingPrice;
        this.financingTypes = financingTypes;
    }

    // --- Getters and Setters ---
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
}
