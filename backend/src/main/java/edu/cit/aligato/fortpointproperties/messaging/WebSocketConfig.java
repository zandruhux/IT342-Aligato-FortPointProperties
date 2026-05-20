package edu.cit.aligato.fortpointproperties.messaging;

import java.security.Principal;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import edu.cit.aligato.fortpointproperties.auth.entity.User;
import edu.cit.aligato.fortpointproperties.auth.repository.UserRepository;
import edu.cit.aligato.fortpointproperties.shared.security.JwtUtil;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public WebSocketConfig(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/app");
        config.enableSimpleBroker("/topic", "/queue", "/user");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    authenticate(accessor);
                }
                return message;
            }
        });
    }

    private void authenticate(StompHeaderAccessor accessor) {
        String authorization = accessor.getFirstNativeHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return;
        }

        String token = authorization.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return;
        }

        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return;
        }

        String role = normalizeRole(user.getRole());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                (Principal) user::getId, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
        accessor.setUser(authentication);
    }

    private String normalizeRole(String role) {
        if ("registered_user".equalsIgnoreCase(role) || "USER".equalsIgnoreCase(role)) {
            return "REGISTERED_USER";
        }
        return role == null ? "" : role.toUpperCase();
    }
}
