package com.boticlic.boticlic.controller;

import com.boticlic.boticlic.repository.UsuarioRepository;
import com.boticlic.boticlic.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email    = body.get("email");
        String password = body.get("password");

        return usuarioRepository.findByEmail(email)
                // ✅ Verificar con BCrypt (contraseñas encriptadas)
                .filter(u -> passwordEncoder.matches(password, u.getPassword()))
                .map(u -> ResponseEntity.ok(Map.of(
                        "token",  jwtUtil.generarToken(u.getEmail(), u.getRol()),
                        "rol",    u.getRol(),
                        "nombre", u.getNombre(),
                        "id",     u.getId()
                )))
                .orElse(ResponseEntity.status(401).build());
    }
}