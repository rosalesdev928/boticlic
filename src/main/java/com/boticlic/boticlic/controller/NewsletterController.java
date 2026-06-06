package com.boticlic.boticlic.controller;

import com.boticlic.boticlic.model.Suscriptor;
import com.boticlic.boticlic.repository.SuscriptorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/newsletter")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NewsletterController {

    private final SuscriptorRepository suscriptorRepository;

    // ─────────────────────────────────────────────
    // POST /api/newsletter/suscribir
    // Lo llama el frontend cuando el usuario escribe su correo
    // en el footer y hace clic en "Suscribirme"
    // ─────────────────────────────────────────────
    @PostMapping("/suscribir")
    public ResponseEntity<?> suscribir(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        // Validar que venga un correo
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El correo es obligatorio"));
        }

        // Validar formato básico del correo
        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El correo no tiene un formato válido"));
        }

        // Si ya estaba suscrito, devolver 409 (el frontend lo trata como éxito)
        if (suscriptorRepository.existsByEmail(email)) {
            return ResponseEntity.status(409)
                    .body(Map.of("mensaje", "Este correo ya está suscrito"));
        }

        // Guardar el nuevo suscriptor
        Suscriptor nuevo = new Suscriptor();
        nuevo.setEmail(email);
        suscriptorRepository.save(nuevo);

        return ResponseEntity.ok(
                Map.of("mensaje", "¡Suscrito correctamente! Recibirás nuestras ofertas pronto.")
        );
    }

    // ─────────────────────────────────────────────
    // GET /api/newsletter/suscriptores
    // Solo accesible por ADMIN — para ver la lista de correos
    // desde el panel de administración
    // ─────────────────────────────────────────────
    @GetMapping("/suscriptores")
    public ResponseEntity<List<Suscriptor>> listar() {
        return ResponseEntity.ok(suscriptorRepository.findByActivoTrue());
    }

    // ─────────────────────────────────────────────
    // DELETE /api/newsletter/desuscribir?email=...
    // Para que un usuario pueda quitarse de la lista
    // ─────────────────────────────────────────────
    @DeleteMapping("/desuscribir")
    public ResponseEntity<?> desuscribir(@RequestParam String email) {
        return suscriptorRepository.findByEmail(email)
                .map(s -> {
                    s.setActivo(false);
                    suscriptorRepository.save(s);
                    return ResponseEntity.ok(
                            Map.of("mensaje", "Te has desuscrito correctamente")
                    );
                })
                .orElse(ResponseEntity.notFound().build());
    }
}