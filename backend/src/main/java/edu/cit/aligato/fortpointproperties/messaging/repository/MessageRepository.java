package edu.cit.aligato.fortpointproperties.messaging.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.cit.aligato.fortpointproperties.messaging.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversationIdOrderByCreatedAtAsc(Long conversationId);

    Optional<Message> findTopByConversationIdOrderByCreatedAtDesc(Long conversationId);
}
