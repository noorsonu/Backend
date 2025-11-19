package com.main.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.main.entities.UserEntity;

import org.springframework.security.core.GrantedAuthority;

import java.util.Date;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.stream.Collectors;
import io.jsonwebtoken.Claims;


@Component
public class JwtUtils {

    private final String jwtSecret;
    private final long jwtExpirationMs;

    public JwtUtils(Environment env) {
        // Prefer environment variables, fall back to properties if present
        String secret = env.getProperty("JWT_SECRET", env.getProperty("jwt.secret"));
        String expStr = env.getProperty("JWT_EXPIRATION_MS", env.getProperty("jwt.expiration-ms"));

        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT secret is not configured. Set environment variable JWT_SECRET.");
        }
        // JJWT HS256 requires >= 256-bit (32-byte) key
        if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("JWT secret must be at least 32 bytes (256 bits). Increase JWT_SECRET length.");
        }

        if (expStr == null || expStr.isBlank()) {
            throw new IllegalStateException("JWT expiration is not configured. Set environment variable JWT_EXPIRATION_MS (milliseconds).");
        }
        long exp;
        try {
            exp = Long.parseLong(expStr);
        } catch (NumberFormatException nfe) {
            throw new IllegalStateException("JWT_EXPIRATION_MS must be a number (milliseconds).", nfe);
        }
        if (exp <= 0) {
            throw new IllegalStateException("JWT_EXPIRATION_MS must be > 0.");
        }

        this.jwtSecret = secret;
        this.jwtExpirationMs = exp;
    }

    private Key getKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + jwtExpirationMs);
        List<String> roles = userDetails.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList());
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", roles)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false; 
        }
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return extractAllClaims(token).get("roles", List.class);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
