package edu.cit.aligato.fortpointproperties;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import edu.cit.aligato.fortpointproperties.auth.controller.AuthController;
import edu.cit.aligato.fortpointproperties.auth.dto.ApiResponse;
import edu.cit.aligato.fortpointproperties.auth.dto.AuthResponse;
import edu.cit.aligato.fortpointproperties.auth.dto.LoginRequest;
import edu.cit.aligato.fortpointproperties.auth.dto.RegisterRequest;
import edu.cit.aligato.fortpointproperties.auth.dto.UserDTO;
import edu.cit.aligato.fortpointproperties.auth.entity.User;
import edu.cit.aligato.fortpointproperties.auth.repository.UserRepository;
import edu.cit.aligato.fortpointproperties.auth.service.AuthService;
import edu.cit.aligato.fortpointproperties.shared.security.JwtUtil;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthController authController;

    @Test
    void register_returnsCreated_andTokens() {
        RegisterRequest req = new RegisterRequest("Alice", "Smith", "alice@example.com", "Pass123!", "Pass123!");
        User user = new User();
        user.setId("u1");
        user.setEmail(req.getEmail());
        user.setFirstname(req.getFirstname());
        user.setLastname(req.getLastname());
        user.setRole("registered_user");

        when(authService.registerUser(any(RegisterRequest.class))).thenReturn(user);
        when(jwtUtil.generateAccessToken(user.getEmail(), user.getRole())).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(user.getEmail())).thenReturn("refresh-token");

        ResponseEntity<ApiResponse<AuthResponse>> response = authController.register(req);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("access-token", response.getBody().getData().getAccessToken());
    }

    @Test
    void register_duplicateEmail_returnsConflict() {
        RegisterRequest req = new RegisterRequest("Alice", "Smith", "alice@example.com", "Pass123!", "Pass123!");

        when(authService.registerUser(any(RegisterRequest.class)))
                .thenThrow(new IllegalArgumentException("Email is already in use"));

        ResponseEntity<ApiResponse<AuthResponse>> response = authController.register(req);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(!response.getBody().isSuccess());
        assertEquals("DB-002", response.getBody().getError().getCode());
    }

    @Test
    void register_invalidRequest_returnsBadRequest() {
        RegisterRequest req = new RegisterRequest("Alice", "Smith", "alice@example.com", "short", "short");

        when(authService.registerUser(any(RegisterRequest.class)))
                .thenThrow(new IllegalArgumentException("Password must be at least 8 characters long"));

        ResponseEntity<ApiResponse<AuthResponse>> response = authController.register(req);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(!response.getBody().isSuccess());
        assertEquals("AUTH-001", response.getBody().getError().getCode());
    }

    @Test
    void login_returnsOk_andTokens() {
        LoginRequest req = new LoginRequest("bob@example.com", "pwd");
        User user = new User();
        user.setId("u2");
        user.setEmail(req.getEmail());
        user.setFirstname("Bob");
        user.setLastname("Builder");
        user.setRole("registered_user");

        when(authService.authenticateUser(any(LoginRequest.class))).thenReturn(user);
        when(jwtUtil.generateAccessToken(user.getEmail(), user.getRole())).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(user.getEmail())).thenReturn("refresh-token");

        ResponseEntity<ApiResponse<AuthResponse>> response = authController.login(req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("refresh-token", response.getBody().getData().getRefreshToken());
    }

    @Test
    void login_invalidCredentials_returnsUnauthorized() {
        LoginRequest req = new LoginRequest("bob@example.com", "bad");

        when(authService.authenticateUser(any(LoginRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid email or password"));

        ResponseEntity<ApiResponse<AuthResponse>> response = authController.login(req);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(!response.getBody().isSuccess());
        assertEquals("AUTH-001", response.getBody().getError().getCode());
    }

    @Test
    void getProfile_returnsAuthenticatedUser() {
        User user = new User();
        user.setId("u3");
        user.setEmail("profile@example.com");
        user.setFirstname("Profile");
        user.setLastname("User");
        user.setRole("registered_user");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user.getEmail(), null));

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        ResponseEntity<ApiResponse<UserDTO>> response = authController.getProfile();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("profile@example.com", response.getBody().getData().getEmail());
        assertEquals("registered_user", response.getBody().getData().getRole());

        SecurityContextHolder.clearContext();
    }

    @Test
    void getProfile_missingUser_returnsNotFound() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("missing@example.com", null));

        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        ResponseEntity<ApiResponse<UserDTO>> response = authController.getProfile();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(!response.getBody().isSuccess());
        assertEquals("AUTH-004", response.getBody().getError().getCode());

        SecurityContextHolder.clearContext();
    }

    @Test
    void getAllUsers_mapsUsersToDTOs() {
        User admin = new User();
        admin.setId("u4");
        admin.setEmail("admin@example.com");
        admin.setFirstname("Admin");
        admin.setLastname("User");
        admin.setRole("admin");
        User agent = new User();
        agent.setId("u5");
        agent.setEmail("agent@example.com");
        agent.setFirstname("Agent");
        agent.setLastname("User");
        agent.setRole("agent");

        when(userRepository.findAll()).thenReturn(List.of(admin, agent));

        ResponseEntity<ApiResponse<List<UserDTO>>> response = authController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getData().size());
        assertEquals("admin@example.com", response.getBody().getData().get(0).getEmail());
        assertEquals("agent", response.getBody().getData().get(1).getRole());
    }
}
