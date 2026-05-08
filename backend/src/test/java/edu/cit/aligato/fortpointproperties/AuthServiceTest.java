package edu.cit.aligato.fortpointproperties;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import edu.cit.aligato.fortpointproperties.auth.dto.LoginRequest;
import edu.cit.aligato.fortpointproperties.auth.dto.RegisterRequest;
import edu.cit.aligato.fortpointproperties.auth.entity.User;
import edu.cit.aligato.fortpointproperties.auth.repository.UserRepository;
import edu.cit.aligato.fortpointproperties.auth.service.AuthService;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerUser_success() {
        RegisterRequest req = new RegisterRequest("John", "Doe", "john@example.com", "StrongP@ssw0rd!", "StrongP@ssw0rd!");

        when(userRepository.existsByEmail(req.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(req.getPassword())).thenReturn("hashed-password");
        User saved = new User();
        saved.setId("user-1");
        saved.setEmail(req.getEmail());
        when(userRepository.save(any(User.class))).thenReturn(saved);

        User result = authService.registerUser(req);

        assertNotNull(result);
        assertEquals("john@example.com", result.getEmail());
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("John", userCaptor.getValue().getFirstname());
        assertEquals("Doe", userCaptor.getValue().getLastname());
        assertEquals("hashed-password", userCaptor.getValue().getPasswordHash());
        assertEquals("registered_user", userCaptor.getValue().getRole());
    }

    @Test
    void registerUser_duplicateEmail_throwsAndDoesNotSave() {
        RegisterRequest req = new RegisterRequest("John", "Doe", "john@example.com", "StrongP@ssw0rd!", "StrongP@ssw0rd!");

        when(userRepository.existsByEmail(req.getEmail())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.registerUser(req));

        assertEquals("Email is already in use", exception.getMessage());
        verify(passwordEncoder, never()).encode(any(String.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_weakPassword_throwsAndDoesNotSave() {
        RegisterRequest req = new RegisterRequest("John", "Doe", "john@example.com", "short", "short");

        when(userRepository.existsByEmail(req.getEmail())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.registerUser(req));

        assertEquals("Password must be at least 8 characters long", exception.getMessage());
        verify(passwordEncoder, never()).encode(any(String.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void authenticateUser_success() {
        LoginRequest req = new LoginRequest("jane@example.com", "password123");
        User user = new User();
        user.setEmail(req.getEmail());
        user.setPasswordHash("encoded-pass");

        when(userRepository.findByEmail(req.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(req.getPassword(), user.getPasswordHash())).thenReturn(true);

        User result = authService.authenticateUser(req);

        assertNotNull(result);
        assertEquals(req.getEmail(), result.getEmail());
    }

    @Test
    void authenticateUser_invalidPassword_throws() {
        LoginRequest req = new LoginRequest("jane@example.com", "badpass");
        User user = new User();
        user.setEmail(req.getEmail());
        user.setPasswordHash("encoded-pass");

        when(userRepository.findByEmail(req.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(req.getPassword(), user.getPasswordHash())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> authService.authenticateUser(req));
    }

    @Test
    void authenticateUser_unknownEmail_throwsGenericMessage() {
        LoginRequest req = new LoginRequest("missing@example.com", "password123");

        when(userRepository.findByEmail(req.getEmail())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.authenticateUser(req));

        assertEquals("Invalid email or password", exception.getMessage());
        verify(passwordEncoder, never()).matches(any(String.class), any(String.class));
    }

    @Test
    void getUserByEmail_found() {
        User user = new User();
        user.setEmail("jane@example.com");

        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));

        User result = authService.getUserByEmail("jane@example.com");

        assertEquals("jane@example.com", result.getEmail());
    }

    @Test
    void getUserByEmail_missing_throws() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.getUserByEmail("missing@example.com"));

        assertEquals("User not found", exception.getMessage());
    }
}
