package com.example.userregistrationandauthentication.data

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserResponse
)