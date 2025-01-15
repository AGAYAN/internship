package com.example.Internship_project.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtCore {

    @Value("${testing.app.secret}")
    private String secret;
    @Value("${testing.app.lifetime}")
    private int lifetime;

    private Key key;

    public JwtCore() {
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    public String generateToken(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl)authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + lifetime))
                .signWith(key)  // Используем безопасный ключ
                .compact();
    }

    public String getNameFromJwt(String token) {
        return Jwts.parser()
                .setSigningKey(key)  // Используем безопасный ключ
                .parseClaimsJwt(token)
                .getBody()
                .getSubject();
    }
}
