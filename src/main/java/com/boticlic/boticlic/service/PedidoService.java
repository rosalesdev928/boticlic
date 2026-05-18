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
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionService notificacionService;

    public Pedido crearPedido(Pedido pedido) {

        // ✅ Cargar el usuario completo desde BD (el frontend solo manda {id})
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

            // Descontar stock
            producto.setStock(producto.getStock() - detalle.getCantidad());
            productoRepository.save(producto);

            // Establecer referencias
            detalle.setPedido(pedido);
            detalle.setPrecioUnitario(producto.getPrecio());

            total += detalle.getSubtotal();
        }

        pedido.setTotal(total);
        pedido.setEstado("PENDIENTE");

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // ✅ Ahora sí tenemos el email del usuario completo
        notificacionService.notificarPedidoConfirmado(
                usuarioCompleto.getEmail(),
                pedidoGuardado.getId(),
                pedidoGuardado.getTotal()
        );

        return pedidoGuardado;
    }

    public List<Pedido> listarPorUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioId(usuarioId);
    }

    public List<Pedido> listarPorEstado(String estado) {
        return pedidoRepository.findByEstado(estado);
    }

    public Pedido actualizarEstado(Long id, String estado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        pedido.setEstado(estado);
        Pedido actualizado = pedidoRepository.save(pedido);

        // ✅ El pedido ya tiene el usuario completo cargado por JPA
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