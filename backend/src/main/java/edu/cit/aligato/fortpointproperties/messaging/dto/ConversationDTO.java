package edu.cit.aligato.fortpointproperties.messaging.dto;

import java.time.LocalDateTime;

public class ConversationDTO {
    private Long id;
    private String registeredUserId;
    private String assignedAgentId;
    private String registeredUserName;
    private String assignedAgentName;
    private String status;
    private String latestMessagePreview;
    private String latestMessageSenderId;
    private String latestMessageSenderName;
    private LocalDateTime latestMessageAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ConversationDTO() {
    }

    public ConversationDTO(Long id, String registeredUserId, String assignedAgentId, String status,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.registeredUserId = registeredUserId;
        this.assignedAgentId = assignedAgentId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public ConversationDTO(Long id, String registeredUserId, String assignedAgentId, String registeredUserName,
            String assignedAgentName, String status, String latestMessagePreview, String latestMessageSenderId,
            LocalDateTime latestMessageAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.registeredUserId = registeredUserId;
        this.assignedAgentId = assignedAgentId;
        this.registeredUserName = registeredUserName;
        this.assignedAgentName = assignedAgentName;
        this.status = status;
        this.latestMessagePreview = latestMessagePreview;
        this.latestMessageSenderId = latestMessageSenderId;
        this.latestMessageAt = latestMessageAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public ConversationDTO(Long id, String registeredUserId, String assignedAgentId, String registeredUserName,
            String assignedAgentName, String status, String latestMessagePreview, String latestMessageSenderId,
            String latestMessageSenderName, LocalDateTime latestMessageAt, LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.registeredUserId = registeredUserId;
        this.assignedAgentId = assignedAgentId;
        this.registeredUserName = registeredUserName;
        this.assignedAgentName = assignedAgentName;
        this.status = status;
        this.latestMessagePreview = latestMessagePreview;
        this.latestMessageSenderId = latestMessageSenderId;
        this.latestMessageSenderName = latestMessageSenderName;
        this.latestMessageAt = latestMessageAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRegisteredUserId() {
        return registeredUserId;
    }

    public void setRegisteredUserId(String registeredUserId) {
        this.registeredUserId = registeredUserId;
    }

    public String getAssignedAgentId() {
        return assignedAgentId;
    }

    public void setAssignedAgentId(String assignedAgentId) {
        this.assignedAgentId = assignedAgentId;
    }

    public String getRegisteredUserName() {
        return registeredUserName;
    }

    public void setRegisteredUserName(String registeredUserName) {
        this.registeredUserName = registeredUserName;
    }

    public String getAssignedAgentName() {
        return assignedAgentName;
    }

    public void setAssignedAgentName(String assignedAgentName) {
        this.assignedAgentName = assignedAgentName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLatestMessagePreview() {
        return latestMessagePreview;
    }

    public void setLatestMessagePreview(String latestMessagePreview) {
        this.latestMessagePreview = latestMessagePreview;
    }

    public String getLatestMessageSenderId() {
        return latestMessageSenderId;
    }

    public void setLatestMessageSenderId(String latestMessageSenderId) {
        this.latestMessageSenderId = latestMessageSenderId;
    }

    public String getLatestMessageSenderName() {
        return latestMessageSenderName;
    }

    public void setLatestMessageSenderName(String latestMessageSenderName) {
        this.latestMessageSenderName = latestMessageSenderName;
    }

    public LocalDateTime getLatestMessageAt() {
        return latestMessageAt;
    }

    public void setLatestMessageAt(LocalDateTime latestMessageAt) {
        this.latestMessageAt = latestMessageAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
