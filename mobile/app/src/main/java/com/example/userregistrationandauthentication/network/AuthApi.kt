package com.example.userregistrationandauthentication.network

import com.example.userregistrationandauthentication.data.ApiResponse
import com.example.userregistrationandauthentication.data.AuthResponse
import com.example.userregistrationandauthentication.data.LoginRequest
import com.example.userregistrationandauthentication.data.LoginResponse
import com.example.userregistrationandauthentication.data.RegisterRequest
import com.example.userregistrationandauthentication.data.UserResponse

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