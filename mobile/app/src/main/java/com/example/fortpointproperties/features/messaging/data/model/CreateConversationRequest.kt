package com.example.fortpointproperties.features.messaging.data.model

import com.google.gson.annotations.SerializedName

data class CreateConversationRequest(
    @SerializedName("content")
    val content: String
)
