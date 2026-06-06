package com.boticlic.boticlic.service;

import com.boticlic.boticlic.model.DetallePedido;
import com.boticlic.boticlic.model.Pedido;
import com.boticlic.boticlic.model.Producto;
import com.boticlic.boticlic.model.Usuario;
import com.boticlic.boticlic.repository.PedidoRepository;
import com.boticlic.boticlic.repository.ProductoRepository;
import com.boticlic.boticlic.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionService notificacionService;

    // ✅ @Transactional: si algo falla a mitad (ej: error al descontar stock
    // del producto 3 de 5), hace rollback automático y NADA queda guardado.
    // Sin esto el stock puede quedar corrupto de forma silenciosa.
    @Transactional
    public Pedido crearPedido(Pedido pedido) {

        // Cargar el usuario completo desde BD
        Usuario usuarioCompleto = usuarioRepository.findById(pedido.getUsuario().getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        pedido.setUsuario(usuarioCompleto);

        double total = 0;
        for (DetallePedido detalle : pedido.getDetalles()) {
            Producto producto = productoRepository.findById(detalle.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + detalle.getProducto().getId()));

            if (producto.getStock() < detalle.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para: " + producto.getNombre()
                        + " (disponible: " + producto.getStock() + ", solicitado: " + detalle.getCantidad() + ")");
            }

            // Descontar stock — si algo falla después el @Transactional revierte todo
            producto.setStock(producto.getStock() - detalle.getCantidad());
            productoRepository.save(producto);

            detalle.setPedido(pedido);
            detalle.setPrecioUnitario(producto.getPrecio());
            total += detalle.getSubtotal();
        }

        pedido.setTotal(total);

        if (pedido.getEstado() == null || pedido.getEstado().isBlank()) {
            pedido.setEstado("PENDIENTE");
        }

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // Notificar solo si es pedido online (no venta en mostrador)
        // La notificación va FUERA de la transacción crítica a propósito:
        // si el email falla, el pedido igual quedó guardado correctamente.
        if (!"VENTA_MOSTRADOR".equals(pedidoGuardado.getEstado())) {
            notificacionService.notificarPedidoConfirmado(
                    usuarioCompleto.getEmail(),
                    pedidoGuardado.getId(),
                    pedidoGuardado.getTotal()
            );
        }

        return pedidoGuardado;
    }

    public List<Pedido> listarPorUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioId(usuarioId);
    }

    public List<Pedido> listarPorEstado(String estado) {
        return pedidoRepository.findByEstado(estado);
    }

    // ✅ @Transactional: actualiza estado del pedido + dispara notificación
    // como una sola unidad. Si la notificación falla, el estado igual se guarda
    // (la notificación está en try/catch propio en caso de querer aislarla).
    @Transactional
    public Pedido actualizarEstado(Long id, String estado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        pedido.setEstado(estado);
        Pedido actualizado = pedidoRepository.save(pedido);

        notificacionService.notificarCambioDEstado(
                pedido.getUsuario().getEmail(),
                String.valueOf(pedido.getId()),
                estado
        );
        return actualizado;
    }

    public Pedido buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
    }

    public List<Pedido> historialPorUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioId(usuarioId);
    }
}