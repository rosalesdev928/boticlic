package com.boticlic.boticlic.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.Map;

// @RestControllerAdvice intercepta TODAS las excepciones que lanzan
// los controllers y services, y las convierte en respuestas JSON limpias
// en lugar del HTML de error 500 que devuelve Spring por defecto.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ─────────────────────────────────────────────
    // RuntimeException — el más común en el proyecto
    // Cubre: "Producto no encontrado", "Stock insuficiente",
    //        "Usuario no encontrado", "Receta no encontrada", etc.
    // ─────────────────────────────────────────────
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        // Casos que deben devolver 404 en lugar de 400
        String msg = ex.getMessage();
        if (msg != null && (
                msg.contains("no encontrado") ||
                        msg.contains("no encontrada") ||
                        msg.contains("not found"))) {
            status = HttpStatus.NOT_FOUND;
        }

        return ResponseEntity.status(status).body(Map.of(
                "error",     msg != null ? msg : "Error inesperado",
                "status",    status.value(),
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    // ─────────────────────────────────────────────
    // Archivo demasiado grande al subir receta o imagen
    // Spring lanza esto cuando supera el límite del multipart
    // ─────────────────────────────────────────────
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleMaxUpload(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(Map.of(
                "error",     "El archivo es demasiado grande. Máximo permitido: 10MB",
                "status",    413,
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    // ─────────────────────────────────────────────
    // IllegalArgumentException — argumentos inválidos
    // ─────────────────────────────────────────────
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArg(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error",     ex.getMessage() != null ? ex.getMessage() : "Argumento inválido",
                "status",    400,
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    // ─────────────────────────────────────────────
    // Cualquier otra excepción no prevista
    // Evita que Spring devuelva stacktrace en HTML al cliente
    // ─────────────────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        // Imprime en consola para que el desarrollador vea el detalle real
        System.err.println("❌ Error no controlado: " + ex.getClass().getSimpleName()
                + " — " + ex.getMessage());
        ex.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error",     "Ocurrió un error interno. Intenta de nuevo.",
                "status",    500,
                "timestamp", LocalDateTime.now().toString()
        ));
    }
}