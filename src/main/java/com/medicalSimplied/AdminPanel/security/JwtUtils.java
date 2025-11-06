package com.medicalSimplied.AdminPanel.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtils {

    private final SecretKey key;
    private final String issuer;
    private final long accessTtlMs;
    private final long refreshTtlMs;

    public JwtUtils(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.issuer}") String issuer,
            @Value("${jwt.access.expiration-ms}") long accessTtlMs,
            @Value("${jwt.refresh.expiration-ms}") long refreshTtlMs
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer;
        this.accessTtlMs = accessTtlMs;
        this.refreshTtlMs = refreshTtlMs;
    }

    public String generateAccessToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(subject)
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(accessTtlMs)))
                .claims(claims)
                .signWith(key) // ✅ 0.12.6 syntax
                .compact();
    }

    public String generateRefreshToken(String subject) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(subject)
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(refreshTtlMs)))
                .claim("typ", "refresh")
                .signWith(key) // ✅ 0.12.6 syntax
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parser()
                .verifyWith(key) // ✅ replaces setSigningKey
                .build()
                .parseSignedClaims(token);
    }

    public String getSubject(String token) {
        return parse(token).getPayload().getSubject();
    }

    public Date getExpiry(String token) {
        return parse(token).getPayload().getExpiration();
    }

    public boolean isRefreshToken(String token) {
        Object t = parse(token).getPayload().get("typ");
        return "refresh".equals(t);
    }
}
