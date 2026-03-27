package edu.cit.aligato.fortpointproperties.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.aligato.fortpointproperties.dto.ApiResponse;
import edu.cit.aligato.fortpointproperties.dto.AuthResponse;
import edu.cit.aligato.fortpointproperties.dto.ErrorDetail;
import edu.cit.aligato.fortpointproperties.dto.LoginRequest;
import edu.cit.aligato.fortpointproperties.dto.RegisterRequest;
import edu.cit.aligato.fortpointproperties.dto.UserDTO;
import edu.cit.aligato.fortpointproperties.entity.User;
import edu.cit.aligato.fortpointproperties.security.JwtUtil;
import edu.cit.aligato.fortpointproperties.service.AuthService;
import edu.cit.aligato.fortpointproperties.service.GoogleAuthService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final GoogleAuthService googleAuthService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, GoogleAuthService googleAuthService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.googleAuthService = googleAuthService;
        this.jwtUtil = jwtUtil;
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
                user.getRole()
            );

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
                user.getRole()
            );

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

    @PostMapping("/google/login")
    public ResponseEntity<ApiResponse<AuthResponse>> loginWithGoogle(@RequestParam String googleToken) {
        try {
            // Parse and verify the Google token, extract user info
            User user = googleAuthService.authenticateWithGoogle(googleToken);

            // Generate tokens
            String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole());
            String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

            // Create user DTO
            UserDTO userDTO = new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getFirstname(),
                user.getLastname(),
                user.getRole()
            );

            // Create auth response
            AuthResponse authResponse = new AuthResponse(userDTO, accessToken, refreshToken);

            // Wrap in standardized API response
            ApiResponse<AuthResponse> response = ApiResponse.success(authResponse);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            ErrorDetail error = new ErrorDetail("AUTH-002", e.getMessage(), null);
            ApiResponse<AuthResponse> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            ErrorDetail error = new ErrorDetail("AUTH-003", "Google authentication failed: " + e.getMessage(), null);
            ApiResponse<AuthResponse> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
    }
}
