package com.example.fortpointproperties.features.properties.data.model

data class PropertyCardDto(
    val id: String? = null,
    val name: String? = null,
    val basicDescription: String? = null,
    val location: String? = null,
    val priceRangeMin: Double? = null,
    val priceRangeMax: Double? = null,
    val listingTypes: List<String> = emptyList(),
    val hasPromo: Boolean? = null,
    val coverPhotoUrl: String? = null
)
