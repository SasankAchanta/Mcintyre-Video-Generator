package org.mcintyrelab.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Configuration
@Slf4j
public class JwtUtil {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Integer jwtExpiration;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        // converts your plain string into a proper cryptographic key object
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)      // payload - who the token is for
                .setIssuedAt(new Date())   // payload - when it was created
                .setExpiration(new Date(new Date().getTime() + jwtExpiration))  // payload - when it expires
                .signWith(secretKey)       // signature - proves it wasn't tampered with
                .compact();                 // combines it all into the final token string
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)  // tell the parser what key was used to sign the token
                .build()                   // create the parser
                .parseClaimsJws(token)     // parse the token and verify the signature
                .getBody()                 // get the payload (contains userId, expiration, etc.)
                .getSubject();             // get the subject (the UUID we stored)
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)  // set the key to verify against
                    .build()
                    .parseClaimsJws(token);    // if this doesn't throw an exception, token is valid
            return true;
        } catch (JwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
}
