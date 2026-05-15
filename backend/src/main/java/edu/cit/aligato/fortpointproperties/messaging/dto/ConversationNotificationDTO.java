package edu.cit.aligato.fortpointproperties.messaging.dto;

public class ConversationNotificationDTO {
    private String type;
    private Long conversationId;
    private String registeredUserId;
    private String registeredUserName;
    private String preview;
    private String status;

    public ConversationNotificationDTO() {
    }

    public ConversationNotificationDTO(String type, Long conversationId, String registeredUserId, String preview,
            String status) {
        this.type = type;
        this.conversationId = conversationId;
        this.registeredUserId = registeredUserId;
        this.preview = preview;
        this.status = status;
    }

    public ConversationNotificationDTO(String type, Long conversationId, String registeredUserId,
            String registeredUserName, String preview, String status) {
        this.type = type;
        this.conversationId = conversationId;
        this.registeredUserId = registeredUserId;
        this.registeredUserName = registeredUserName;
        this.preview = preview;
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

    public String getRegisteredUserId() {
        return registeredUserId;
    }

    public void setRegisteredUserId(String registeredUserId) {
        this.registeredUserId = registeredUserId;
    }

    public String getRegisteredUserName() {
        return registeredUserName;
    }

    public void setRegisteredUserName(String registeredUserName) {
        this.registeredUserName = registeredUserName;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
