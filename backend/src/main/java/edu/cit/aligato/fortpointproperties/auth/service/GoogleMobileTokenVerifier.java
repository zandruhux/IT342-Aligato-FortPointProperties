package edu.cit.aligato.fortpointproperties.auth.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;

@Service
public class GoogleMobileTokenVerifier {

    private static final Set<String> TRUSTED_GOOGLE_ISSUERS = Set.of(
            "https://accounts.google.com",
            "accounts.google.com");

    private final JwtDecoder jwtDecoder;
    private final String googleClientId;

    public GoogleMobileTokenVerifier(
            @Value("${spring.security.oauth2.client.registration.google.client-id}") String googleClientId) {
        this.googleClientId = googleClientId;
        this.jwtDecoder = NimbusJwtDecoder
                .withJwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
                .build();
    }

    public GoogleMobileUser verify(String idToken) {
        if (idToken == null || idToken.isBlank()) {
            throw new IllegalArgumentException("Google ID token is required.");
        }
        if (googleClientId == null || googleClientId.isBlank()) {
            throw new IllegalArgumentException("Google mobile sign-in is not configured.");
        }

        Jwt jwt;
        try {
            jwt = jwtDecoder.decode(idToken.trim());
        } catch (JwtException e) {
            throw new IllegalArgumentException("Google sign-in token could not be verified.");
        }

        if (!jwt.getAudience().contains(googleClientId)) {
            throw new IllegalArgumentException("Google sign-in token was issued for a different client.");
        }

        String issuer = jwt.getIssuer() == null ? "" : jwt.getIssuer().toString();
        if (!TRUSTED_GOOGLE_ISSUERS.contains(issuer)) {
            throw new IllegalArgumentException("Google sign-in token issuer is invalid.");
        }

        if (!isEmailVerified(jwt.getClaim("email_verified"))) {
            throw new IllegalArgumentException("Google email must be verified before signing in.");
        }

        return new GoogleMobileUser(
                jwt.getClaimAsString("email"),
                jwt.getClaimAsString("given_name"),
                jwt.getClaimAsString("family_name"),
                jwt.getClaimAsString("picture"),
                jwt.getSubject());
    }

    private boolean isEmailVerified(Object value) {
        if (value instanceof Boolean verified) {
            return verified;
        }
        return value != null && Boolean.parseBoolean(String.valueOf(value));
    }

    public record GoogleMobileUser(
            String email,
            String firstname,
            String lastname,
            String profileImageUrl,
            String providerId) {
    }
}
