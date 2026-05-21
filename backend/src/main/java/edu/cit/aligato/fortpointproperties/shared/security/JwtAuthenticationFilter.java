package edu.cit.aligato.fortpointproperties.shared.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import edu.cit.aligato.fortpointproperties.auth.entity.User;
import edu.cit.aligato.fortpointproperties.auth.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String authorizationHeader = request.getHeader("Authorization");

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);

                if (jwtUtil.isTokenValid(token)) {
                    String email = jwtUtil.extractEmail(token);
                    User user = userRepository.findByEmail(email).orElse(null);
                    if (user == null) {
                        logger.warn("JWT Token references a user that no longer exists: " + email);
                        setAnonymousAuthentication();
                        filterChain.doFilter(request, response);
                        return;
                    }

                    String role = normalizeRole(user.getRole());

                    List<GrantedAuthority> authorities = Arrays.asList(
                            new SimpleGrantedAuthority("ROLE_" + role));
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            email, null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.info("JWT Token validated for user: " + email);
                } else {
                    logger.warn("JWT Token is invalid");
                    setAnonymousAuthentication();
                }
            } else {
                setAnonymousAuthentication();
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: " + e.getMessage(), e);
            setAnonymousAuthentication();
        }

        filterChain.doFilter(request, response);
    }

    private void setAnonymousAuthentication() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            AnonymousAuthenticationToken anonymousToken = new AnonymousAuthenticationToken(
                    "anonymousUser", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
            SecurityContextHolder.getContext().setAuthentication(anonymousToken);
        }
    }

    private String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            return "ANONYMOUS";
        }

        String normalized = role.trim().toUpperCase().replace("-", "_").replace(" ", "_");
        if (normalized.startsWith("ROLE_")) {
            normalized = normalized.substring("ROLE_".length());
        }
        if ("USER".equals(normalized) || "REGISTERED_USER".equals(normalized) || "REGISTERED_USEER".equals(normalized)) {
            return "REGISTERED_USER";
        }
        return normalized;
    }
}
