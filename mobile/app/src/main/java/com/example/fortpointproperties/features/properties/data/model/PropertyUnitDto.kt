package com.example.fortpointproperties.features.properties.data.model

data class PropertyUnitDto(
    val id: String? = null,
    val unitType: String? = null,
    val floorArea: Double? = null,
    val lotArea: Double? = null,
    val reservationFee: Double? = null,
    val equityPeriodMonths: Int? = null,
    val monthlyEquity: Double? = null,
    val totalSellingPrice: Double? = null
)
