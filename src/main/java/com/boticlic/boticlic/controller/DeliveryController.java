package com.boticlic.boticlic.controller;

import com.boticlic.boticlic.model.Delivery;
import com.boticlic.boticlic.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping("/asignar")
    public ResponseEntity<Delivery> asignar(
            @RequestParam Long pedidoId,
            @RequestParam Long repartidorId) {
        return ResponseEntity.ok(deliveryService.asignarRepartidor(pedidoId, repartidorId));
    }

    @GetMapping("/repartidor/{repartidorId}")
    public ResponseEntity<List<Delivery>> porRepartidor(@PathVariable Long repartidorId) {
        return ResponseEntity.ok(deliveryService.listarPorRepartidor(repartidorId));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Delivery> actualizarEstado(
            @PathVariable Long id,
            @RequestParam String estado,
            @RequestParam(required = false) Double latitud,
            @RequestParam(required = false) Double longitud) {
        return ResponseEntity.ok(deliveryService.actualizarEstado(id, estado, latitud, longitud));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Delivery>> porEstado(@PathVariable String estado) {
        return ResponseEntity.ok(deliveryService.listarPorEstado(estado));
    }
}