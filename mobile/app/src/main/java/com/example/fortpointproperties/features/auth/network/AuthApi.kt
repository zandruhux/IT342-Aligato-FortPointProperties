package com.example.fortpointproperties.features.auth.network

import com.example.fortpointproperties.features.auth.data.AuthResponse
import com.example.fortpointproperties.features.auth.data.LoginRequest
import com.example.fortpointproperties.features.auth.data.LoginResponse
import com.example.fortpointproperties.features.auth.data.RegisterRequest
import com.example.fortpointproperties.features.auth.data.UpdateProfileRequest
import com.example.fortpointproperties.features.auth.data.UserResponse
import com.example.fortpointproperties.shared.network.ApiResponse

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.Response
import okhttp3.MultipartBody
import okhttp3.ResponseBody

interface AuthApi {

    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<AuthResponse>>

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<LoginResponse>>

    @GET("api/v1/auth/profile")
    suspend fun getProfile(): Response<ApiResponse<UserResponse>>

    @PUT("api/v1/auth/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<ApiResponse<UserResponse>>

    @Multipart
    @PUT("api/v1/auth/profile-image")
    suspend fun updateProfileImage(@Part image: MultipartBody.Part): Response<ResponseBody>

    @DELETE("api/v1/auth/profile-image")
    suspend fun removeProfileImage(): Response<ResponseBody>
}
