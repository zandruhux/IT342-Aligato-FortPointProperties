package edu.cit.aligato.fortpointproperties.properties.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "property_units")
public class PropertyUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @Column(nullable = false)
    private String unitType;

    @Column(name = "floor_area")
    private Double floorArea;

    @Column(name = "lot_area")
    private Double lotArea;

    @Column(nullable = false)
    private Double reservationFee;

    @Column(name = "equity_period_months", nullable = false)
    private Integer equityPeriodMonths;

    @Column(name = "monthly_equity", nullable = false)
    private Double monthlyEquity;

    @Column(name = "total_selling_price", nullable = false)
    private Double totalSellingPrice;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public PropertyUnit() {
    }

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
