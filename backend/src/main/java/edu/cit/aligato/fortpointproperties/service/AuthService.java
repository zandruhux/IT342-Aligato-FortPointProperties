package edu.cit.aligato.fortpointproperties.service;

import edu.cit.aligato.fortpointproperties.dto.RegisterRequest;
import edu.cit.aligato.fortpointproperties.entity.User;
import edu.cit.aligato.fortpointproperties.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        // SDD specifies bcrypt with salt rounds = 12
        this.passwordEncoder = new BCryptPasswordEncoder(12);
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
}