package com.boticlic.boticlic.service;

import com.boticlic.boticlic.model.Delivery;
import com.boticlic.boticlic.model.Pedido;
import com.boticlic.boticlic.model.Usuario;
import com.boticlic.boticlic.repository.DeliveryRepository;
import com.boticlic.boticlic.repository.PedidoRepository;
import com.boticlic.boticlic.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionService notificacionService;

    public Delivery asignarRepartidor(Long pedidoId, Long repartidorId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        Usuario repartidor = usuarioRepository.findById(repartidorId)
                .orElseThrow(() -> new RuntimeException("Repartidor no encontrado"));

        // ✅ Verificar que el repartidor tenga rol DELIVERY
        if (!"DELIVERY".equals(repartidor.getRol())) {
            throw new RuntimeException("El usuario no es un repartidor");
        }

        // ✅ Verificar que el pedido no tenga ya un delivery asignado
        if (deliveryRepository.findByPedidoId(pedidoId).isPresent()) {
            throw new RuntimeException("Este pedido ya tiene un repartidor asignado");
        }

        // ✅ Solo pedidos CONFIRMADOS pueden asignarse
        if (!"CONFIRMADO".equals(pedido.getEstado())) {
            throw new RuntimeException("Solo se pueden asignar pedidos en estado CONFIRMADO");
        }

        Delivery delivery = new Delivery();
        delivery.setPedido(pedido);
        delivery.setRepartidor(repartidor);
        delivery.setEstado("ASIGNADO");
        delivery.setFechaAsignacion(LocalDateTime.now());
        delivery.setDireccionEntrega(pedido.getDireccionEntrega());

        // ✅ CORREGIDO: el pedido NO cambia de estado al asignar
        // Se mantiene en CONFIRMADO hasta que el repartidor salga
        // pedido.setEstado("EN_CAMINO"); ← ESTO ERA EL BUG

        return deliveryRepository.save(delivery);
    }

    public List<Delivery> listarPorRepartidor(Long repartidorId) {
        return deliveryRepository.findByRepartidorId(repartidorId);
    }

    public Delivery actualizarEstado(Long id, String estado, Double latitud, Double longitud) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery no encontrado"));

        delivery.setEstado(estado);

        if (latitud != null) delivery.setLatitud(latitud);
        if (longitud != null) delivery.setLongitud(longitud);

        // ✅ CORREGIDO: el pedido cambia de estado según el delivery
        if ("EN_CAMINO".equals(estado)) {
            // Repartidor confirmó que salió → pedido pasa a EN_CAMINO
            Pedido pedido = pedidoRepository.findById(delivery.getPedido().getId())
                    .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
            pedido.setEstado("EN_CAMINO");
            pedidoRepository.save(pedido);
            System.out.println("🚚 Pedido #" + pedido.getId() + " → EN_CAMINO");
        }

        if ("ENTREGADO".equals(estado)) {
            delivery.setFechaEntrega(LocalDateTime.now());

            // Pedido pasa a ENTREGADO
            Pedido pedido = pedidoRepository.findById(delivery.getPedido().getId())
                    .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
            pedido.setEstado("ENTREGADO");
            pedidoRepository.save(pedido);
            System.out.println("✅ Pedido #" + pedido.getId() + " → ENTREGADO");

            // Notificar al cliente
            try {
                notificacionService.notificarCambioDEstado(
                        pedido.getUsuario().getEmail(),
                        String.valueOf(pedido.getId()),
                        "ENTREGADO"
                );
            } catch (Exception ignored) {}
        }

        return deliveryRepository.save(delivery);
    }

    public List<Delivery> listarPorEstado(String estado) {
        return deliveryRepository.findByEstado(estado);
    }
}