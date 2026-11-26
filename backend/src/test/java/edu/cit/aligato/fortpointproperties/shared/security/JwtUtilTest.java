package edu.cit.aligato.fortpointproperties.shared.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

class JwtUtilTest {

    // Non-ASCII characters on purpose: the signing key must be derived from the
    // UTF-8 bytes of the secret on every platform, not the JVM default charset.
    private static final String SECRET =
            "ñandú-ключ-秘密-test-secret-test-secret-test-secret-test-secret-0123456789";

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secretKey", SECRET);
        ReflectionTestUtils.setField(jwtUtil, "accessTokenExpiration", 60_000L);
        ReflectionTestUtils.setField(jwtUtil, "refreshTokenExpiration", 120_000L);
    }

    @Test
    void generateAccessToken_isSignedWithUtf8KeyBytes() {
        String token = jwtUtil.generateAccessToken("user@example.com", "ADMIN");

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals("user@example.com", claims.getSubject());
        assertEquals("ADMIN", claims.get("role", String.class));
    }

    @Test
    void accessToken_roundTripsEmailAndRole() {
        String token = jwtUtil.generateAccessToken("user@example.com", "AGENT");

        assertTrue(jwtUtil.isTokenValid(token));
        assertEquals("user@example.com", jwtUtil.extractEmail(token));
        assertEquals("AGENT", jwtUtil.extractRole(token));
    }

    @Test
    void isTokenValid_tamperedToken_returnsFalse() {
        String token = jwtUtil.generateAccessToken("user@example.com", "ADMIN");
        String tampered = token.substring(0, token.length() - 2) + (token.endsWith("A") ? "BB" : "AA");

        assertFalse(jwtUtil.isTokenValid(tampered));
    }
}
