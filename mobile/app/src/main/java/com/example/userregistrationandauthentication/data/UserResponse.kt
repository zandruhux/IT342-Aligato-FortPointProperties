package com.example.userregistrationandauthentication.data

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("firstname")
    val firstname: String,
    
    @SerializedName("lastname")
    val lastname: String,
    
    val email: String,
    val role: String
)