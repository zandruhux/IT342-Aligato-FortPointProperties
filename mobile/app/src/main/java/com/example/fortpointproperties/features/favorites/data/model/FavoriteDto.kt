package com.example.fortpointproperties.features.favorites.data.model

data class FavoriteDto(
    val id: String? = null,
    val propertyId: String? = null,
    val propertyName: String? = null,
    val description: String? = null,
    val location: String? = null,
    val priceRangeMin: Double? = null,
    val priceRangeMax: Double? = null,
    val hasPromo: Boolean? = null,
    val coverPhotoUrl: String? = null,
    val createdAt: String? = null
)
