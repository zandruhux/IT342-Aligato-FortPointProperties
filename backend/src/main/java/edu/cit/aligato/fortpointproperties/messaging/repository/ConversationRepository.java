package edu.cit.aligato.fortpointproperties.messaging.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import edu.cit.aligato.fortpointproperties.messaging.entity.Conversation;
import edu.cit.aligato.fortpointproperties.messaging.entity.Conversation.ConversationStatus;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findByRegisteredUserIdOrderByUpdatedAtDesc(String registeredUserId);

    List<Conversation> findByStatusOrAssignedAgentIdOrderByUpdatedAtDesc(ConversationStatus status, String agentId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
            UPDATE conversations
            SET assigned_agent_id = :agentId,
                status = 'ASSIGNED',
                updated_at = NOW()
            WHERE id = :conversationId
            AND assigned_agent_id IS NULL
            """, nativeQuery = true)
    int lockConversation(@Param("conversationId") Long conversationId, @Param("agentId") String agentId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE conversations SET updated_at = NOW() WHERE id = :conversationId", nativeQuery = true)
    int touchConversation(@Param("conversationId") Long conversationId);
}
