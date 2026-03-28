package com.example.userregistrationandauthentication.data

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserResponse
)