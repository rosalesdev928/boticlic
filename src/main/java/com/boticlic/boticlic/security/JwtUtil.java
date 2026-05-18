package com.boticlic.boticlic.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // Clave secreta para firmar el token (mínimo 32 caracteres)
    private final SecretKey key = Keys.hmacShaKeyFor(
            "boticlic-clave-super-secreta-2026-ok".getBytes()
    );

    // El token dura 8 horas
    private final long EXPIRACION = 1000 * 60 * 60 * 8;

    // Genera el token con el email y el rol del usuario
    public String generarToken(String email, String rol) {
        return Jwts.builder()
                .subject(email)
                .claim("rol", rol)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRACION))
                .signWith(key)
                .compact();
    }

    // Extrae el email del token
    public String extraerEmail(String token) {
        return parsear(token).getSubject();
    }

    // Extrae el rol del token
    public String extraerRol(String token) {
        return (String) parsear(token).get("rol");
    }

    // Verifica si el token es válido
    public boolean esValido(String token) {
        try {
            parsear(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Método interno para parsear el token
    private Claims parsear(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}