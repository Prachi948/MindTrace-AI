package com.mindtrace.mindtrace_backend.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@Service
public class JwtService {
    private static final String HEADER = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
    private final ObjectMapper objectMapper;
    private final byte[] secret;
    private final long expirationSeconds;

    public JwtService(ObjectMapper objectMapper,
                      @Value("${app.jwt.secret}") String secretValue,
                      @Value("${app.jwt.expiration-seconds:86400}") long expirationSeconds) {
        this.objectMapper = objectMapper;
        if (secretValue == null || secretValue.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException("app.jwt.secret must be at least 32 UTF-8 bytes");
        }
        this.secret = secretValue.getBytes(StandardCharsets.UTF_8);
        this.expirationSeconds = expirationSeconds;
    }

    public String generateToken(Long userId, String role) {
        try {
            long expiresAt = Instant.now().getEpochSecond() + expirationSeconds;
            String header = encode(HEADER.getBytes(StandardCharsets.UTF_8));
            String payloadJson = "{\"sub\":" + userId + ",\"role\":\"" + safeRole(role)
                    + "\",\"iat\":" + Instant.now().getEpochSecond()
                    + ",\"exp\":" + expiresAt + "}";
            String payload = encode(payloadJson.getBytes(StandardCharsets.UTF_8));
            String content = header + "." + payload;
            return content + "." + encode(sign(content));
        } catch (Exception ex) {
            throw new IllegalStateException("Could not generate access token", ex);
        }
    }

    public TokenDetails validateAndRead(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) throw new IllegalArgumentException("Invalid token format");
            String content = parts[0] + "." + parts[1];
            byte[] expected = sign(content);
            byte[] actual = Base64.getUrlDecoder().decode(parts[2]);
            if (!java.security.MessageDigest.isEqual(expected, actual)) {
                throw new IllegalArgumentException("Invalid token signature");
            }
            JsonNode payload = objectMapper.readTree(Base64.getUrlDecoder().decode(parts[1]));
            long userId = payload.path("sub").asLong(0);
            long expiresAt = payload.path("exp").asLong(0);
            String role = payload.path("role").asText("");
            if (userId <= 0 || role.isBlank() || Instant.now().getEpochSecond() >= expiresAt) {
                throw new IllegalArgumentException("Token is expired or invalid");
            }
            return new TokenDetails(userId, role);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid or expired access token", ex);
        }
    }

    private byte[] sign(String content) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret, "HmacSHA256"));
        return mac.doFinal(content.getBytes(StandardCharsets.UTF_8));
    }

    private String encode(byte[] value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value);
    }

    private String safeRole(String role) {
        if (role == null || !role.matches("[A-Za-z0-9_-]{1,30}")) return "STUDENT";
        return role;
    }

    public record TokenDetails(Long userId, String role) {}
}
