package com.boticlic.boticlic.service;

import org.springframework.stereotype.Service;

@Service
public class NotificacionService {

    public void enviarNotificacion(String email, String asunto, String mensaje) {
        // Por ahora solo imprime en consola
        // Más adelante se puede integrar con SMTP
        System.out.println("=== NOTIFICACION ===");
        System.out.println("Para: " + email);
        System.out.println("Asunto: " + asunto);
        System.out.println("Mensaje: " + mensaje);
        System.out.println("====================");
    }

    public void notificarCambioDEstado(String email, String numeroPedido, String estado) {
        String mensaje = "Tu pedido #" + numeroPedido + " ahora está en estado: " + estado;
        enviarNotificacion(email, "Actualización de tu pedido BotiClic", mensaje);
    }

    public void notificarPedidoConfirmado(String email, Long pedidoId, Double total) {
        String mensaje = "Tu pedido #" + pedidoId + " fue confirmado. Total: S/ " + total;
        enviarNotificacion(email, "Pedido confirmado - BotiClic", mensaje);
    }
}