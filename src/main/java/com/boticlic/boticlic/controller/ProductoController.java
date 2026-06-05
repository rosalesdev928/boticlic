package com.boticlic.boticlic.controller;

import com.boticlic.boticlic.model.Producto;
import com.boticlic.boticlic.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<Producto>> listar() {
        return ResponseEntity.ok(productoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> buscarPorId(@PathVariable Long id) {
        return productoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscar(@RequestParam String nombre) {
        return ResponseEntity.ok(productoService.buscarPorNombre(nombre));
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Producto>> porCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(productoService.buscarPorCategoria(categoria));
    }

    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto producto) {
        return ResponseEntity.ok(productoService.guardar(producto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @RequestBody Producto datos) {
        return ResponseEntity.ok(productoService.actualizar(id, datos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stock-bajo")
    public ResponseEntity<List<Producto>> stockBajo() {
        return ResponseEntity.ok(productoService.productosConStockBajo());
    }
    // ✅ Subir imagen de producto
    @PostMapping("/{id}/imagen")
    public ResponseEntity<?> subirImagen(
            @PathVariable Long id,
            @RequestParam("imagen") MultipartFile archivo) {
        try {
            // Validar que sea imagen
            String tipo = archivo.getContentType();
            if (tipo == null || !tipo.startsWith("image/")) {
                return ResponseEntity.badRequest().body("Solo se aceptan imágenes");
            }
            // Validar tamaño (máximo 5MB)
            if (archivo.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body("La imagen no puede superar 5MB");
            }
            // Crear carpeta si no existe
            String uploadDir = "src/main/resources/static/img/productos/";
            java.nio.file.Path dirPath = java.nio.file.Paths.get(uploadDir);
            if (!java.nio.file.Files.exists(dirPath)) {
                java.nio.file.Files.createDirectories(dirPath);
            }
            // Guardar con nombre único
            String ext = archivo.getOriginalFilename() != null
                    ? archivo.getOriginalFilename().substring(archivo.getOriginalFilename().lastIndexOf('.'))
                    : ".jpg";
            String nombreArchivo = "producto_" + id + "_" + System.currentTimeMillis() + ext;
            java.nio.file.Path rutaArchivo = dirPath.resolve(nombreArchivo);
            java.nio.file.Files.write(rutaArchivo, archivo.getBytes());
            // Actualizar URL en la base de datos
            Producto producto = productoService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            String urlImagen = "/img/productos/" + nombreArchivo;
            producto.setImagen(urlImagen);
            productoService.guardar(producto);
            return ResponseEntity.ok(java.util.Map.of(
                    "url", urlImagen,
                    "mensaje", "Imagen subida correctamente"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error al subir imagen: " + e.getMessage());
        }
    }

}