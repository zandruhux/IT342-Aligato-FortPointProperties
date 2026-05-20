package edu.cit.aligato.fortpointproperties.messaging.dto;

import java.time.LocalDateTime;

public class MessageDTO {
    private Long id;
    private Long conversationId;
    private String senderId;
    private String senderName;
    private String senderRole;
    private String content;
    private LocalDateTime createdAt;

    public MessageDTO() {
    }

    public MessageDTO(Long id, Long conversationId, String senderId, String senderRole, String content,
            LocalDateTime createdAt) {
        this.id = id;
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.senderRole = senderRole;
        this.content = content;
        this.createdAt = createdAt;
    }

    public MessageDTO(Long id, Long conversationId, String senderId, String senderName, String senderRole,
            String content, LocalDateTime createdAt) {
        this.id = id;
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderRole = senderRole;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderRole() {
        return senderRole;
    }

    public void setSenderRole(String senderRole) {
        this.senderRole = senderRole;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
