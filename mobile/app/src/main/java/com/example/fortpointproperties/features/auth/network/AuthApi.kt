package com.example.fortpointproperties.features.auth.network

import com.example.fortpointproperties.features.auth.data.ApiResponse
import com.example.fortpointproperties.features.auth.data.AuthResponse
import com.example.fortpointproperties.features.auth.data.LoginRequest
import com.example.fortpointproperties.features.auth.data.LoginResponse
import com.example.fortpointproperties.features.auth.data.RegisterRequest
import com.example.fortpointproperties.features.auth.data.UserResponse

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.Response

interface AuthApi {

    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<AuthResponse>>

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<LoginResponse>>

    @GET("api/v1/auth/profile")
    suspend fun getProfile(): Response<ApiResponse<UserResponse>>
}