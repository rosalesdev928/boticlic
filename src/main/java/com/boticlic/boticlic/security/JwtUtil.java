package com.boticlic.boticlic.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // ✅ CORREGIDO: clave leída desde application.properties
    // ya no está hardcodeada en el código fuente
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiracion}")
    private long expiracion;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generarToken(String email, String rol) {
        return Jwts.builder()
                .subject(email)
                .claim("rol", rol)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiracion))
                .signWith(getKey())
                .compact();
    }

    public String extraerEmail(String token) {
        return parsear(token).getSubject();
    }

    public String extraerRol(String token) {
        return (String) parsear(token).get("rol");
    }

    public boolean esValido(String token) {
        try {
            parsear(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parsear(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}