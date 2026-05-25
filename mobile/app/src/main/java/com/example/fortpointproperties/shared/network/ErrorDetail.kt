package com.example.fortpointproperties.shared.network

data class ErrorDetail(
    val code: String? = null,
    val message: String? = null,
    val details: Any? = null
)
