package edu.cit.aligato.fortpointproperties;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import edu.cit.aligato.fortpointproperties.auth.repository.UserRepository;
import edu.cit.aligato.fortpointproperties.messaging.dto.CreateConversationDTO;
import edu.cit.aligato.fortpointproperties.messaging.dto.MessageDTO;
import edu.cit.aligato.fortpointproperties.messaging.dto.SendMessageDTO;
import edu.cit.aligato.fortpointproperties.messaging.entity.Conversation;
import edu.cit.aligato.fortpointproperties.messaging.entity.Conversation.ConversationStatus;
import edu.cit.aligato.fortpointproperties.messaging.entity.Message;
import edu.cit.aligato.fortpointproperties.messaging.entity.Message.SenderRole;
import edu.cit.aligato.fortpointproperties.messaging.repository.ConversationRepository;
import edu.cit.aligato.fortpointproperties.messaging.repository.MessageRepository;
import edu.cit.aligato.fortpointproperties.messaging.service.MessagingService;

@ExtendWith(MockitoExtension.class)
public class MessagingServiceTest {

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private MessagingService messagingService;

    @Test
    void registeredUserCreatesConversation_savesMessageAndNotifiesAgents() {
        CreateConversationDTO dto = new CreateConversationDTO();
        dto.setContent("Hello, is this property available?");

        when(conversationRepository.save(any(Conversation.class))).thenAnswer(invocation -> {
            Conversation conversation = invocation.getArgument(0);
            conversation.setId(1L);
            return conversation;
        });
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

        messagingService.createConversation(dto, "u1");

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository).save(messageCaptor.capture());
        assertEquals(1L, messageCaptor.getValue().getConversationId());
        assertEquals("u1", messageCaptor.getValue().getSenderId());
        assertEquals(SenderRole.REGISTERED_USER, messageCaptor.getValue().getSenderRole());
        verify(messagingTemplate).convertAndSend(eq("/topic/agents/inbox"), any(Object.class));
    }

    @Test
    void firstAgentReplyLocksConversationAndSavesMessage() {
        Conversation open = conversation(1L, "u1", null, ConversationStatus.OPEN);
        Conversation assigned = conversation(1L, "u1", "a1", ConversationStatus.ASSIGNED);
        SendMessageDTO dto = message("I can help.");

        when(conversationRepository.findById(1L)).thenReturn(Optional.of(open), Optional.of(assigned));
        when(conversationRepository.lockConversation(1L, "a1")).thenReturn(1);
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MessageDTO result = messagingService.sendMessage(1L, dto, "a1", "AGENT");

        assertEquals("a1", result.getSenderId());
        verify(conversationRepository).lockConversation(1L, "a1");
        verify(messagingTemplate).convertAndSend(eq("/topic/agents/conversations/1/locked"), any(Object.class));
        verify(messagingTemplate).convertAndSendToUser(eq("u1"), eq("/queue/messages"), any(Object.class));
    }

    @Test
    void secondAgentCannotReplyToAssignedConversation() {
        Conversation assigned = conversation(1L, "u1", "a1", ConversationStatus.ASSIGNED);

        when(conversationRepository.findById(1L)).thenReturn(Optional.of(assigned));

        assertThrows(IllegalStateException.class,
                () -> messagingService.sendMessage(1L, message("Taking this."), "a2", "AGENT"));

        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    void registeredUserReplyOnlyNotifiesAssignedAgent() {
        Conversation assigned = conversation(1L, "u1", "a1", ConversationStatus.ASSIGNED);
        when(conversationRepository.findById(1L)).thenReturn(Optional.of(assigned), Optional.of(assigned));
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

        messagingService.sendMessage(1L, message("Thank you."), "u1", "REGISTERED_USER");

        verify(messagingTemplate).convertAndSendToUser(eq("a1"), eq("/queue/messages"), any(Object.class));
        verify(messagingTemplate, never()).convertAndSendToUser(eq("u1"), eq("/queue/messages"), any(Object.class));
    }

    @Test
    void assignedAgentReplyOnlyNotifiesRegisteredUser() {
        Conversation assigned = conversation(1L, "u1", "a1", ConversationStatus.ASSIGNED);
        when(conversationRepository.findById(1L)).thenReturn(Optional.of(assigned), Optional.of(assigned));
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

        messagingService.sendMessage(1L, message("You are welcome."), "a1", "AGENT");

        verify(messagingTemplate).convertAndSendToUser(eq("u1"), eq("/queue/messages"), any(Object.class));
        verify(messagingTemplate, never()).convertAndSendToUser(eq("a1"), eq("/queue/messages"), any(Object.class));
    }

    @Test
    void publicAndAdminRolesCannotSendMessages() {
        assertThrows(SecurityException.class,
                () -> messagingService.sendMessage(1L, message("Hello."), "public1", "PUBLIC"));
        assertThrows(SecurityException.class,
                () -> messagingService.sendMessage(1L, message("Hello."), "admin1", "ADMIN"));
    }

    @Test
    void getMessagesReturnsPersistedHistoryForConversationParticipant() {
        Conversation assigned = conversation(1L, "u1", "a1", ConversationStatus.ASSIGNED);
        Message saved = new Message();
        saved.setId(10L);
        saved.setConversationId(1L);
        saved.setSenderId("u1");
        saved.setSenderRole(SenderRole.REGISTERED_USER);
        saved.setContent("Still saved.");

        when(conversationRepository.findById(1L)).thenReturn(Optional.of(assigned));
        when(messageRepository.findByConversationIdOrderByCreatedAtAsc(1L)).thenReturn(List.of(saved));

        List<MessageDTO> results = messagingService.getMessages(1L, "u1", "REGISTERED_USER");

        assertEquals(1, results.size());
        assertEquals("Still saved.", results.get(0).getContent());
    }

    private Conversation conversation(Long id, String registeredUserId, String assignedAgentId,
            ConversationStatus status) {
        Conversation conversation = new Conversation();
        conversation.setId(id);
        conversation.setRegisteredUserId(registeredUserId);
        conversation.setAssignedAgentId(assignedAgentId);
        conversation.setStatus(status);
        return conversation;
    }

    private SendMessageDTO message(String content) {
        SendMessageDTO dto = new SendMessageDTO();
        dto.setContent(content);
        return dto;
    }
}
