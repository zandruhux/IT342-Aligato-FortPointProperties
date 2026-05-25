package com.example.fortpointproperties.features.messaging.data.model

import com.google.gson.annotations.SerializedName

data class ConversationDto(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("registeredUserId")
    val registeredUserId: String? = null,

    @SerializedName("assignedAgentId")
    val assignedAgentId: String? = null,

    @SerializedName("registeredUserName")
    val registeredUserName: String? = null,

    @SerializedName("assignedAgentName")
    val assignedAgentName: String? = null,

    @SerializedName("registeredUserProfileImageUrl")
    val registeredUserProfileImageUrl: String? = null,

    @SerializedName("assignedAgentProfileImageUrl")
    val assignedAgentProfileImageUrl: String? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("latestMessagePreview")
    val latestMessagePreview: String? = null,

    @SerializedName("latestMessageSenderId")
    val latestMessageSenderId: String? = null,

    @SerializedName("latestMessageSenderName")
    val latestMessageSenderName: String? = null,

    @SerializedName("latestMessageSenderProfileImageUrl")
    val latestMessageSenderProfileImageUrl: String? = null,

    @SerializedName("unreadCount")
    val unreadCount: Long? = 0,

    @SerializedName("unread")
    val unread: Boolean? = false,

    @SerializedName("latestMessageAt")
    val latestMessageAt: String? = null,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("updatedAt")
    val updatedAt: String? = null
)
