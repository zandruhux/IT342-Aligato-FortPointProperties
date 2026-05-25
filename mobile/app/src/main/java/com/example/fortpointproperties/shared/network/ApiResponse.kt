package com.example.fortpointproperties.shared.network

data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ErrorDetail? = null,
    val timestamp: String? = null
)
