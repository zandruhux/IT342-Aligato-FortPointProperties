package edu.cit.aligato.fortpointproperties.auth.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import edu.cit.aligato.fortpointproperties.auth.dto.LoginRequest;
import edu.cit.aligato.fortpointproperties.auth.dto.RegisterRequest;
import edu.cit.aligato.fortpointproperties.auth.dto.UserDTO;
import edu.cit.aligato.fortpointproperties.auth.entity.User;
import edu.cit.aligato.fortpointproperties.auth.repository.UserRepository;
import edu.cit.aligato.fortpointproperties.auth.service.ProfileImageStorageService.UploadedProfileImage;
import edu.cit.aligato.fortpointproperties.shared.utils.PasswordValidator;

@Service
public class AuthService {

    private static final long MAX_PROFILE_IMAGE_SIZE_BYTES = 2 * 1024 * 1024;

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ProfileImageStorageService profileImageStorageService;

    /**
     * Constructor with BCryptPasswordEncoder injected from SecurityConfig
     * Password encoder is configured with SDD specified salt rounds = 12
     */
    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
            ProfileImageStorageService profileImageStorageService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.profileImageStorageService = profileImageStorageService;
    }

    public User registerUser(RegisterRequest request) {
        // Prevent duplicate email registration
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        // Validate password strength
        PasswordValidator.ValidationResult validationResult = PasswordValidator.validate(request.getPassword());
        if (!validationResult.isValid()) {
            throw new IllegalArgumentException(validationResult.getErrorMessage());
        }

        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setFirstname(request.getFirstname());
        newUser.setLastname(request.getLastname());

        // Securely hash the password before saving
        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        // Default role for a newly registered public user per the SDD
        newUser.setRole("registered_user");

        return userRepository.save(newUser);
    }

    public User authenticateUser(LoginRequest request) {
        // 1. Find the user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        // 2. Verify the password matches the hashed password in the database
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        // 3. If everything is good, return the user
        return user;
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
                user.getProfileImageUrl());
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
