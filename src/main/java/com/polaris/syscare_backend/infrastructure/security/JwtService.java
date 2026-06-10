package com.polaris.syscare_backend.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService
{
    private static final long EXPIRATION_MS = 1000L * 60 * 60 * 24; // 24h
    private final SecretKey secretKey;

    public JwtService(@Value("${jwt.secret}") String secret)
    {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email, String role)
    {
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(secretKey)
                .compact();
    }

    public String extractEmail(String token)
    {
        return parseClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, String email)
    {
        try
        {
            return extractEmail(token).equals(email) && !isTokenExpired(token);
        } catch (Exception e)
        {
            return false;
        }
    }

    private boolean isTokenExpired(String token)
    {
        return parseClaims(token).getExpiration().before(new Date());
    }

    private Claims parseClaims(String token)
    {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


}
