package edu.cit.aligato.fortpointproperties.messaging.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.cit.aligato.fortpointproperties.messaging.entity.ConversationReadState;

@Repository
public interface ConversationReadStateRepository extends JpaRepository<ConversationReadState, Long> {
    Optional<ConversationReadState> findByConversationIdAndUserId(Long conversationId, String userId);
}
