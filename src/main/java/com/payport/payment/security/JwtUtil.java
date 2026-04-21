package com.payport.payment.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    // Pulled from application.properties — never hardcode secrets in code
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    // Build a cryptographic key from your plain-text secret
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Called after successful login — creates and signs a JWT
    public String generateToken(String mobileNumber) {
        return Jwts.builder()
                .subject(mobileNumber)           // who this token is for
                .issuedAt(new Date())             // when it was created
                .expiration(new Date(System.currentTimeMillis() + expirationMs)) // when it dies
                .signWith(getSigningKey())        // sign it — makes it tamper-proof
                .compact();                       // serialize to the final token string
    }

    // Pull the mobile number out of a token (used in the filter)
    public String extractMobileNumber(String token) {
        return parseClaims(token).getSubject();
    }

    // Check token is valid and not expired
    public boolean isTokenValid(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.getExpiration().after(new Date()); // not expired?
        } catch (Exception e) {
            return false; // any parse/signature error = invalid
        }
    }

    // Parse and verify signature — throws if tampered or expired
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}