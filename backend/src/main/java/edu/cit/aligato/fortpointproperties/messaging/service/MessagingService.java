package edu.cit.aligato.fortpointproperties.messaging.service;

import java.util.List;
import java.util.Optional;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.cit.aligato.fortpointproperties.auth.entity.User;
import edu.cit.aligato.fortpointproperties.auth.repository.UserRepository;
import edu.cit.aligato.fortpointproperties.messaging.dto.ConversationDTO;
import edu.cit.aligato.fortpointproperties.messaging.dto.ConversationLockedDTO;
import edu.cit.aligato.fortpointproperties.messaging.dto.ConversationNotificationDTO;
import edu.cit.aligato.fortpointproperties.messaging.dto.CreateConversationDTO;
import edu.cit.aligato.fortpointproperties.messaging.dto.MessageDTO;
import edu.cit.aligato.fortpointproperties.messaging.dto.SendMessageDTO;
import edu.cit.aligato.fortpointproperties.messaging.entity.Conversation;
import edu.cit.aligato.fortpointproperties.messaging.entity.Conversation.ConversationStatus;
import edu.cit.aligato.fortpointproperties.messaging.entity.Message;
import edu.cit.aligato.fortpointproperties.messaging.entity.Message.SenderRole;
import edu.cit.aligato.fortpointproperties.messaging.repository.ConversationRepository;
import edu.cit.aligato.fortpointproperties.messaging.repository.MessageRepository;

@Service
public class MessagingService {
    private static final String REGISTERED_USER_ROLE = "REGISTERED_USER";
    private static final String AGENT_ROLE = "AGENT";

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public MessagingService(ConversationRepository conversationRepository, MessageRepository messageRepository,
            UserRepository userRepository, SimpMessagingTemplate messagingTemplate) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public ConversationDTO createConversation(CreateConversationDTO dto, String registeredUserId) {
        validateSupportedRole(REGISTERED_USER_ROLE);
        String content = normalizeContent(dto.getContent());

        Conversation conversation = new Conversation();
        conversation.setRegisteredUserId(registeredUserId);
        conversation.setStatus(ConversationStatus.OPEN);
        Conversation savedConversation = conversationRepository.save(conversation);

        Message message = buildMessage(savedConversation.getId(), registeredUserId, SenderRole.REGISTERED_USER, content);
        messageRepository.save(message);

        messagingTemplate.convertAndSend("/topic/agents/inbox",
                new ConversationNotificationDTO("NEW_CONVERSATION", savedConversation.getId(), registeredUserId,
                        userDisplayName(registeredUserId), preview(content), savedConversation.getStatus().name()));

        return toConversationDTO(savedConversation);
    }

