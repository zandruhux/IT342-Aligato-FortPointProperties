package com.example.fortpointproperties.features.messaging.network

import com.example.fortpointproperties.features.messaging.data.model.ConversationDto
import com.example.fortpointproperties.features.messaging.data.model.CreateConversationRequest
import com.example.fortpointproperties.features.messaging.data.model.MessageDto
import com.example.fortpointproperties.features.messaging.data.model.SendMessageRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.PUT

interface MessagingApi {
    @POST("api/messaging/conversations")
    suspend fun createConversation(
        @Body request: CreateConversationRequest,
    ): Response<ConversationDto>

    @GET("api/messaging/conversations")
    suspend fun getConversations(): Response<List<ConversationDto>>

    @GET("api/messaging/conversations/{conversationId}/messages")
    suspend fun getMessages(
        @Path("conversationId") conversationId: Long,
    ): Response<List<MessageDto>>

    @PUT("api/messaging/conversations/{conversationId}/read")
    suspend fun markConversationRead(
        @Path("conversationId") conversationId: Long,
    ): Response<ConversationDto>

    @POST("api/messaging/conversations/{conversationId}/messages")
    suspend fun sendMessage(
        @Path("conversationId") conversationId: Long,
        @Body request: SendMessageRequest,
    ): Response<MessageDto>
}
