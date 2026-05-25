package com.example.fortpointproperties.features.messaging.data.model

import com.google.gson.annotations.SerializedName

data class MessageDto(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("conversationId")
    val conversationId: Long? = null,

    @SerializedName("senderId")
    val senderId: String? = null,

    @SerializedName("senderName")
    val senderName: String? = null,

    @SerializedName("senderProfileImageUrl")
    val senderProfileImageUrl: String? = null,

    @SerializedName("senderRole")
    val senderRole: String? = null,

    @SerializedName("content")
    val content: String? = null,

    @SerializedName("createdAt")
    val createdAt: String? = null
)
