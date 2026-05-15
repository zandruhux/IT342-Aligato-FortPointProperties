package edu.cit.aligato.fortpointproperties.shared.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://localhost:3000",
                "http://localhost:5174",
                "http://127.0.0.1:5173",
                "http://10.0.2.2:8080"));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return authentication -> {
            throw new BadCredentialsException("Authentication not supported");
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                // --- PUBLIC ENDPOINTS ---
                .requestMatchers("/api/v1/auth/register", "/api/v1/auth/login").permitAll()
                .requestMatchers("/ws/**").permitAll()
                .requestMatchers("/properties").permitAll()
                .requestMatchers("/properties/{id}").permitAll()
                .requestMatchers("/properties/search/location").permitAll()

                // --- AUTHENTICATED USER ENDPOINTS ---
                .requestMatchers("/api/messaging/**").hasAnyRole("REGISTERED_USER", "AGENT")
                .requestMatchers("/api/v1/auth/profile").authenticated()
                .requestMatchers("/user/properties").authenticated()
                .requestMatchers("/user/properties/{id}/advanced").authenticated()
                .requestMatchers("/user/properties/search/name").authenticated()
                .requestMatchers("/user/properties/search/location").authenticated()
                .requestMatchers("/user/favorites").authenticated()
                .requestMatchers("/user/favorites/{propertyId}").authenticated()
                .requestMatchers("/user/favorites/{propertyId}/check").authenticated()
                .requestMatchers("/user/favorites/count").authenticated()

                // --- AGENT ENDPOINTS ---
                .requestMatchers("/agent/properties").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers("/agent/properties/{id}/advanced").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers("/agent/properties/search/name").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers("/agent/properties/search/location").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers("/agent/properties/search/developer").hasAnyRole("AGENT", "ADMIN")

                // --- ADMIN ENDPOINTS ---
                .requestMatchers("/admin/properties").hasRole("ADMIN")
                .requestMatchers("/admin/properties/{id}").hasRole("ADMIN")
                .requestMatchers("/admin/properties/{propertyId}/units").hasRole("ADMIN")
                .requestMatchers("/admin/properties/{propertyId}/units/{unitId}").hasRole("ADMIN")
                .requestMatchers("/admin/properties/search/name").hasRole("ADMIN")
                .requestMatchers("/admin/properties/search/location").hasRole("ADMIN")
                .requestMatchers("/admin/properties/search/developer").hasRole("ADMIN")
                .requestMatchers("/api/v1/auth/users").hasRole("ADMIN")

                // fallback
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
