package com.example.fortpointproperties.features.auth.data

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
)