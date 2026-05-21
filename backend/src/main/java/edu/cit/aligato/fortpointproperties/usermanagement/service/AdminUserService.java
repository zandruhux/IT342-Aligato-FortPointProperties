package edu.cit.aligato.fortpointproperties.usermanagement.service;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import edu.cit.aligato.fortpointproperties.auth.entity.User;
import edu.cit.aligato.fortpointproperties.auth.repository.UserRepository;
import edu.cit.aligato.fortpointproperties.shared.utils.PasswordValidator;
import edu.cit.aligato.fortpointproperties.usermanagement.dto.AdminCreateUserRequestDTO;
import edu.cit.aligato.fortpointproperties.usermanagement.dto.AdminUserResponseDTO;

@Service
public class AdminUserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AdminUserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<AdminUserResponseDTO> getUsers(String role) {
        if (role == null || role.isBlank()) {
            return userRepository.findAll().stream()
                    .map(this::toResponseDTO)
                    .toList();
        }

        String storageRole = toStorageRole(role);
        return userRepository.findByRole(storageRole).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public AdminUserResponseDTO createUser(AdminCreateUserRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        PasswordValidator.ValidationResult validationResult = PasswordValidator.validate(request.getPassword());
        if (!validationResult.isValid()) {
            throw new IllegalArgumentException(validationResult.getErrorMessage());
        }

        User user = new User();
        user.setFirstname(request.getFirstname().trim());
        user.setLastname(request.getLastname().trim());
        user.setEmail(request.getEmail().trim());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(toStorageRole(request.getRole()));

        return toResponseDTO(userRepository.save(user));
    }

    public AdminUserResponseDTO updateUserRole(String id, String role) {
        User user = getUser(id);
        user.setRole(toStorageRole(role));
        return toResponseDTO(userRepository.save(user));
    }

    public void deleteUser(String id) {
        User user = getUser(id);
        userRepository.delete(user);
    }

    private User getUser(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private String toStorageRole(String role) {
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("Role is required");
        }

        String normalized = role.trim().toUpperCase();
        if (normalized.startsWith("ROLE_")) {
            normalized = normalized.substring("ROLE_".length());
        }

        if ("ADMIN".equals(normalized)) {
            return "ADMIN";
        }
        if ("AGENT".equals(normalized)) {
            return "AGENT";
        }
        if ("USER".equals(normalized) || "REGISTERED_USER".equals(normalized) || "REGISTERED_USER".equals(role)) {
            return "registered_user";
        }
        if ("REGISTERED_USER".equals(normalized) || "REGISTERED_USEER".equals(normalized)) {
            return "registered_user";
        }

        throw new IllegalArgumentException("Invalid role: " + role);
    }

    private AdminUserResponseDTO toResponseDTO(User user) {
        AdminUserResponseDTO dto = new AdminUserResponseDTO();
        dto.setId(user.getId());
        dto.setFirstname(user.getFirstname());
        dto.setLastname(user.getLastname());
        dto.setFullName(buildFullName(user));
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setStatus("Active");
        dto.setProfileImageUrl(user.getProfileImageUrl());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }

    private String buildFullName(User user) {
        String firstName = user.getFirstname() == null ? "" : user.getFirstname().trim();
        String lastName = user.getLastname() == null ? "" : user.getLastname().trim();
        return (firstName + " " + lastName).trim();
    }
}
