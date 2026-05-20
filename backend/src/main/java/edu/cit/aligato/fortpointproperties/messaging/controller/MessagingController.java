package edu.cit.aligato.fortpointproperties.messaging.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.aligato.fortpointproperties.auth.entity.User;
import edu.cit.aligato.fortpointproperties.auth.repository.UserRepository;
import edu.cit.aligato.fortpointproperties.messaging.dto.ConversationDTO;
import edu.cit.aligato.fortpointproperties.messaging.dto.CreateConversationDTO;
import edu.cit.aligato.fortpointproperties.messaging.dto.MessageDTO;
import edu.cit.aligato.fortpointproperties.messaging.dto.SendMessageDTO;
import edu.cit.aligato.fortpointproperties.messaging.service.MessagingService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/messaging")
@PreAuthorize("hasAnyRole('REGISTERED_USER', 'AGENT')")
public class MessagingController {
    private final MessagingService messagingService;
    private final UserRepository userRepository;

    public MessagingController(MessagingService messagingService, UserRepository userRepository) {
        this.messagingService = messagingService;
        this.userRepository = userRepository;
    }

    @PostMapping("/conversations")
    @PreAuthorize("hasRole('REGISTERED_USER')")
    public ResponseEntity<ConversationDTO> createConversation(@Valid @RequestBody CreateConversationDTO dto,
            Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        return new ResponseEntity<>(messagingService.createConversation(dto, user.getId()), HttpStatus.CREATED);
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationDTO>> getConversations(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        String role = normalizeRole(user.getRole());

        if ("AGENT".equals(role)) {
            return ResponseEntity.ok(messagingService.getAgentInbox(user.getId()));
        }

        return ResponseEntity.ok(messagingService.getRegisteredUserConversations(user.getId()));
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<List<MessageDTO>> getMessages(@PathVariable Long conversationId, Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        return ResponseEntity.ok(messagingService.getMessages(conversationId, user.getId(), user.getRole()));
    }

    @PostMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<MessageDTO> sendMessage(@PathVariable Long conversationId,
            @Valid @RequestBody SendMessageDTO dto, Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        return new ResponseEntity<>(messagingService.sendMessage(conversationId, dto, user.getId(), user.getRole()),
                HttpStatus.CREATED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Invalid request");
        return new ResponseEntity<>(Map.of("error", message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleConflict(IllegalStateException exception) {
        return new ResponseEntity<>(Map.of("error", exception.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, String>> handleForbidden(SecurityException exception) {
        return new ResponseEntity<>(Map.of("error", exception.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException exception) {
        return new ResponseEntity<>(Map.of("error", exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    private User getAuthenticatedUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private String normalizeRole(String role) {
        if ("registered_user".equalsIgnoreCase(role) || "USER".equalsIgnoreCase(role)) {
            return "REGISTERED_USER";
        }
        return role == null ? "" : role.toUpperCase();
    }
}
