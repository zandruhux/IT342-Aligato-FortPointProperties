package edu.cit.aligato.fortpointproperties.properties.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class PropertyUnitRequestDTO {
    @NotBlank(message = "Unit type is required")
    public String unitType;

    public Double floorArea;

    public Double lotArea;

    @NotNull(message = "Reservation fee is required")
    @PositiveOrZero(message = "Reservation fee must be positive or zero")
    public Double reservationFee;

    @NotNull(message = "Equity period (months) is required")
    @Positive(message = "Equity period must be positive")
    public Integer equityPeriodMonths;

    @NotNull(message = "Monthly equity is required")
    @PositiveOrZero(message = "Monthly equity must be positive or zero")
    public Double monthlyEquity;

    @NotNull(message = "Total selling price is required")
    @Positive(message = "Total selling price must be positive")
    public Double totalSellingPrice;
}
