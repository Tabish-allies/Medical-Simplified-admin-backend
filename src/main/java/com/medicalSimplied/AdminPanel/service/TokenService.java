// com.medicalSimplied.AdminPanel.service.TokenService.java
package com.medicalSimplied.AdminPanel.service;

import com.medicalSimplied.AdminPanel.model.AdminAuthInfo;
import com.medicalSimplied.AdminPanel.model.AdminToken;
import com.medicalSimplied.AdminPanel.repository.AdminTokenRepo;
import com.medicalSimplied.AdminPanel.security.JwtUtils;
import com.medicalSimplied.AdminPanel.security.TokenHash;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtUtils jwt;
    private final AdminTokenRepo tokenRepo;

    public Map<String, Object> issueTokenPair(AdminAuthInfo admin) {
        // Claims for access token: minimal example, add roles if you have them
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", admin.getAdminId());
        claims.put("role", "ADMIN");

        String access = jwt.generateAccessToken(admin.getUsername(), claims);
        String refresh = jwt.generateRefreshToken(admin.getUsername());

        // Persist (hashed) refresh token
        String hash = TokenHash.sha256Hex(refresh);
        AdminToken at = AdminToken.builder()
                .adminId(admin.getAdminId())
                .tokenHash(hash)
                .expiresAt(jwt.getExpiry(refresh).toInstant())
                .revoked(false)
                .createdAt(Instant.now())
                .build();
        tokenRepo.save(at);

        Map<String, Object> body = new HashMap<>();
        body.put("accessToken", access);
        body.put("accessTokenExpiresAt", jwt.getExpiry(access).toInstant());
        body.put("refreshToken", refresh);
        body.put("refreshTokenExpiresAt", at.getExpiresAt());
        body.put("tokenType", "Bearer");
        return body;
    }

    public Map<String, Object> rotateRefresh(String oldRefreshToken) {
        // Validate JWT and ensure it's a refresh token
        if (!jwt.isRefreshToken(oldRefreshToken)) {
            throw new RuntimeException("Invalid token type");
        }
        String username = jwt.getSubject(oldRefreshToken);
        Instant expires = jwt.getExpiry(oldRefreshToken).toInstant();
        if (expires.isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        // Ensure token exists & is not revoked
        String oldHash = TokenHash.sha256Hex(oldRefreshToken);
        AdminToken stored = tokenRepo.findByTokenHash(oldHash)
                .orElseThrow(() -> new RuntimeException("Unknown refresh token"));
        if (stored.isRevoked()) throw new RuntimeException("Refresh token revoked");

        // Create new tokens (rotation)
        AdminAuthInfo admin = new AdminAuthInfo();
        admin.setAdminId(stored.getAdminId());
        admin.setUsername(username);

        Map<String, Object> pair = issueTokenPair(admin);

        // revoke old
        stored.setRevoked(true);
        stored.setRevokedAt(Instant.now());
        stored.setReplacedByTokenHash(TokenHash.sha256Hex((String) pair.get("refreshToken")));
        tokenRepo.save(stored);

        return pair;
    }

    public void revokeRefreshToken(String refreshToken) {
        String hash = TokenHash.sha256Hex(refreshToken);
        tokenRepo.findByTokenHash(hash).ifPresent(t -> {
            t.setRevoked(true);
            t.setRevokedAt(Instant.now());
            tokenRepo.save(t);
        });
    }

    public void revokeAllForAdmin(Long adminId) {
        tokenRepo.findAll().stream()
                .filter(t -> t.getAdminId().equals(adminId) && !t.isRevoked())
                .forEach(t -> { t.setRevoked(true); t.setRevokedAt(Instant.now()); tokenRepo.save(t); });
    }
}
