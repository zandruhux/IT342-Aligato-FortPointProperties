package com.example.fortpointproperties.features.properties.data.model

data class PropertyDetailDto(
    val id: String? = null,
    val name: String? = null,
    val basicDescription: String? = null,
    val location: String? = null,
    val priceRangeMin: Double? = null,
    val priceRangeMax: Double? = null,
    val listingTypes: List<String> = emptyList(),
    val financingTypes: List<String> = emptyList(),
    val petFriendly: Boolean? = null,
    val parkingAvailable: Boolean? = null,
    val hasPromo: Boolean? = null,
    val turnoverDate: String? = null,
    val amenities: List<AmenityDto> = emptyList(),
    val photos: List<PropertyPhotoDto> = emptyList(),
    val units: List<PropertyUnitDto> = emptyList()
)
