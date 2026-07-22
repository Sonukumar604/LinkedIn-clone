package com.LinkedInProject.userService.service;

import com.LinkedInProject.userService.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    // 1 Hour
    private static final long ACCESS_TOKEN_EXPIRATION = 60 * 60 * 1000;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generate Access Token
     */
    public String generateAccessToken(User user) {

        log.info("Generating access token for user: {}", user.getEmail());

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * Extract User Id from JWT
     */
    public Long getUserIdFromToken(String token) {
        return Long.valueOf(extractAllClaims(token).getSubject());
    }

    /**
     * Extract Email from JWT
     */
    public String getEmailFromToken(String token) {
        return extractAllClaims(token).get("email", String.class);
    }

    /**
     * Validate Token
     */
    public boolean isTokenValid(String token) {
        return !isTokenExpired(token);
    }

    /**
     * Check Token Expiry
     */
    public boolean isTokenExpired(String token) {
        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }

    /**
     * Extract All Claims
     */
    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}