    @Transactional
    public MessageDTO sendMessage(Long conversationId, SendMessageDTO dto, String senderId, String senderRole) {
        String normalizedRole = normalizeRole(senderRole);
        validateSupportedRole(normalizedRole);
        String content = normalizeContent(dto.getContent());

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        if (REGISTERED_USER_ROLE.equals(normalizedRole)) {
            validateRegisteredUserAccess(conversation, senderId);
        } else {
            validateAgentAccessForSend(conversationId, conversation, senderId);
        }

        Message savedMessage = messageRepository.save(buildMessage(conversationId, senderId,
                SenderRole.valueOf(normalizedRole), content));
        conversationRepository.touchConversation(conversationId);

        Conversation currentConversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));
        MessageDTO messageDTO = toMessageDTO(savedMessage);

        if (REGISTERED_USER_ROLE.equals(normalizedRole)) {
            if (currentConversation.getAssignedAgentId() != null) {
                messagingTemplate.convertAndSendToUser(currentConversation.getAssignedAgentId(), "/queue/messages",
                        withType(messageDTO));
            }
        } else {
            messagingTemplate.convertAndSendToUser(currentConversation.getRegisteredUserId(), "/queue/messages",
                    withType(messageDTO));
        }

        return messageDTO;
    }

    @Transactional(readOnly = true)
    public List<ConversationDTO> getRegisteredUserConversations(String registeredUserId) {
        return conversationRepository.findByRegisteredUserIdOrderByUpdatedAtDesc(registeredUserId).stream()
                .map(this::toConversationDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ConversationDTO> getAgentInbox(String agentId) {
        return conversationRepository.findByStatusOrAssignedAgentIdOrderByUpdatedAtDesc(ConversationStatus.OPEN, agentId).stream()
                .map(this::toConversationDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MessageDTO> getMessages(Long conversationId, String userId, String role) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));
        String normalizedRole = normalizeRole(role);
        validateSupportedRole(normalizedRole);

        if (REGISTERED_USER_ROLE.equals(normalizedRole)) {
            validateRegisteredUserAccess(conversation, userId);
        } else if (!userId.equals(conversation.getAssignedAgentId())) {
            throw new SecurityException("Only the assigned agent can view this conversation");
        }

        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId).stream()
                .map(this::toMessageDTO)
                .toList();
    }

    private void validateAgentAccessForSend(Long conversationId, Conversation conversation, String agentId) {
        if (conversation.getAssignedAgentId() == null) {
            int updatedRows = conversationRepository.lockConversation(conversationId, agentId);
            Conversation reloadedConversation = conversationRepository.findById(conversationId)
                    .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

            if (updatedRows == 1) {
                messagingTemplate.convertAndSend("/topic/agents/conversations/" + conversationId + "/locked",
                        new ConversationLockedDTO("CONVERSATION_LOCKED", conversationId, agentId,
                                ConversationStatus.ASSIGNED.name()));
                return;
            }

            if (agentId.equals(reloadedConversation.getAssignedAgentId())) {
                return;
            }

            throw new IllegalStateException("Conversation is already assigned to another agent");
        }

        if (!agentId.equals(conversation.getAssignedAgentId())) {
            throw new IllegalStateException("Conversation is already assigned to another agent");
        }
    }

    private void validateRegisteredUserAccess(Conversation conversation, String registeredUserId) {
        if (!registeredUserId.equals(conversation.getRegisteredUserId())) {
            throw new SecurityException("Registered user can only access their own conversations");
        }
    }

    private Message buildMessage(Long conversationId, String senderId, SenderRole senderRole, String content) {
        Message message = new Message();
        message.setConversationId(conversationId);
        message.setSenderId(senderId);
        message.setSenderRole(senderRole);
        message.setContent(content);
        return message;
    }

    private String normalizeContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Message content is required");
        }
        return content.trim();
    }

    private String normalizeRole(String role) {
        if (role == null) {
            return "";
        }
        if ("registered_user".equalsIgnoreCase(role) || "USER".equalsIgnoreCase(role)) {
            return REGISTERED_USER_ROLE;
        }
        return role.toUpperCase();
    }

    private void validateSupportedRole(String role) {
        if (!REGISTERED_USER_ROLE.equals(role) && !AGENT_ROLE.equals(role)) {
            throw new SecurityException("Messaging is only available to registered users and agents");
        }
    }

    private String preview(String content) {
        return content.length() > 120 ? content.substring(0, 120) : content;
    }

    private ConversationDTO toConversationDTO(Conversation conversation) {
        Optional<Message> latestMessage = messageRepository.findTopByConversationIdOrderByCreatedAtDesc(conversation.getId());
        return new ConversationDTO(conversation.getId(), conversation.getRegisteredUserId(),
                conversation.getAssignedAgentId(), userDisplayName(conversation.getRegisteredUserId()),
                userDisplayName(conversation.getAssignedAgentId()), conversation.getStatus().name(),
                latestMessage.map(message -> preview(message.getContent())).orElse(""),
                latestMessage.map(Message::getSenderId).orElse(null),
                latestMessage.map(message -> userDisplayName(message.getSenderId())).orElse(null),
                latestMessage.map(Message::getCreatedAt).orElse(null),
                conversation.getCreatedAt(), conversation.getUpdatedAt());
    }

    private MessageDTO toMessageDTO(Message message) {
        return new MessageDTO(message.getId(), message.getConversationId(), message.getSenderId(),
                userDisplayName(message.getSenderId()),
                message.getSenderRole().name(), message.getContent(), message.getCreatedAt());
    }

    private String userDisplayName(String userId) {
        if (userId == null) {
            return null;
        }

        Optional<User> userById = userRepository.findById(userId);
        if (userById != null && userById.isPresent()) {
            return formatUserName(userById.get());
        }

        Optional<User> userByEmail = userRepository.findByEmail(userId);
        if (userByEmail != null && userByEmail.isPresent()) {
            return formatUserName(userByEmail.get());
        }

        return null;
    }

    private String formatUserName(User user) {
        String firstName = user.getFirstname() == null ? "" : user.getFirstname().trim();
        String lastName = user.getLastname() == null ? "" : user.getLastname().trim();
        String fullName = (firstName + " " + lastName).trim();
        return fullName.isEmpty() ? user.getEmail() : fullName;
    }

    private Object withType(MessageDTO messageDTO) {
        return new Object() {
            public final String type = "NEW_MESSAGE";
            public final Long conversationId = messageDTO.getConversationId();
            public final String senderId = messageDTO.getSenderId();
            public final String senderName = messageDTO.getSenderName();
            public final String senderRole = messageDTO.getSenderRole();
            public final String content = messageDTO.getContent();
            public final java.time.LocalDateTime createdAt = messageDTO.getCreatedAt();
        };
    }
}
