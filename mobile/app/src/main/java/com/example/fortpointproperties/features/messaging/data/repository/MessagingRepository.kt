package com.example.fortpointproperties.features.messaging.data.repository

import com.example.fortpointproperties.features.auth.network.AuthApi
import com.example.fortpointproperties.features.messaging.data.model.ConversationDto
import com.example.fortpointproperties.features.messaging.data.model.CreateConversationRequest
import com.example.fortpointproperties.features.messaging.data.model.MessageDto
import com.example.fortpointproperties.features.messaging.data.model.SendMessageRequest
import com.example.fortpointproperties.features.messaging.network.MessagingApi
import com.example.fortpointproperties.shared.auth.SessionManager
import com.example.fortpointproperties.shared.auth.TokenManager
import com.example.fortpointproperties.shared.network.ApiClient
import com.google.gson.JsonParser
import retrofit2.Response

class MessagingRepository(
    private val messagingApi: MessagingApi = ApiClient.retrofit.create(MessagingApi::class.java),
    private val authApi: AuthApi = ApiClient.retrofit.create(AuthApi::class.java),
) {

    suspend fun getConversations(): List<ConversationDto> {
        requireRegisteredUserAccess()
        return extractList(messagingApi.getConversations(), "Failed to load conversations.")
    }

    suspend fun createConversation(content: String): ConversationDto {
        requireRegisteredUserAccess()
        return extractItem(
            messagingApi.createConversation(CreateConversationRequest(content.trim())),
            "Failed to start conversation."
        )
    }

    suspend fun getMessages(conversationId: Long): List<MessageDto> {
        requireRegisteredUserAccess()
        return extractList(
            messagingApi.getMessages(conversationId),
            "Failed to load messages."
        )
    }

    suspend fun sendMessage(conversationId: Long, content: String): MessageDto {
        requireRegisteredUserAccess()
        return extractItem(
            messagingApi.sendMessage(conversationId, SendMessageRequest(content.trim())),
            "Failed to send message."
        )
    }

    suspend fun markConversationRead(conversationId: Long): ConversationDto {
        requireRegisteredUserAccess()
        return extractItem(
            messagingApi.markConversationRead(conversationId),
            "Failed to update conversation read state."
        )
    }

    private suspend fun requireRegisteredUserAccess() {
        if (TokenManager.getAccessToken().isNullOrBlank()) {
            throw MessagingLoginRequiredException("Login required to access messages.")
        }

        val storedRole = SessionManager.getStoredRole()
        when {
            SessionManager.isRegisteredUserRole(storedRole) -> return
            SessionManager.isPrivilegedRole(storedRole) -> {
                throw MessagingRoleException("This mobile module is only for registered users.")
            }
        }

        val profileResponse = authApi.getProfile()
        if (!profileResponse.isSuccessful) {
            if (profileResponse.code() == 401 || profileResponse.code() == 403) {
                throw MessagingLoginRequiredException("Session expired. Please log in again.")
            }
            throw MessagingLoadException(parseErrorMessage(profileResponse, "Unable to verify user access."))
        }

        val user = profileResponse.body()?.data
            ?: throw MessagingLoadException("Unable to read the current user profile.")

        TokenManager.saveUserRole(user.role)

        if (!SessionManager.isRegisteredUserRole(user.role)) {
            throw MessagingRoleException("This mobile module is only for registered users.")
        }
    }

    private fun <T> extractList(
        response: Response<List<T>>,
        fallbackMessage: String,
    ): List<T> {
        if (!response.isSuccessful) {
            if (response.code() == 401 || response.code() == 403) {
                throw MessagingLoginRequiredException("Session expired. Please log in again.")
            }
            throw MessagingLoadException(parseErrorMessage(response, fallbackMessage))
        }

        return response.body() ?: throw MessagingLoadException("Server returned an empty response.")
    }

    private fun <T> extractItem(
        response: Response<T>,
        fallbackMessage: String,
    ): T {
        if (!response.isSuccessful) {
            if (response.code() == 401 || response.code() == 403) {
                throw MessagingLoginRequiredException("Session expired. Please log in again.")
            }
            throw MessagingSendException(parseErrorMessage(response, fallbackMessage))
        }

        return response.body() ?: throw MessagingSendException("Server returned an empty response.")
    }

    private fun <T> parseErrorMessage(
        response: Response<T>,
        fallbackMessage: String,
    ): String {
        val raw = response.errorBody()?.string().orEmpty()
        if (raw.isNotBlank()) {
            runCatching {
                val json = JsonParser().parse(raw).asJsonObject
                val error = json.getAsJsonObject("error")
                val message = error?.get("message")?.asString
                if (!message.isNullOrBlank()) {
                    return message
                }
                val topLevelMessage = json.get("message")?.asString
                if (!topLevelMessage.isNullOrBlank()) {
                    return topLevelMessage
                }
            }
        }

        return response.message().takeIf { it.isNotBlank() } ?: fallbackMessage
    }

}
