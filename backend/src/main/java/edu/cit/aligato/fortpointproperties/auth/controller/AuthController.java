package edu.cit.aligato.fortpointproperties.auth.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import edu.cit.aligato.fortpointproperties.auth.dto.ApiResponse;
import edu.cit.aligato.fortpointproperties.auth.dto.AuthResponse;
import edu.cit.aligato.fortpointproperties.auth.dto.ErrorDetail;
import edu.cit.aligato.fortpointproperties.auth.dto.LoginRequest;
import edu.cit.aligato.fortpointproperties.auth.dto.RegisterRequest;
import edu.cit.aligato.fortpointproperties.auth.dto.UpdateProfileRequest;
import edu.cit.aligato.fortpointproperties.auth.dto.UserDTO;
import edu.cit.aligato.fortpointproperties.auth.entity.User;
import edu.cit.aligato.fortpointproperties.auth.repository.UserRepository;
import edu.cit.aligato.fortpointproperties.auth.service.AuthService;
import edu.cit.aligato.fortpointproperties.shared.security.JwtUtil;
import jakarta.validation.Valid;


@RestController
@RequestMapping({"/api/v1/auth", "/api/auth"})
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

            String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole());
            String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

            UserDTO userDTO = toUserDTO(user);

            AuthResponse authResponse = new AuthResponse(userDTO, accessToken, refreshToken);

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

            String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole());
            String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

            UserDTO userDTO = toUserDTO(user);

            AuthResponse authResponse = new AuthResponse(userDTO, accessToken, refreshToken);

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
            String email = SecurityContextHolder.getContext().getAuthentication().getName();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            UserDTO userDTO = toUserDTO(user);

            ApiResponse<UserDTO> response = ApiResponse.success(userDTO);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            ErrorDetail error = new ErrorDetail("AUTH-004", e.getMessage(), null);
            ApiResponse<UserDTO> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser() {
        return getProfile();
    }

    @PutMapping({"/profile", "/me"})
    public ResponseEntity<ApiResponse<UserDTO>> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            UserDTO userDTO = authService.updateProfile(email, request);
            return new ResponseEntity<>(ApiResponse.success(userDTO), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            ErrorDetail error = new ErrorDetail("AUTH-007", e.getMessage(), null);
            ApiResponse<UserDTO> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = {"/profile-image", "/me/profile-image"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserDTO>> updateProfileImage(@RequestParam("image") MultipartFile image) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            UserDTO userDTO = authService.updateProfileImage(email, image);
            return new ResponseEntity<>(ApiResponse.success(userDTO), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            ErrorDetail error = new ErrorDetail("AUTH-005", e.getMessage(), null);
            ApiResponse<UserDTO> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping({"/profile-image", "/me/profile-image"})
    public ResponseEntity<ApiResponse<UserDTO>> removeProfileImage() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            UserDTO userDTO = authService.removeProfileImage(email);
            return new ResponseEntity<>(ApiResponse.success(userDTO), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            ErrorDetail error = new ErrorDetail("AUTH-006", e.getMessage(), null);
            ApiResponse<UserDTO> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    // TODO: remove test endpoint after QA
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOs = users.stream()
                .map(this::toUserDTO)
                .toList();

        ApiResponse<List<UserDTO>> response = ApiResponse.success(userDTOs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private UserDTO toUserDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getFirstname(),
                user.getLastname(),
                user.getRole(),
                user.getPhoneNumber(),
                user.getProfileImageUrl());
    }
}
