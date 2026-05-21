package edu.cit.aligato.fortpointproperties.auth.service;

import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import edu.cit.aligato.fortpointproperties.auth.dto.LoginRequest;
import edu.cit.aligato.fortpointproperties.auth.dto.RegisterRequest;
import edu.cit.aligato.fortpointproperties.auth.dto.UpdateProfileRequest;
import edu.cit.aligato.fortpointproperties.auth.dto.UserDTO;
import edu.cit.aligato.fortpointproperties.auth.entity.User;
import edu.cit.aligato.fortpointproperties.auth.repository.UserRepository;
import edu.cit.aligato.fortpointproperties.auth.service.ProfileImageStorageService.UploadedProfileImage;
import edu.cit.aligato.fortpointproperties.shared.utils.PasswordValidator;

@Service
public class AuthService {

    private static final long MAX_PROFILE_IMAGE_SIZE_BYTES = 2 * 1024 * 1024;
    private static final String GOOGLE_PROVIDER = "GOOGLE";
    private static final String DEFAULT_GOOGLE_ROLE = "REGISTERED_USER";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ProfileImageStorageService profileImageStorageService;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
            ProfileImageStorageService profileImageStorageService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.profileImageStorageService = profileImageStorageService;
    }

    public User registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        PasswordValidator.ValidationResult validationResult = PasswordValidator.validate(request.getPassword());
        if (!validationResult.isValid()) {
            throw new IllegalArgumentException(validationResult.getErrorMessage());
        }

        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setFirstname(request.getFirstname());
        newUser.setLastname(request.getLastname());

        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        newUser.setRole("registered_user");

        return userRepository.save(newUser);
    }

    public User authenticateUser(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return user;
    }

    public User authenticateGoogleUser(String email, String firstname, String lastname, String profileImageUrl,
            String providerId) {
        String normalizedEmail = normalizeRequired(email, "Google account email is required").toLowerCase();
        String normalizedProviderId = normalizeRequired(providerId, "Google account provider ID is required");

        return userRepository.findByEmailIgnoreCase(normalizedEmail)
                .map(existingUser -> loginExistingGoogleUser(existingUser, normalizedProviderId, profileImageUrl))
                .orElseGet(() -> createGoogleUser(normalizedEmail, firstname, lastname, profileImageUrl,
                        normalizedProviderId));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public UserDTO getCurrentUserProfile(String email) {
        return toUserDTO(getUserByEmail(email));
    }

    public UserDTO updateProfileImage(String email, MultipartFile file) {
        validateProfileImage(file);

        User user = getUserByEmail(email);
        String oldProfileImagePath = user.getProfileImagePath();
        UploadedProfileImage uploadedImage = profileImageStorageService.uploadProfileImage(user.getId(), file);

        user.setProfileImagePath(uploadedImage.getPath());
        user.setProfileImageUrl(uploadedImage.getUrl());
        User savedUser = userRepository.save(user);

        if (oldProfileImagePath != null && !oldProfileImagePath.isBlank()) {
            profileImageStorageService.deleteProfileImage(oldProfileImagePath);
        }

        return toUserDTO(savedUser);
    }

    public UserDTO updateProfile(String currentEmail, UpdateProfileRequest request) {
        User user = getUserByEmail(currentEmail);
        String nextEmail = request.getEmail().trim();

        if (!user.getEmail().equalsIgnoreCase(nextEmail)) {
            throw new IllegalArgumentException("Email cannot be changed from this profile page");
        }

        user.setFirstname(request.getFirstname().trim());
        user.setLastname(request.getLastname().trim());
        user.setPhoneNumber(request.getPhoneNumber() == null ? null : request.getPhoneNumber().trim());

        return toUserDTO(userRepository.save(user));
    }

    public UserDTO removeProfileImage(String email) {
        User user = getUserByEmail(email);
        String oldProfileImagePath = user.getProfileImagePath();

        if (oldProfileImagePath != null && !oldProfileImagePath.isBlank()) {
            profileImageStorageService.deleteProfileImage(oldProfileImagePath);
        }

        user.setProfileImagePath(null);
        user.setProfileImageUrl(null);
        return toUserDTO(userRepository.save(user));
    }

    public UserDTO toUserDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getFirstname(),
                user.getLastname(),
                user.getRole(),
                user.getPhoneNumber(),
                user.getProfileImageUrl());
    }

    private User loginExistingGoogleUser(User user, String providerId, String profileImageUrl) {
        if (!GOOGLE_PROVIDER.equalsIgnoreCase(user.getProvider())) {
            throw new IllegalArgumentException(
                    "An account with this email already exists. Please log in using email and password.");
        }

        if (user.getProviderId() != null && !user.getProviderId().isBlank()
                && !user.getProviderId().equals(providerId)) {
            throw new IllegalArgumentException("This Google account cannot be used for this email address.");
        }

        boolean changed = false;
        if (user.getProviderId() == null || user.getProviderId().isBlank()) {
            user.setProviderId(providerId);
            changed = true;
        }
        if ((user.getProfileImageUrl() == null || user.getProfileImageUrl().isBlank())
                && profileImageUrl != null && !profileImageUrl.isBlank()) {
            user.setProfileImageUrl(profileImageUrl.trim());
            changed = true;
        }

        return changed ? userRepository.save(user) : user;
    }

    private User createGoogleUser(String email, String firstname, String lastname, String profileImageUrl,
            String providerId) {
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setFirstname(defaultIfBlank(firstname, "Google"));
        newUser.setLastname(defaultIfBlank(lastname, "User"));
        newUser.setPasswordHash(passwordEncoder.encode(generatePlaceholderPassword()));
        newUser.setRole(DEFAULT_GOOGLE_ROLE);
        newUser.setProvider(GOOGLE_PROVIDER);
        newUser.setProviderId(providerId);
        if (profileImageUrl != null && !profileImageUrl.isBlank()) {
            newUser.setProfileImageUrl(profileImageUrl.trim());
        }

        return userRepository.save(newUser);
    }

    private String normalizeRequired(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    private String defaultIfBlank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private String generatePlaceholderPassword() {
        byte[] randomBytes = new byte[32];
        SECURE_RANDOM.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private void validateProfileImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Profile image is required");
        }

        if (file.getSize() > MAX_PROFILE_IMAGE_SIZE_BYTES) {
            throw new IllegalArgumentException("Profile image must be 2MB or smaller");
        }

        String contentType = file.getContentType();
        if (!"image/jpeg".equals(contentType)
                && !"image/png".equals(contentType)
                && !"image/webp".equals(contentType)) {
            throw new IllegalArgumentException("Only JPG, JPEG, PNG, and WEBP images are allowed");
        }

        String filename = file.getOriginalFilename();
        String extension = "";
        if (filename != null && filename.lastIndexOf('.') >= 0) {
            extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        }

        if (!"jpg".equals(extension)
                && !"jpeg".equals(extension)
                && !"png".equals(extension)
                && !"webp".equals(extension)) {
            throw new IllegalArgumentException("Only JPG, JPEG, PNG, and WEBP images are allowed");
        }
    }
}
