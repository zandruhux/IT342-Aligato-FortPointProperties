package com.example.fortpointproperties.features.auth.data

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("firstname")
    val firstname: String,
    
    @SerializedName("lastname")
    val lastname: String,
    
    val email: String,
    val password: String
)