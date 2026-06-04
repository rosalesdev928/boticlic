package com.boticlic.boticlic.controller;

import com.boticlic.boticlic.model.Receta;
import com.boticlic.boticlic.service.RecetaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recetas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RecetaController {

    private final RecetaService recetaService;

    // ✅ CLIENTE — Subir receta
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Receta> subir(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("usuarioId") Long usuarioId,
            @RequestParam(value = "pedidoId", required = false) Long pedidoId,
            @RequestParam(value = "notas", required = false) String notas) throws IOException {

        if (archivo.isEmpty()) return ResponseEntity.badRequest().build();

        String contentType = archivo.getContentType();
        if (contentType == null || (!contentType.startsWith("image/") && !contentType.equals("application/pdf")))
            return ResponseEntity.badRequest().build();

        if (archivo.getSize() > 10 * 1024 * 1024) return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(recetaService.guardarReceta(archivo, usuarioId, pedidoId, notas));
    }

    // ✅ ADMIN / FARMACEUTICO — Listar recetas pendientes
    @GetMapping("/pendientes")
    public ResponseEntity<List<Receta>> listarPendientes() {
        return ResponseEntity.ok(recetaService.listarPendientes());
    }

    // ✅ ADMIN / FARMACEUTICO — Listar todas
    @GetMapping
    public ResponseEntity<List<Receta>> listarTodas() {
        return ResponseEntity.ok(recetaService.listarTodas());
    }

    // ✅ CLIENTE — Ver sus recetas
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Receta>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(recetaService.listarPorUsuario(usuarioId));
    }

    // ✅ ADMIN / FARMACEUTICO — Aprobar receta
    @PutMapping("/{id}/aprobar")
    public ResponseEntity<Receta> aprobar(@PathVariable Long id) {
        return ResponseEntity.ok(recetaService.aprobar(id));
    }

    // ✅ ADMIN / FARMACEUTICO — Rechazar receta
    @PutMapping("/{id}/rechazar")
    public ResponseEntity<Receta> rechazar(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String motivo = body != null ? body.get("motivo") : null;
        return ResponseEntity.ok(recetaService.rechazar(id, motivo));
    }

    // ✅ ADMIN / FARMACEUTICO — Descargar / ver archivo
    @GetMapping("/{id}/archivo")
    public ResponseEntity<byte[]> descargarArchivo(@PathVariable Long id) {
        Receta receta = recetaService.buscarPorId(id);

        if (receta.getArchivo() == null) return ResponseEntity.notFound().build();

        String contentType = receta.getTipoArchivo() != null
                ? receta.getTipoArchivo() : "application/octet-stream";
        String filename = receta.getNombreArchivo() != null
                ? receta.getNombreArchivo() : "receta_" + id;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(receta.getArchivo());
    }
}