package com.boticlic.boticlic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final JavaMailSender mailSender;

    // Correo desde el que se envían los emails
    // Se lee desde application.properties → spring.mail.username
    @Value("${spring.mail.username}")
    private String emailRemitente;

    // ─────────────────────────────────────────────
    // Método base — construye y envía el email HTML
    // @Async hace que el envío sea en segundo plano:
    // si el email tarda 2 segundos, el usuario NO espera
    // ─────────────────────────────────────────────
    @Async
    public void enviarNotificacion(String emailDestino, String asunto, String cuerpoHtml) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setFrom(emailRemitente, "BotiClic Farmacia");
            helper.setTo(emailDestino);
            helper.setSubject(asunto);
            helper.setText(cuerpoHtml, true); // true = es HTML

            mailSender.send(mensaje);
            System.out.println("✅ Email enviado a: " + emailDestino + " | Asunto: " + asunto);

        } catch (Exception e) {
            // Si falla el email, NO interrumpimos el flujo del pedido.
            // Solo registramos el error en consola.
            System.err.println("⚠️ No se pudo enviar email a " + emailDestino + ": " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────
    // Email: pedido confirmado
    // Se envía cuando el cliente hace un pedido exitoso
    // ─────────────────────────────────────────────
    public void notificarPedidoConfirmado(String email, Long pedidoId, Double total) {
        String asunto = "✅ Pedido #" + pedidoId + " confirmado — BotiClic";
        String html = """
            <div style="font-family:Arial,sans-serif;max-width:560px;margin:0 auto;background:#f9f9f9;border-radius:10px;overflow:hidden">
              <div style="background:#1b5e20;padding:28px 30px;text-align:center">
                <h1 style="color:white;margin:0;font-size:22px">💊 BotiClic</h1>
                <p style="color:rgba(255,255,255,.85);margin:6px 0 0;font-size:14px">Tu farmacia digital de confianza</p>
              </div>
              <div style="padding:30px">
                <h2 style="color:#1b5e20;margin-top:0">¡Pedido confirmado!</h2>
                <p style="color:#444;font-size:15px;line-height:1.6">
                  Tu pedido <strong>#%s</strong> ha sido recibido y está siendo preparado.
                </p>
                <div style="background:#e8f5e9;border-radius:8px;padding:16px 20px;margin:20px 0">
                  <p style="margin:0;font-size:14px;color:#555">Total del pedido</p>
                  <p style="margin:4px 0 0;font-size:24px;font-weight:700;color:#1b5e20">S/ %.2f</p>
                </div>
                <p style="color:#666;font-size:13px;line-height:1.6">
                  📦 Recibirás una notificación cuando tu pedido esté en camino.<br>
                  🕐 Entrega estimada: menos de 24 horas.
                </p>
                <p style="color:#999;font-size:12px;margin-top:24px;border-top:1px solid #eee;padding-top:16px">
                  BotiClic · Lima, Perú · hola@boticlic.com
                </p>
              </div>
            </div>
            """.formatted(pedidoId, total);

        enviarNotificacion(email, asunto, html);
    }

    // ─────────────────────────────────────────────
    // Email: cambio de estado del pedido
    // Se envía cuando admin/farmacéutico cambia el estado
    // ─────────────────────────────────────────────
    public void notificarCambioDEstado(String email, String numeroPedido, String estado) {
        // Traducir el estado técnico a texto amigable
        String estadoTexto = switch (estado) {
            case "CONFIRMADO"       -> "✅ Confirmado — estamos preparando tu pedido";
            case "EN_PREPARACION"   -> "🔧 En preparación — tu pedido está siendo armado";
            case "EN_CAMINO"        -> "🚚 En camino — tu pedido está en ruta";
            case "ENTREGADO"        -> "🎉 Entregado — ¡gracias por tu compra!";
            case "RECHAZADO"        -> "❌ Rechazado — hubo un problema con tu pedido";
            case "RECETA_APROBADA"  -> "✅ Receta aprobada — tu pedido puede continuar";
            case "RECETA_RECHAZADA" -> "❌ Receta rechazada — por favor contáctanos";
            default                 -> estado;
        };

        String asunto = "📦 Actualización de tu pedido #" + numeroPedido + " — BotiClic";
        String html = """
            <div style="font-family:Arial,sans-serif;max-width:560px;margin:0 auto;background:#f9f9f9;border-radius:10px;overflow:hidden">
              <div style="background:#1b5e20;padding:28px 30px;text-align:center">
                <h1 style="color:white;margin:0;font-size:22px">💊 BotiClic</h1>
                <p style="color:rgba(255,255,255,.85);margin:6px 0 0;font-size:14px">Tu farmacia digital de confianza</p>
              </div>
              <div style="padding:30px">
                <h2 style="color:#1b5e20;margin-top:0">Actualización de tu pedido</h2>
                <p style="color:#444;font-size:15px;line-height:1.6">
                  Tu pedido <strong>#%s</strong> ha cambiado de estado:
                </p>
                <div style="background:#e8f5e9;border-radius:8px;padding:16px 20px;margin:20px 0;font-size:16px;font-weight:600;color:#1b5e20">
                  %s
                </div>
                <p style="color:#666;font-size:13px;line-height:1.6">
                  Si tienes alguna pregunta, escríbenos por WhatsApp al <strong>+51 933 406 222</strong>
                  o al correo <strong>hola@boticlic.com</strong>.
                </p>
                <p style="color:#999;font-size:12px;margin-top:24px;border-top:1px solid #eee;padding-top:16px">
                  BotiClic · Lima, Perú · hola@boticlic.com
                </p>
              </div>
            </div>
            """.formatted(numeroPedido, estadoTexto);

        enviarNotificacion(email, asunto, html);
    }
}