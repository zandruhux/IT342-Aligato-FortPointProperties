package com.example.fortpointproperties.features.auth.data

import com.google.gson.annotations.SerializedName

data class UpdateProfileRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("firstname")
    val firstname: String,

    @SerializedName("lastname")
    val lastname: String,

    @SerializedName("phoneNumber")
    val phoneNumber: String? = null
)
