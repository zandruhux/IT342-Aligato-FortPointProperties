package com.example.fortpointproperties.features.auth.data

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserResponse
)