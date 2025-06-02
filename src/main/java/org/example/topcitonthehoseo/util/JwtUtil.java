package org.example.topcitonthehoseo.util;
import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private final Key key;
    private final long accessTokenValidity = 1000L * 60 * 30; // 30분
    private final long refreshTokenValidity = 1000L * 60 * 60 * 24 * 7; // 7일

    public JwtUtil() {
        String secret = "ThisIsASecretKeyForJwtEncodingMustBeAtLeast256BitsLong!";
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String createAccessToken(String studentId, String role) {
        return createToken(studentId, role, accessTokenValidity);
    }

    public String createRefreshToken(String studentId) {
        return createToken(studentId, null, refreshTokenValidity);
    }

    private String createToken(String studentId, String role, long validity) {
        JwtBuilder builder = Jwts.builder()
                .setSubject(studentId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + validity))
                .signWith(key, SignatureAlgorithm.HS256);

        if (role != null) {
            builder.claim("role", role);
        }

        return builder.compact();
    }

    public Claims parseToken(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getStudentIdFromToken(String token) {
        return parseToken(token).getSubject(); // subject = studentId
    }

    public String getRoleFromToken(String token) {
        return parseToken(token).get("role", String.class);
    }
}
