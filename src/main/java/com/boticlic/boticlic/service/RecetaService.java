package com.boticlic.boticlic.service;

import com.boticlic.boticlic.model.DetallePedido;
import com.boticlic.boticlic.model.Pedido;
import com.boticlic.boticlic.model.Producto;
import com.boticlic.boticlic.model.Receta;
import com.boticlic.boticlic.model.Usuario;
import com.boticlic.boticlic.repository.PedidoRepository;
import com.boticlic.boticlic.repository.ProductoRepository;
import com.boticlic.boticlic.repository.RecetaRepository;
import com.boticlic.boticlic.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecetaService {

    private final RecetaRepository recetaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final NotificacionService notificacionService;

    public Receta guardarReceta(MultipartFile archivo, Long usuarioId, Long pedidoId, String notas) throws IOException {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Receta receta = new Receta();
        receta.setUsuario(usuario);
        receta.setArchivo(archivo.getBytes());
        receta.setNombreArchivo(archivo.getOriginalFilename());
        receta.setTipoArchivo(archivo.getContentType());
        receta.setNotas(notas);
        receta.setEstado("PENDIENTE");
        receta.setFechaEnvio(LocalDateTime.now());

        if (pedidoId != null) {
            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
            receta.setPedido(pedido);
        }

        return recetaRepository.save(receta);
    }

    public List<Receta> listarPendientes() {
        return recetaRepository.findByEstadoOrderByFechaEnvioDesc("PENDIENTE");
    }

    public List<Receta> listarTodas() {
        return recetaRepository.findAll();
    }

    public List<Receta> listarPorUsuario(Long usuarioId) {
        return recetaRepository.findByUsuarioId(usuarioId);
    }

    public Receta aprobar(Long id) {
        Receta receta = recetaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));

        receta.setEstado("APROBADA");
        receta.setFechaRevision(LocalDateTime.now());

        if (receta.getPedido() != null) {
            Long pedidoId = receta.getPedido().getId();
            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
            pedido.setEstado("CONFIRMADO");
            pedidoRepository.save(pedido);
        }

        Receta guardada = recetaRepository.save(receta);

        try {
            notificacionService.notificarCambioDEstado(
                    receta.getUsuario().getEmail(),
                    receta.getPedido() != null ? String.valueOf(receta.getPedido().getId()) : "N/A",
                    "RECETA_APROBADA"
            );
        } catch (Exception ignored) {}

        return guardada;
    }

    public Receta rechazar(Long id, String motivo) {
        Receta receta = recetaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));

        receta.setEstado("RECHAZADA");
        receta.setFechaRevision(LocalDateTime.now());
        receta.setMotivoRechazo(motivo != null ? motivo : "Receta no válida");

        if (receta.getPedido() != null) {
            Long pedidoId = receta.getPedido().getId();
            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

            // ✅ RESTAURAR STOCK de cada producto del pedido
            for (DetallePedido detalle : pedido.getDetalles()) {
                Producto producto = productoRepository.findById(detalle.getProducto().getId())
                        .orElse(null);
                if (producto != null) {
                    producto.setStock(producto.getStock() + detalle.getCantidad());
                    productoRepository.save(producto);
                    System.out.println("✅ Stock restaurado: " + producto.getNombre()
                            + " +"+detalle.getCantidad()+" = "+producto.getStock());
                }
            }

            pedido.setEstado("RECHAZADO");
            pedidoRepository.save(pedido);
            System.out.println("✅ Pedido #" + pedidoId + " cambiado a RECHAZADO");
        } else {
            System.out.println("⚠️ Receta #" + id + " sin pedido asociado");
        }

        Receta guardada = recetaRepository.save(receta);

        try {
            notificacionService.notificarCambioDEstado(
                    receta.getUsuario().getEmail(),
                    receta.getPedido() != null ? String.valueOf(receta.getPedido().getId()) : "N/A",
                    "RECETA_RECHAZADA"
            );
        } catch (Exception ignored) {}

        return guardada;
    }

    public Receta buscarPorId(Long id) {
        return recetaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));
    }
}