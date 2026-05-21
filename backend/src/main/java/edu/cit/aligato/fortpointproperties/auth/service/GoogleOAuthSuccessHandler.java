package edu.cit.aligato.fortpointproperties.auth.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import edu.cit.aligato.fortpointproperties.auth.entity.User;
import edu.cit.aligato.fortpointproperties.shared.security.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class GoogleOAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final String frontendCallbackUrl;
    private final String frontendLoginUrl;

    public GoogleOAuthSuccessHandler(AuthService authService, JwtUtil jwtUtil,
            @Value("${app.oauth2.frontend-callback-url}") String frontendCallbackUrl,
            @Value("${app.oauth2.frontend-login-url}") String frontendLoginUrl) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
        this.frontendCallbackUrl = frontendCallbackUrl;
        this.frontendLoginUrl = frontendLoginUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        try {
            OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

            Object emailVerified = oauthUser.getAttribute("email_verified");
            if (Boolean.FALSE.equals(emailVerified)) {
                redirectWithError(response, "Google email must be verified before signing in.");
                return;
            }

            User user = authService.authenticateGoogleUser(
                    getAttribute(oauthUser, "email"),
                    getAttribute(oauthUser, "given_name"),
                    getAttribute(oauthUser, "family_name"),
                    getAttribute(oauthUser, "picture"),
                    getAttribute(oauthUser, "sub"));

            String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole());
            String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

            String redirectUrl = UriComponentsBuilder.fromUriString(frontendCallbackUrl)
                    .queryParam("token", accessToken)
                    .queryParam("refreshToken", refreshToken)
                    .encode()
                    .build()
                    .toUriString();

            response.sendRedirect(redirectUrl);
        } catch (IllegalArgumentException e) {
            redirectWithError(response, e.getMessage());
        }
    }

    private String getAttribute(OAuth2User oauthUser, String name) {
        Object value = oauthUser.getAttribute(name);
        return value == null ? null : String.valueOf(value);
    }

    private void redirectWithError(HttpServletResponse response, String message) throws IOException {
        String redirectUrl = UriComponentsBuilder.fromUriString(frontendLoginUrl)
                .queryParam("error", message)
                .encode()
                .build()
                .toUriString();
        response.sendRedirect(redirectUrl);
    }
}
