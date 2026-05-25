package com.example.fortpointproperties.features.favorites.network

import com.example.fortpointproperties.features.favorites.data.model.FavoriteDto
import com.example.fortpointproperties.shared.network.ApiResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FavoriteApi {

    @GET("user/favorites")
    suspend fun getFavorites(): Response<ApiResponse<List<FavoriteDto>>>

    @POST("user/favorites/{propertyId}")
    suspend fun addToFavorites(@Path("propertyId") propertyId: String): Response<ApiResponse<String>>

    @DELETE("user/favorites/{propertyId}")
    suspend fun removeFromFavorites(@Path("propertyId") propertyId: String): Response<ApiResponse<String>>

    @GET("user/favorites/{propertyId}/check")
    suspend fun checkIfFavorited(@Path("propertyId") propertyId: String): Response<ApiResponse<Boolean>>
}
