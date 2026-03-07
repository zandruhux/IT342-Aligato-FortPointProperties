package edu.cit.aligato.fortpointproperties.service;

import edu.cit.aligato.fortpointproperties.dto.LoginRequest;
import edu.cit.aligato.fortpointproperties.dto.RegisterRequest;
import edu.cit.aligato.fortpointproperties.entity.User;
import edu.cit.aligato.fortpointproperties.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Constructor with BCryptPasswordEncoder injected from SecurityConfig
     * Password encoder is configured with SDD specified salt rounds = 12
     */
    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(RegisterRequest request) {
        // Prevent duplicate email registration
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
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
}