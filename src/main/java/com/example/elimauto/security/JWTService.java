package com.example.elimauto.security;

import com.example.elimauto.models.User;
import com.example.elimauto.models.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;


@Service
public class JWTService {
    private final Key secretKey;

    public JWTService(@Value("${jwt.secret}") String secretKey) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public String generateToken(User user) {
        String roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.joining(","));
        String name = user.getName();
        Long userId = user.getId();
        return Jwts.builder()
                .setSubject(user.getPhoneNumber())
                .claim("role", roles)
                .claim("name", name)
                .claim("id", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }


    public boolean isTokenValid(String token, String phoneNumber) {
        String extractedPhoneNumber = extractClaims(token).getSubject();
        return (extractedPhoneNumber.equals(phoneNumber) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
