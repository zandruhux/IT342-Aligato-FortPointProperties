package edu.cit.aligato.fortpointproperties.messaging.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
@Table(name = "conversations")
public class Conversation {

    public enum ConversationStatus {
        OPEN,
        ASSIGNED,
        CLOSED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "registered_user_id", nullable = false)
    private String registeredUserId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registered_user_id", referencedColumnName = "id", insertable = false, updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User registeredUser;

    @Column(name = "assigned_agent_id")
    private String assignedAgentId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_agent_id", referencedColumnName = "id", insertable = false, updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User assignedAgent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConversationStatus status = ConversationStatus.OPEN;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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

    public User getRegisteredUser() {
        return registeredUser;
    }

    public String getAssignedAgentId() {
        return assignedAgentId;
    }

    public void setAssignedAgentId(String assignedAgentId) {
        this.assignedAgentId = assignedAgentId;
    }

    public User getAssignedAgent() {
        return assignedAgent;
    }

    public ConversationStatus getStatus() {
        return status;
    }

    public void setStatus(ConversationStatus status) {
        this.status = status;
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
