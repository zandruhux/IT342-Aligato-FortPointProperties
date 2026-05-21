package edu.cit.aligato.fortpointproperties.messaging.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.cit.aligato.fortpointproperties.auth.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "messages")
public class Message {

    public enum SenderRole {
        REGISTERED_USER,
        AGENT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conversation_id", nullable = false)
    private Long conversationId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", referencedColumnName = "id", insertable = false, updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Conversation conversation;

    @Column(name = "sender_id", nullable = false)
    private String senderId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", referencedColumnName = "id", insertable = false, updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User sender;

    @Enumerated(EnumType.STRING)
    @Column(name = "sender_role", nullable = false)
    private SenderRole senderRole;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

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

    public Conversation getConversation() {
        return conversation;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public User getSender() {
        return sender;
    }

    public SenderRole getSenderRole() {
        return senderRole;
    }

    public void setSenderRole(SenderRole senderRole) {
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
