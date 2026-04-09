package edu.cit.aligato.fortpointproperties.auth.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.aligato.fortpointproperties.auth.dto.AuthResponse;
import edu.cit.aligato.fortpointproperties.auth.dto.LoginRequest;
import edu.cit.aligato.fortpointproperties.auth.dto.RegisterRequest;
import edu.cit.aligato.fortpointproperties.auth.dto.UserDTO;
import edu.cit.aligato.fortpointproperties.auth.service.AuthService;
import edu.cit.aligato.fortpointproperties.dto.ApiResponse;
import edu.cit.aligato.fortpointproperties.dto.ErrorDetail;
import edu.cit.aligato.fortpointproperties.entity.User;
import edu.cit.aligato.fortpointproperties.repository.UserRepository;
import edu.cit.aligato.fortpointproperties.security.JwtUtil;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/v1/auth") //TO CHANGE THIS PART
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, JwtUtil jwtUtil, UserRepository userRepository) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = authService.registerUser(request);

            // Generate tokens
            String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole());
            String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

            // Create user DTO
            UserDTO userDTO = new UserDTO(
                    user.getId(),
                    user.getEmail(),
                    user.getFirstname(),
                    user.getLastname(),
                    user.getRole());

            // Create auth response
            AuthResponse authResponse = new AuthResponse(userDTO, accessToken, refreshToken);

            // Wrap in standardized API response
            ApiResponse<AuthResponse> response = ApiResponse.success(authResponse);

            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            String errorCode = e.getMessage().contains("already in use") ? "DB-002" : "AUTH-001";
            HttpStatus status = errorCode.equals("DB-002") ? HttpStatus.CONFLICT : HttpStatus.BAD_REQUEST;
            ErrorDetail error = new ErrorDetail(errorCode, e.getMessage(), null);
            ApiResponse<AuthResponse> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, status);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            User user = authService.authenticateUser(request);

            // Generate tokens
            String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole());
            String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

            // Create user DTO
            UserDTO userDTO = new UserDTO(
                    user.getId(),
                    user.getEmail(),
                    user.getFirstname(),
                    user.getLastname(),
                    user.getRole());

            // Create auth response
            AuthResponse authResponse = new AuthResponse(userDTO, accessToken, refreshToken);

            // Wrap in standardized API response
            ApiResponse<AuthResponse> response = ApiResponse.success(authResponse);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            ErrorDetail error = new ErrorDetail("AUTH-001", e.getMessage(), null);
            ApiResponse<AuthResponse> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserDTO>> getProfile() {
        try {
            // Get email from JWT token (from security context)
            String email = SecurityContextHolder.getContext().getAuthentication().getName();

            // Find user by email
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            // Create user DTO
            UserDTO userDTO = new UserDTO(
                    user.getId(),
                    user.getEmail(),
                    user.getFirstname(),
                    user.getLastname(),
                    user.getRole());

            // Wrap in standardized API response
            ApiResponse<UserDTO> response = ApiResponse.success(userDTO);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            ErrorDetail error = new ErrorDetail("AUTH-004", e.getMessage(), null);
            ApiResponse<UserDTO> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    //THIS IS ONLY A TEST. KINDLY DELETE AFTER
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOs = users.stream()
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getEmail(),
                        user.getFirstname(),
                        user.getLastname(),
                        user.getRole()))
                .toList();

        ApiResponse<List<UserDTO>> response = ApiResponse.success(userDTOs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
