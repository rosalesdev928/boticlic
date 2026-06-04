package com.boticlic.boticlic.controller;

import com.boticlic.boticlic.model.Usuario;
import com.boticlic.boticlic.repository.UsuarioRepository;
import com.boticlic.boticlic.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

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

        // ✅ Buscar usuario por email
        Optional<Usuario> optUsuario = usuarioRepository.findByEmail(email);

        // ✅ Si no existe el email
        if (optUsuario.isEmpty()) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Correo o contraseña incorrectos."));
        }

        Usuario u = optUsuario.get();

        // ✅ Si la contraseña es incorrecta
        if (!passwordEncoder.matches(password, u.getPassword())) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Correo o contraseña incorrectos."));
        }

        // ✅ Si la cuenta está desactivada
        if (!u.isActivo()) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "Tu cuenta está desactivada. Contacta al administrador."));
        }

        // ✅ Todo OK — generar token
        return ResponseEntity.ok(Map.of(
                "token",  jwtUtil.generarToken(u.getEmail(), u.getRol()),
                "rol",    u.getRol(),
                "nombre", u.getNombre(),
                "id",     u.getId()
        ));
    }
}