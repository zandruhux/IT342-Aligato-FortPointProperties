package com.example.fortpointproperties.features.messaging.data.model

import com.google.gson.annotations.SerializedName

data class MessagingSocketEvent(
    @SerializedName("type")
    val type: String? = null,

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
    val createdAt: String? = null,

    @SerializedName("registeredUserId")
    val registeredUserId: String? = null,

    @SerializedName("registeredUserName")
    val registeredUserName: String? = null,

    @SerializedName("registeredUserProfileImageUrl")
    val registeredUserProfileImageUrl: String? = null,

    @SerializedName("assignedAgentId")
    val assignedAgentId: String? = null,

    @SerializedName("assignedAgentName")
    val assignedAgentName: String? = null,

    @SerializedName("assignedAgentProfileImageUrl")
    val assignedAgentProfileImageUrl: String? = null,

    @SerializedName("preview")
    val preview: String? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("unreadCount")
    val unreadCount: Long? = null
)
