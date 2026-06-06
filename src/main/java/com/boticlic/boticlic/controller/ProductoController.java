package com.boticlic.boticlic.controller;

import com.boticlic.boticlic.model.Producto;
import com.boticlic.boticlic.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    // ✅ Ruta de uploads leída desde application.properties
    // En Windows será: C:/boticlic-uploads/productos/
    // En Linux/Mac:    /var/boticlic-uploads/productos/
    // Si no se configura, usa una carpeta "uploads" en el directorio donde corre el .jar
    @Value("${app.upload.dir:uploads/productos}")
    private String uploadDir;

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

    // ✅ Subir imagen — ahora se guarda FUERA del proyecto
    //    en la carpeta definida por app.upload.dir
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

            // Validar tamaño máximo 5MB
            if (archivo.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body("La imagen no puede superar 5MB");
            }

            // Crear carpeta externa si no existe
            Path dirPath = Paths.get(uploadDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            // Nombre único para evitar colisiones
            String ext = "";
            String original = archivo.getOriginalFilename();
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf('.'));
            } else {
                ext = ".jpg";
            }
            String nombreArchivo = "producto_" + id + "_" + System.currentTimeMillis() + ext;
            Path rutaArchivo = dirPath.resolve(nombreArchivo);
            Files.write(rutaArchivo, archivo.getBytes());

            // La URL que se guarda en BD apunta al endpoint /uploads/productos/
            // que Spring sirve como recurso estático (configurado en WebConfig)
            String urlImagen = "/uploads/productos/" + nombreArchivo;

            Producto producto = productoService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            producto.setImagen(urlImagen);
            productoService.guardar(producto);

            return ResponseEntity.ok(Map.of(
                    "url", urlImagen,
                    "mensaje", "Imagen subida correctamente"
            ));

        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("Error al guardar la imagen: " + e.getMessage());
        }
    }
}