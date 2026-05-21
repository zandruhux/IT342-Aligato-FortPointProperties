package edu.cit.aligato.fortpointproperties.messaging.repository;

import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import edu.cit.aligato.fortpointproperties.messaging.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversationIdOrderByCreatedAtAsc(Long conversationId);

    Optional<Message> findTopByConversationIdOrderByCreatedAtDesc(Long conversationId);

    @Query("""
            SELECT COUNT(m)
            FROM Message m
            WHERE m.conversationId = :conversationId
            AND m.senderId <> :userId
            AND (:lastReadAt IS NULL OR m.createdAt > :lastReadAt)
            """)
    long countUnreadMessages(@Param("conversationId") Long conversationId,
            @Param("userId") String userId,
            @Param("lastReadAt") LocalDateTime lastReadAt);
}
