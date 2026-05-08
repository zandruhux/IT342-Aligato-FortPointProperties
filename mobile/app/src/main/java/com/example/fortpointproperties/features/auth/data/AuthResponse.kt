package com.example.fortpointproperties.features.auth.data

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserResponse
)