package com.chingubackend.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.AbstractPersistable_;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key;
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 24시간

    public JwtUtil(@Value("${JWT_SECRET_KEY}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long id, String username, String nickname) {
        return Jwts.builder()
                .setSubject(username)
                .claim("id", id)
                .claim("nickname", nickname)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Long extractUserId(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Object idObj = claims.get("id");
            if (idObj == null) {
                System.out.println("Token does not contain an 'id' claim.");
                return null;
            }

            return Long.parseLong(idObj.toString());
        } catch (ExpiredJwtException e) {
            System.out.println("Token expired: " + e.getMessage());
            return null;
        } catch (JwtException e) {
            System.out.println("Invalid token: " + e.getMessage());
            return null;
        }
    }

    public String extractUsername(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            System.out.println("Token expired: " + e.getMessage());
            return null;
        } catch (JwtException e) {
            System.out.println("Invalid token: " + e.getMessage());
            return null;
        }
    }

    public String extractNickname(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Object nicknameObj = claims.get("nickname");
            if (nicknameObj == null) {
                System.out.println("Token does not contain a 'nickname' claim.");
                return null;
            }

            return nicknameObj.toString();
        } catch (ExpiredJwtException e) {
            System.out.println("Token expired: " + e.getMessage());
            return null;
        } catch (JwtException e) {
            System.out.println("Invalid token: " + e.getMessage());
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            System.out.println("JWT validation failed: " + e.getMessage());
            return false;
        }
    }
}