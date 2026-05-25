package com.example.fortpointproperties.features.properties.network

import com.example.fortpointproperties.features.properties.data.model.PropertyCardDto
import com.example.fortpointproperties.features.properties.data.model.PropertyDetailDto
import com.example.fortpointproperties.shared.network.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PropertyApi {

    @GET("user/properties")
    suspend fun getProperties(): Response<ApiResponse<List<PropertyCardDto>>>

    @GET("user/properties/search")
    suspend fun searchProperties(
        @Query("name") name: String? = null,
        @Query("location") location: String? = null,
        @Query("developer") developer: String? = null,
    ): Response<ApiResponse<List<PropertyCardDto>>>

    @GET("user/properties/{id}/advanced")
    suspend fun getPropertyDetails(@Path("id") id: String): Response<ApiResponse<PropertyDetailDto>>
}
