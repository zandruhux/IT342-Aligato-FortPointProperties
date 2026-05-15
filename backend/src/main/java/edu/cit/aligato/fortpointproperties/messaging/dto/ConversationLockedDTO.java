package edu.cit.aligato.fortpointproperties.messaging.dto;

public class ConversationLockedDTO {
    private String type;
    private Long conversationId;
    private String assignedAgentId;
    private String status;

    public ConversationLockedDTO() {
    }

    public ConversationLockedDTO(String type, Long conversationId, String assignedAgentId, String status) {
        this.type = type;
        this.conversationId = conversationId;
        this.assignedAgentId = assignedAgentId;
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public String getAssignedAgentId() {
        return assignedAgentId;
    }

    public void setAssignedAgentId(String assignedAgentId) {
        this.assignedAgentId = assignedAgentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
