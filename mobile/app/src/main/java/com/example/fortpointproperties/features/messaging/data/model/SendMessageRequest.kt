package com.example.fortpointproperties.features.messaging.data.model

import com.google.gson.annotations.SerializedName

data class SendMessageRequest(
    @SerializedName("content")
    val content: String
)
