package gr.mycitygov.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    @Value("${app.jwt.secret:change_me_to_a_long_secret_key_32_chars_min}")
    private String secret;

    @Value("${app.jwt.expiration-minutes:60}")
    private long expirationMinutes;

    private Key getSigningKey() {
        // Θέλει >= 32 chars για HS256
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /** Δημιουργία token: subject = userId */
    public String generateToken(String username, Long userId, String role) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expirationMinutes * 60);

        return Jwts.builder()
                .setSubject(username) // ✅ username, π.χ. "c1"
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .addClaims(Map.of(
                        "role", role,
                        "userId", userId
                ))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /** Παίρνει το subject από το token (εδώ = userId σε String) */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /** Generic helper για claims */
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /** Έλεγχος εγκυρότητας: signature + expiration */
    public boolean isValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Date exp = claims.getExpiration();
            return exp != null && exp.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /** Έλεγχος εγκυρότητας + ότι το subject ταιριάζει με τον authenticated χρήστη */
    public boolean isValid(String token, org.springframework.security.core.userdetails.UserDetails userDetails) {
        String subject = extractUsername(token);
        return isValid(token) && subject.equals(userDetails.getUsername());
    }
}
